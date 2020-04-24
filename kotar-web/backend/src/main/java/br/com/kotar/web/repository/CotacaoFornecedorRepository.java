package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.Fornecedor;

public interface CotacaoFornecedorRepository extends BaseRepository<CotacaoFornecedor> {

	//@formatter:off
	@Query("  select cotacaoFornecedor "
		   + "  from CotacaoFornecedor cotacaoFornecedor "
		   + " where cotacaoFornecedor.cotacao = :cotacao "
		   + "   and cotacaoFornecedor.fornecedor = :fornecedor ")
	public CotacaoFornecedor findByCotacaoAndFornecedor(@Param("cotacao") Cotacao cotacao, @Param("fornecedor") Fornecedor fornecedor);

	@Query("  select cotacaoFornecedor "
		   + "  from CotacaoFornecedor cotacaoFornecedor "
		   + " where cotacaoFornecedor.cotacao = :cotacao ")
	public List<CotacaoFornecedor> findByCotacao(@Param("cotacao") Cotacao cotacao);
	
	@Query("  select count(cotacaoFornecedor) "
		   + "  from CotacaoFornecedor cotacaoFornecedor "
		   + " where cotacaoFornecedor.cotacao = :cotacao "
		   + "   and cotacaoFornecedor.dataRecusa is null "
		   + "   and cotacaoFornecedor.enviado = 1 ")
	public long countByCotacaoRespondida(@Param("cotacao") Cotacao cotacao);

	@Query("  select cotacaoFornecedor "
			+ "  from CotacaoFornecedor cotacaoFornecedor "
			+ " inner join fetch cotacaoFornecedor.cotacao "
			+ " inner join fetch cotacaoFornecedor.fornecedor fornecedor " 
			+ " where cotacaoFornecedor.id = :id ")
	public Optional<CotacaoFornecedor> findById(@Param("id") Long id);
	
	
	@Query("  select cotacaoFornecedor "
		   + "  from CotacaoFornecedor cotacaoFornecedor "
		   + " where cotacaoFornecedor.cotacao = :cotacao "
		   + "   and ( select count(1) "
		   + "           from CotacaoItem cotacaoItem "
		   + "          where cotacaoItem.cotacao = cotacaoFornecedor.cotacao) = (select count(1)"
		   + "                                                                      from CotacaoItemFornecedor cotacaoItemFornecedor "
		   + "                                                                     where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor "
		   + "                                                                       and exists (select 1 "
		   + "                                                                                     from CotacaoItemFornecedorValor cotacaoItemFornecedorValor "
		   + "                                                                                    where cotacaoItemFornecedorValor.cotacaoItemFornecedor = cotacaoItemFornecedor ) ) ")
	public List<CotacaoFornecedor> findByCotacaoGlobal(@Param("cotacao") Cotacao cotacao);

	@Query("  select cotacaoFornecedor "
			+ "  from CotacaoFornecedor cotacaoFornecedor "
			+ " inner join fetch cotacaoFornecedor.fornecedor "
			+ " where cotacaoFornecedor.codigoAprovacaoVencedor is not null "
			+ " and cotacaoFornecedor.vencedorByUser = 1 "
			+ " and cotacaoFornecedor.cotacao = :cotacao ")
	public CotacaoFornecedor findVencedorByCotacao(@Param("cotacao") Cotacao cotacao);	
	
	
	@Query("  select cotacaoFornecedor "
			+ "  from CotacaoFornecedor cotacaoFornecedor "
			+ " inner join fetch cotacaoFornecedor.cotacao "
			+ " where cotacaoFornecedor.codigoAprovacaoVencedor is not null "
			+ " and cotacaoFornecedor.vencedorByUser = 1 "
			+ " and cotacaoFornecedor.cotacao.cliente = :cliente ")
	public List<CotacaoFornecedor> findVencedorByCliente(@Param("cliente") Cliente cliente);
	
	
	@Query("  select cotacaoFornecedor "
			+ "  from CotacaoFornecedor cotacaoFornecedor "
			+ " inner join fetch cotacaoFornecedor.cotacao cotacao "
			+ " inner join fetch cotacao.cliente cliente "
			+ " inner join fetch cliente.usuario usuario "
			+ " inner join fetch cotacaoFornecedor.fornecedor fornecedor "
			+ " where cotacaoFornecedor.enviado = 1 "
			+ " and cotacaoFornecedor.pushEnviado = 0 ")
	public List<CotacaoFornecedor> findRespostasEnviadasParaPush();	
	//@formatter:on
}