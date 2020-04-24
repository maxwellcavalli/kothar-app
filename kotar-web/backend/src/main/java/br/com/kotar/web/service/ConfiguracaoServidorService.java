package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.ConfiguracaoServidor;
import br.com.kotar.web.repository.ConfiguracaoServidorRepository;

@Service
public class ConfiguracaoServidorService extends BaseService<ConfiguracaoServidor> {

	//@formatter:off
	@Autowired ConfiguracaoServidorRepository configuracaoServidorRepository;
	//@formatter:on

	@Override
	public BaseRepository<ConfiguracaoServidor> getRepository() {
		return configuracaoServidorRepository;
	}
}