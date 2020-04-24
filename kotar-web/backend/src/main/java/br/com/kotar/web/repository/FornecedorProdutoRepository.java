package br.com.kotar.web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorProduto;
import br.com.kotar.domain.business.Produto;

public interface FornecedorProdutoRepository extends BaseRepository<FornecedorProduto>{

	//@formatter:off
	@Query(value = "select fornecedorProduto "
			+ "       from FornecedorProduto fornecedorProduto "
			+ "      where fornecedorProduto.fornecedor = :fornecedor  "
			+ "        and fornecedorProduto.produto = :produto ")
	public FornecedorProduto findByFornecedorAndProduto(@Param("fornecedor") Fornecedor fornecedor, @Param("produto") Produto produto);
	
	@Query(value = "select fornecedorProduto "
			+ "       from FornecedorProduto fornecedorProduto "
			+ "      where fornecedorProduto.fornecedor = :fornecedor  "
			+ "        and fornecedorProduto.identificacao = :identificacao")
	public FornecedorProduto findByFornecedorAndIdentificacao(@Param("fornecedor") Fornecedor fornecedor, @Param("identificacao") String identificacao);
	
	
	//@formatter:on
	
}