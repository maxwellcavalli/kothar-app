package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemArquivo;

public interface CotacaoItemArquivoRepository extends BaseRepository<CotacaoItemArquivo>{

	//@formatter:off
	@Query(value = "select new br.com.kotar.domain.business.CotacaoItemArquivo( "
			+ "				cotacaoItemArquivo.id, cotacaoItemArquivo.nome, cotacaoItem ) "
			+ "       from CotacaoItemArquivo cotacaoItemArquivo "
			+ "      inner join cotacaoItemArquivo.cotacaoItem cotacaoItem "
			+ "      where cotacaoItemArquivo.cotacaoItem = :cotacaoItem ")
	public List<CotacaoItemArquivo> findByCotacaoItem(@Param("cotacaoItem") CotacaoItem cotacaoItem);

	@Query(value = "select  cotacaoItemArquivo "
			+ "       from CotacaoItemArquivo cotacaoItemArquivo "
			+ "      inner join cotacaoItemArquivo.cotacaoItem cotacaoItem "
			+ "      where cotacaoItemArquivo.cotacaoItem = :cotacaoItem ")
	public List<CotacaoItemArquivo> findByCotacaoItemWithFile(@Param("cotacaoItem") CotacaoItem cotacaoItem);
	
	
	@Query(value = "select cotacaoItemArquivo "
			+ "       from CotacaoItemArquivo cotacaoItemArquivo "
			+ "      inner join cotacaoItemArquivo.cotacaoItem cotacaoItem "
			+ "      where cotacaoItemArquivo.id = :id")
	public Optional<CotacaoItemArquivo> findById(@Param("id") Long id);
	
	//@formatter:on
	
}