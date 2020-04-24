package br.com.kotar.web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Avaliacao;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.security.Usuario;

import java.util.Optional;

public interface AvaliacaoRepository extends BaseRepository<Avaliacao> {
	
	//@formatter:off
	@Query(value = "select avaliacao "
			+ "       from Avaliacao avaliacao "
			+ "      inner join fetch avaliacao.cotacaoFornecedor cotacaoFornecedor "
			+ "      inner join fetch cotacaoFornecedor.cotacao cotacao "		
			+ "      where avaliacao.id = :id ")
	public Optional<Avaliacao> findById(@Param("id") Long id);
	
	@Query(value = "select avaliacao "
			+ "       from Avaliacao avaliacao "
			+ "      inner join fetch avaliacao.cotacaoFornecedor cotacaoFornecedor "
			+ "      inner join fetch cotacaoFornecedor.cotacao cotacao "		
			+ "      inner join fetch cotacao.cliente cliente "			
			+ "      where cliente.usuario = :usuario ") 
	public Avaliacao findByUsuario(@Param("usuario") Usuario usuario);	
	
	@Query(value = "select avaliacao "
			+ "       from Avaliacao avaliacao "
			+ "      inner join fetch avaliacao.cotacaoFornecedor cotacaoFornecedor "
			+ "      inner join fetch cotacaoFornecedor.cotacao cotacao "		
			+ "      where cotacaoFornecedor.cotacao = :cotacao "
			+ "        and cotacaoFornecedor.vencedorByUser = 1")
	public Avaliacao findByCotacao(@Param("cotacao") Cotacao cotacao);

	@Query(value = " select count(1) "
			+ "        from Cotacao cotacao " 
			+ "   	 where cotacao.cliente = :cliente "
			+ " 	 and (select cotacaoFornecedor "
			+ "			    from CotacaoFornecedor cotacaoFornecedor "
			+ " 		     where cotacaoFornecedor.codigoAprovacaoVencedor is not null "
			+ " 		        and cotacaoFornecedor.vencedorByUser = 1 "
			+ " 		        and cotacaoFornecedor.cotacao = cotacao) is not null "
			+ "      and (select avaliacao "
			+ "          	from Avaliacao avaliacao, CotacaoFornecedor cotacaoFornecedor " 
			+ "        	where avaliacao.cotacaoFornecedor = cotacaoFornecedor " 
			+ "        	and cotacaoFornecedor.cotacao = cotacao) is null " 			
			+ "        	and (cotacao.dataLembreteAvaliacao is null or cotacao.dataLembreteAvaliacao < current_timestamp() + 2*86400) ")
	public Long findQuantidadeAvaliacoesPendentes(@Param("cliente") Cliente cliente);
	//@formatter:on	
}