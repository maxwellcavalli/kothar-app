package br.com.kotar.web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.FornecedorProduto;
import br.com.kotar.domain.business.FornecedorProdutoPreco;

public interface FornecedorProdutoPrecoRepository extends BaseRepository<FornecedorProdutoPreco>{

	//@formatter:off
	
	@Query(value = "select fornecedorProdutoPreco "
			+ "       from FornecedorProdutoPreco fornecedorProdutoPreco "
			+ "      where fornecedorProdutoPreco.id = (select max(fppM.id)"
			+ "                                           from FornecedorProdutoPreco fppM "
			+ "                                          where fppM.fornecedorProduto = :fornecedorProduto)   ")
	public FornecedorProdutoPreco findLastUpdate(@Param("fornecedorProduto") FornecedorProduto fornecedorProduto);
	//@formatter:on
	
}