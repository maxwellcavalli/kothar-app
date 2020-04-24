package br.com.kotar.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorGrupoProduto;

public interface FornecedorGrupoProdutoRepository extends BaseRepository<FornecedorGrupoProduto>{

	//@formatter:off
	@Query(value = "select fornecedorGrupoProduto "
			+ "       from FornecedorGrupoProduto fornecedorGrupoProduto "
			+ "      inner join fetch fornecedorGrupoProduto.grupoProduto "
			+ "      inner join fetch fornecedorGrupoProduto.fornecedor "
			+ "      where fornecedorGrupoProduto.fornecedor = :fornecedor ",
			countQuery="select count(fornecedorGrupoProduto) "
					+ "       from FornecedorGrupoProduto fornecedorGrupoProduto "
					+ "      inner join fornecedorGrupoProduto.grupoProduto "
					+ "      inner join fornecedorGrupoProduto.fornecedor "
					+ "      where fornecedorGrupoProduto.fornecedor = :fornecedor ")
	public List<FornecedorGrupoProduto> findByFornecedor(@Param("fornecedor") Fornecedor fornecedor);
	
	//@formatter:on
	
}