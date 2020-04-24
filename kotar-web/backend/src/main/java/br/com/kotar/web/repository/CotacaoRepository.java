package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;

public interface CotacaoRepository extends CrudRepository<Cotacao>{
	//@formatter:off
	@Query(value = "select cotacao "
			+ "       from Cotacao cotacao "
			+ "      inner join fetch cotacao.cliente "
			+ "       left join fetch cotacao.cotacaoEndereco cotacaoEndereco "
			+ "       left join fetch cotacaoEndereco.cep cep "
			+ "       left join fetch cep.bairro bairro "
			+ "       left join fetch bairro.cidade cidade "
			+ "       left join fetch cidade.estado "
			+ "       left join fetch cotacaoEndereco.enderecoComplemento "
			+ "      where cotacao.id = :id ")
	public Optional<Cotacao> findById(@Param("id") Long id);
	
	
	@Query(value = "select new br.com.kotar.domain.business.Cotacao("
			+ " 			cotacao.id, "
			+ "				(select count(cotacaoFornecedor.id) "
			+ "                from CotacaoFornecedor cotacaoFornecedor "
			+ "               where cotacaoFornecedor.cotacao = cotacao "
			+ "                 and cotacaoFornecedor.dataRecusa is not null ) )"
			+ "       from Cotacao cotacao "
			+ "      where cotacao.dataLimiteRetorno < current_timestamp "
			+ "        and cotacao.situacaoCotacao in ( :situacaoCotacao ) ")
	
	public List<Cotacao> findCotacaoExpirada(@Param("situacaoCotacao") List<SituacaoCotacaoType> situacaoCotacao);
	
	//@formatter:on
}