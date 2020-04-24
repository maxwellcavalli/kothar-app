package br.com.kotar.web.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.repository.impl.BaseRepositoryImpl;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.helper.CotacaoFornecedorFilter;
import br.com.kotar.web.repository.CotacaoFornecedorRepository;
import br.com.kotar.web.util.ServiceUtil;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CotacaoFornecedorDAOImpl extends BaseRepositoryImpl<CotacaoFornecedor> implements CotacaoFornecedorRepository {
	//@formatter:off
	@Autowired CotacaoFornecedorRepository cotacaoFornecedorRepository;
	@Autowired protected Messages messages;
	//@formatter:on

	@Override
	public BaseRepository<CotacaoFornecedor> getRepository() {
		return cotacaoFornecedorRepository;
	}

	public Page<CotacaoFornecedor> findCotacaoFornecedorVencedorByCliente(Cliente cliente, CotacaoFornecedorFilter cotacaoFornecedorFilter) throws Exception {	

		StringBuilder hql = new StringBuilder();
		hql.append(" select cotacaoFornecedor "
			+ "  from CotacaoFornecedor cotacaoFornecedor "
			+ " inner join fetch cotacaoFornecedor.cotacao "
			+ " inner join fetch cotacaoFornecedor.fornecedor "		
			+ " where cotacaoFornecedor.codigoAprovacaoVencedor is not null "
			+ " and cotacaoFornecedor.vencedorByUser = 1 "
			+ " and cotacaoFornecedor.cotacao.cliente = :cliente "
			+ " order by cotacaoFornecedor.id desc"); 

		Pageable pageable = cotacaoFornecedorFilter.getPageable();
		
		Map<String, Object> parameters = new HashMap<>();
     	parameters.put("cliente", cliente);


     	Page<CotacaoFornecedor> retorno = searchPaginated(hql.toString(), pageable, parameters);
     	
     	for (CotacaoFornecedor cf : retorno) { 
     		ServiceUtil.popularTextoPrazo(cf, messages);
     	}     	
     	
		return retorno; 
	}
		

	@Override
	public CotacaoFornecedor findByCotacaoAndFornecedor(Cotacao cotacao, Fornecedor fornecedor) {
		return cotacaoFornecedorRepository.findByCotacaoAndFornecedor(cotacao, fornecedor);
	}

	@Override
	public List<CotacaoFornecedor> findByCotacao(Cotacao cotacao) {
		return cotacaoFornecedorRepository.findByCotacao(cotacao);
	}

	@Override
	public long countByCotacaoRespondida(Cotacao cotacao) {
		return cotacaoFornecedorRepository.countByCotacaoRespondida(cotacao);
	}

	@Override
	public List<CotacaoFornecedor> findByCotacaoGlobal(Cotacao cotacao) {
		return cotacaoFornecedorRepository.findByCotacaoGlobal(cotacao);
	}

	@Override
	public CotacaoFornecedor findVencedorByCotacao(Cotacao cotacao) {
		return cotacaoFornecedorRepository.findVencedorByCotacao(cotacao);
	}

	@Override
	public List<CotacaoFornecedor> findVencedorByCliente(Cliente cliente) {
		return cotacaoFornecedorRepository.findVencedorByCliente(cliente);
	}

	@Override
	public List<CotacaoFornecedor> findRespostasEnviadasParaPush() {
		return cotacaoFornecedorRepository.findRespostasEnviadasParaPush();
	}

	
	
}
