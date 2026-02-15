package br.com.labinfofood.pagamentos.service;

import br.com.labinfofood.pagamentos.dto.PagamentoDto;
import br.com.labinfofood.pagamentos.model.Pagamento;
import br.com.labinfofood.pagamentos.repository.PagamentoRespository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private final PagamentoRespository  pagamentoRespository;
    private final ModelMapper mapper;

    public PagamentoService(PagamentoRespository pagamentoRespository, ModelMapper mapper) {
        this.pagamentoRespository = pagamentoRespository;
        this.mapper = mapper;
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

}
