package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.Fornecedor;

public interface CotacaoItemRepository extends BaseRepository<CotacaoItem>{

	//@formatter:off
	@Query(value = "select cotacaoItem "
			+ "       from CotacaoItem cotacaoItem "
			+ "      inner join fetch cotacaoItem.produto "
			+ "      where cotacaoItem.id = :id ")
	public Optional<CotacaoItem> findById(@Param("id") Long id);
	
	@Query(value = "select cotacaoItem "
			+ "       from CotacaoItem cotacaoItem "
			+ "      inner join fetch cotacaoItem.produto "
			+ "      where cotacaoItem.cotacao = :cotacao ")
	public List<CotacaoItem> findByCotacao(@Param("cotacao") Cotacao cotacao);
	
	@Query(value = "select cotacaoItem "
			+ "       from CotacaoItem cotacaoItem "
			+ "      inner join fetch cotacaoItem.produto "
			+ "      where cotacaoItem.cotacao = :cotacao "
			+ "        and exists (select 1 "
			+ "                      from CotacaoItemFornecedor cotacaoItemFornecedor "
			+ "                     where cotacaoItemFornecedor.cotacaoItem = cotacaoItem) "
			+ "        and exists (select 1 "
			+ "                      from CotacaoFornecedor cotacaoFornecedor "
			+ "                     where cotacaoFornecedor.cotacao = cotacaoItem.cotacao "
			+ "                       and cotacaoFornecedor.fornecedor = :fornecedor) ")
	
	public List<CotacaoItem> findByCotacaoResposta(@Param("cotacao") Cotacao cotacao, @Param("fornecedor") Fornecedor fornecedor);

	//@formatter:on
	
}