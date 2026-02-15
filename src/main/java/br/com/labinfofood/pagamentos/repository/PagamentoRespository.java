package br.com.labinfofood.pagamentos.repository;

import br.com.labinfofood.pagamentos.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRespository extends JpaRepository<Pagamento, Long> {
}
