package br.com.kotar.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.ClienteFornecedor;
import br.com.kotar.domain.business.Fornecedor;

public interface ClienteFornecedorRepository extends BaseRepository<ClienteFornecedor> {

	//@formatter:off
	@Query(value = "select clienteFornecedor "
				+ " from ClienteFornecedor clienteFornecedor "
				+ " where clienteFornecedor.cliente = :cliente "
				+ "   and clienteFornecedor.fornecedor = :fornecedor ")
	public ClienteFornecedor findByClienteAndFornecedor(@Param("cliente") Cliente cliente, 
			@Param("fornecedor") Fornecedor fornecedor);	
	
	@Query(value = "select clienteFornecedor "
			+ " from ClienteFornecedor clienteFornecedor "
			+ " where clienteFornecedor.cliente = :cliente ")
	public List<ClienteFornecedor> findByCliente(@Param("cliente") Cliente cliente);
	
	@Query(value = "select clienteFornecedor "
			+ "  from ClienteFornecedor clienteFornecedor "
			+ " inner join fetch clienteFornecedor.cliente "			
			+ " where clienteFornecedor.fornecedor  = :fornecedor ")
	public List<ClienteFornecedor> findByFornecedor(@Param("fornecedor") Fornecedor fornecedor);

	//@formatter:on

}