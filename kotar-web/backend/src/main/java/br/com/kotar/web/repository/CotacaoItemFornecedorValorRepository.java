package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;

public interface CotacaoItemFornecedorValorRepository extends BaseRepository<CotacaoItemFornecedorValor>{
	
	//@formatter:off
	@Query(value = "select cotacaoItemFornecedorValor "
			+ "       from CotacaoItemFornecedorValor cotacaoItemFornecedorValor "
			+ "      inner join fetch cotacaoItemFornecedorValor.cotacaoItemFornecedor "			
			+ "      where cotacaoItemFornecedorValor.cotacaoItemFornecedor = :cotacaoItemFornecedor ")
	public List<CotacaoItemFornecedorValor> findByCotacaoItemFornecedor(@Param("cotacaoItemFornecedor") CotacaoItemFornecedor cotacaoItemFornecedor);
	
	@Query(value = "select cotacaoItemFornecedorValor "
			+ "       from CotacaoItemFornecedorValor cotacaoItemFornecedorValor "
			+ "      inner join fetch cotacaoItemFornecedorValor.cotacaoItemFornecedor cotacaoItemFornecedor "			
			+ "      where cotacaoItemFornecedor.cotacaoFornecedor= :cotacaoFornecedor ")
	public List<CotacaoItemFornecedorValor> findByCotacaoFornecedor(@Param("cotacaoFornecedor") CotacaoFornecedor cotacaoFornecedor);

	@Query(value = "select cotacaoItemFornecedorValor "
			+ "       from CotacaoItemFornecedorValor cotacaoItemFornecedorValor "
			+ "      inner join fetch cotacaoItemFornecedorValor.cotacaoItemFornecedor cotacaoItemFornecedor "
			+ "      inner join fetch cotacaoItemFornecedor.cotacaoItem cotacaoItem "
			+ "      inner join fetch cotacaoItem.cotacao cotacao "
			+ "      where cotacaoItemFornecedorValor.id = :id ")
	public Optional<CotacaoItemFornecedorValor> findById(@Param("id") Long id);
	//@formatter:on
	
}