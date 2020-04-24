package br.com.kotar.web.repository.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.repository.impl.BaseRepositoryImpl;
import br.com.kotar.domain.business.Avaliacao;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.helper.AvaliacaoFilter;
import br.com.kotar.domain.helper.TotaisAvaliacaoFornecedorHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.AvaliacaoRepository;
import br.com.kotar.web.service.ClienteService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AvaliacaoDAOImpl extends BaseRepositoryImpl<Avaliacao> implements AvaliacaoRepository {
	
	//@formatter:off
	@Autowired AvaliacaoRepository avaliacaoRepository;
	@Autowired ClienteService clienteService;
	//@formatter:on
	
	@Override
	public BaseRepository<Avaliacao> getRepository() {
		return avaliacaoRepository;
	}

	public Page<Avaliacao> findAvaliacao(Usuario usuario, AvaliacaoFilter avaliacaoFilter) throws Exception {
		Pageable pageable = avaliacaoFilter.getPageable();
		
		if (avaliacaoFilter.getIdFornecedor() != null) {
			Fornecedor fornecedor = new Fornecedor();
			fornecedor.setId(avaliacaoFilter.getIdFornecedor());
			return findByFornecedor(fornecedor, pageable);
		}
		else {
			return findByUsuario(usuario, pageable);
		}
	}
	
	public Page<Avaliacao> findByUsuario(Usuario usuario, Pageable pageable) throws Exception {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" select avaliacao ");
		hql.append("   from Avaliacao avaliacao ");
		hql.append(" inner join fetch avaliacao.cotacaoFornecedor cotacaoFornecedor ");
		hql.append(" inner join fetch cotacaoFornecedor.cotacao cotacao ");		
		hql.append(" inner join fetch cotacao.cliente cliente ");			
		hql.append(" where cliente.usuario = :usuario ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("usuario", usuario);
		
		return searchPaginated(hql.toString(), pageable, parameters);
	}
	
	public Page<Avaliacao> findByFornecedor(Fornecedor fornecedor, Pageable pageable) throws Exception {
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" select avaliacao ");
		hql.append("   from Avaliacao avaliacao ");
		hql.append(" inner join fetch avaliacao.cotacaoFornecedor cotacaoFornecedor ");
		hql.append(" inner join fetch cotacaoFornecedor.cotacao cotacao ");
		hql.append(" inner join fetch cotacao.cliente cliente ");
		hql.append(" where cotacaoFornecedor.fornecedor = :fornecedor ");
		hql.append(" order by cotacao.dataAvaliacao desc ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("fornecedor", fornecedor);
		
		
		Page<Avaliacao> avaliacoes = searchPaginated(hql.toString(), pageable, parameters); 
		
		for (Avaliacao avaliacao : avaliacoes) {
			
			Cotacao cotacao = avaliacao.getCotacaoFornecedor().getCotacao();
			Date dataAvaliacao = cotacao.getDataAvaliacao();
			String nomeUsuario = cotacao.getCliente().getPrimeiroNome();
			
			Long id = avaliacao.getCotacaoFornecedor().getId();
			CotacaoFornecedor zero = new CotacaoFornecedor();
			zero.setId(id);
			avaliacao.setCotacaoFornecedor(zero); //Zerar os objetos a partir do CotacaoFornecedor para não ir informações de outro usuário
			
			avaliacao.setDataAvaliacao(dataAvaliacao); 
			avaliacao.setNomeUsuario(nomeUsuario);
		}
		
		return avaliacoes;
	}

	
	public TotaisAvaliacaoFornecedorHelper findTotaisByFornecedor(Fornecedor fornecedor) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		
		sql.append(" SELECT ");
		sql.append("	SUM(CASE WHEN avaliacao.numero_estrelas = 1 THEN 1 ELSE 0 END) uma_estrela, ");
		sql.append("    SUM(CASE WHEN avaliacao.numero_estrelas = 2 THEN 1 ELSE 0 END) duas_estrelas, ");
		sql.append("	SUM(CASE WHEN avaliacao.numero_estrelas = 3 THEN 1 ELSE 0 END) tres_estrelas, ");
		sql.append("	SUM(CASE WHEN avaliacao.numero_estrelas = 4 THEN 1 ELSE 0 END) quatro_estrelas, ");
		sql.append("	SUM(CASE WHEN avaliacao.numero_estrelas = 5 THEN 1 ELSE 0 END) cinco_estrelas, ");
		sql.append("	SUM(1) total_avaliacoes  ");  
		sql.append(" from avaliacao avaliacao, "); 
		sql.append("	cotacao_fornecedor cotacaoFornecedor ");
		sql.append(" where avaliacao.cotacao_fornecedor_id = cotacaoFornecedor.cotacao_fornecedor_id ");
		sql.append(" and cotacaoFornecedor.fornecedor_id = :fornecedor_id ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("fornecedor_id", fornecedor.getId());

		TotaisAvaliacaoFornecedorHelper ret = null;
		Object _l = query.getSingleResult();
		
		if (_l != null) {
			Object[] _cols = (Object[]) _l;

			Long uma_estrela = ((BigDecimal) _cols[0]).longValue();
			Long duas_estrelas = ((BigDecimal) _cols[1]).longValue();
			Long tres_estrelas = ((BigDecimal) _cols[2]).longValue();
			Long quatro_estrelas = ((BigDecimal) _cols[3]).longValue();
			Long cinco_estrelas = ((BigDecimal) _cols[4]).longValue();
			Long total_avaliacoes = ((BigDecimal) _cols[5]).longValue();
			
			BigDecimal media = new BigDecimal((float)(uma_estrela*1 + duas_estrelas*2 + tres_estrelas*3 + quatro_estrelas*4 + cinco_estrelas*5) / total_avaliacoes);
			media = media.setScale(1, BigDecimal.ROUND_HALF_UP);
			
			ret = new TotaisAvaliacaoFornecedorHelper();
			ret.setFornecedor(fornecedor);
			ret.setTotal1Estrela(uma_estrela);
			ret.setTotal2Estrelas(duas_estrelas);
			ret.setTotal3Estrelas(tres_estrelas);
			ret.setTotal4Estrelas(quatro_estrelas);
			ret.setTotal5Estrelas(cinco_estrelas);
			ret.setTotalAvaliacoes(total_avaliacoes);
			 
			ret.setMediaAvaliacoes(media);
		}
		
		return ret;
	}
	
	@Override
	public Avaliacao findByUsuario(Usuario usuario) {
		return avaliacaoRepository.findByUsuario(usuario);
	}

	@Override
	public Avaliacao findByCotacao(Cotacao cotacao) {
		return avaliacaoRepository.findByCotacao(cotacao);
	}

	@Override
	public Long findQuantidadeAvaliacoesPendentes(Cliente cliente) {
		return avaliacaoRepository.findQuantidadeAvaliacoesPendentes(cliente);
	}
}