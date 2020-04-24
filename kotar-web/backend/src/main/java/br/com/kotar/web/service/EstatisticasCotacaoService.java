package br.com.kotar.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.helper.EstatisticasGeraisCotacaoHelper;
import br.com.kotar.web.repository.impl.EstatisticasCotacaoDAOImpl;

@Service
public class EstatisticasCotacaoService extends BaseService<Cotacao> {

	//@formatter:off
	@Autowired EstatisticasCotacaoDAOImpl estatisticasCotacaoDAOImpl;
	//@formatter:on

	@Override
	public BaseRepository<Cotacao> getRepository() {
		return estatisticasCotacaoDAOImpl;
	}
	
	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeral(Cliente cliente) throws Exception {
		if (cliente == null) {
			throw new Exception(messages.get("cliente.not.found"));
		}
		
		return ((EstatisticasCotacaoDAOImpl) getRepository()).findEstatisticaGeral(cliente);
	}
	
	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeralAno(Cliente cliente) throws Exception {
		if (cliente == null) {
			throw new Exception(messages.get("cliente.not.found"));
		}
		
		return ((EstatisticasCotacaoDAOImpl) getRepository()).findEstatisticaGeralAno(cliente);
	}
	
	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeralMes(Cliente cliente, Integer ano) throws Exception {
		if (cliente == null) {
			throw new Exception(messages.get("cliente.not.found"));
		}
		
		return ((EstatisticasCotacaoDAOImpl) getRepository()).findEstatisticaGeralMes(cliente, ano);
	}
	
	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeralCotacao(Cliente cliente, Integer ano, Integer mes) throws Exception {
		if (cliente == null) {
			throw new Exception(messages.get("cliente.not.found"));
		}
		
		return ((EstatisticasCotacaoDAOImpl) getRepository()).findEstatisticaGeralCotacao(cliente, ano, mes);
		
	}
	
	
}
