package br.com.labinfofood.pagamentos.service;

import br.com.labinfofood.pagamentos.dto.PagamentoDto;
import br.com.labinfofood.pagamentos.http.PedidoClient;
import br.com.labinfofood.pagamentos.model.Pagamento;
import br.com.labinfofood.pagamentos.model.Status;
import br.com.labinfofood.pagamentos.repository.PagamentoRespository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PagamentoService {

    private final PagamentoRespository  pagamentoRespository;
    private final ModelMapper mapper;
    private final PedidoClient pedido;

    public PagamentoService(PagamentoRespository pagamentoRespository, ModelMapper mapper, PedidoClient pedido) {
        this.pagamentoRespository = pagamentoRespository;
        this.mapper = mapper;
        this.pedido = pedido;
    }


     public Page<PagamentoDto> obterTodos(Pageable pageable) {
         return pagamentoRespository.findAll(pageable)
                 .map(pagamento -> mapper.map(pagamento, PagamentoDto.class));
     }

     public PagamentoDto obterPorId(Long id) {
         var pagamento = pagamentoRespository.findById(id)
                 .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));
         return mapper.map(pagamento, PagamentoDto.class);
     }

     public PagamentoDto criarPagamento(PagamentoDto pagamentoDto) {
         var pagamento = mapper.map(pagamentoDto, br.com.labinfofood.pagamentos.model.Pagamento.class);
         pagamento.setStatus(br.com.labinfofood.pagamentos.model.Status.CRIADO);
         var pagamentoSalvo = pagamentoRespository.save(pagamento);
         return mapper.map(pagamentoSalvo, PagamentoDto.class);
     }


        public PagamentoDto atualizarPagamento(Long id, PagamentoDto pagamentoDto){
            Pagamento pagamento = mapper.map(pagamentoDto, Pagamento.class);
            pagamento.setId(id);
            Pagamento pagamentoAtualizado = pagamentoRespository.save(pagamento);
            return mapper.map(pagamentoAtualizado, PagamentoDto.class);
        }

        public void deletarPagamento(Long id) {
            pagamentoRespository.deleteById(id);
        }

    public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = pagamentoRespository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        pagamentoRespository.save(pagamento.get());
        pedido.atualizaPagamento(pagamento.get().getPedidoId());
    }

    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = pagamentoRespository.findById(id);

        if (!pagamento.isPresent()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        pagamentoRespository.save(pagamento.get());

    }
}
