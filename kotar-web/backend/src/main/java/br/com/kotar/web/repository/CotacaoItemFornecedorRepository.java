package br.com.kotar.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemFornecedor;

public interface CotacaoItemFornecedorRepository extends BaseRepository<CotacaoItemFornecedor>{
	
	//@formatter:off
	@Query(value = "select cotacaoItemFornecedor "
			+ "       from CotacaoItemFornecedor cotacaoItemFornecedor "
			+ "      inner join fetch cotacaoItemFornecedor.cotacaoItem cotacaoItem "
			+ "      inner join fetch cotacaoItemFornecedor.cotacaoFornecedor cotacaoFornecedor " 
			+ "      inner join fetch cotacaoItem.produto "
			+ "      where cotacaoItem.cotacao = :cotacao "
			+ "        and cotacaoItemFornecedor.cotacaoFornecedor = :cotacaoFornecedor ")
	public List<CotacaoItemFornecedor> findByCotacaoAndCotacaoFornecedor(@Param("cotacao") Cotacao cotacao, 
			@Param("cotacaoFornecedor") CotacaoFornecedor cotacaoFornecedor);
	
	
	@Query(value = "select cotacaoItemFornecedor "
			+ "       from CotacaoItemFornecedor cotacaoItemFornecedor "
			+ "      inner join fetch cotacaoItemFornecedor.cotacaoFornecedor cotacaoFornecedor " 
			+ "      inner join fetch cotacaoFornecedor.fornecedor  "
			+ "      where cotacaoItemFornecedor.cotacaoItem = :cotacaoItem "
			+ "        and cotacaoItemFornecedor.cotacaoFornecedor = :cotacaoFornecedor ")
	public CotacaoItemFornecedor findByCotacaoAndCotacaoItemFornecedorWithValues(@Param("cotacaoItem") CotacaoItem cotacaoItem, 
			@Param("cotacaoFornecedor") CotacaoFornecedor cotacaoFornecedor);
	
	@Query(value = "select cotacaoItemFornecedor "
			+ "       from CotacaoItemFornecedor cotacaoItemFornecedor "
			+ "      where cotacaoItemFornecedor.cotacaoFornecedor = :cotacaoFornecedor")
	public List<CotacaoItemFornecedor> findByCotacaoFornecedor(@Param("cotacaoFornecedor") CotacaoFornecedor cotacaoFornecedor);
	
	
	//@formatter:on
	
}