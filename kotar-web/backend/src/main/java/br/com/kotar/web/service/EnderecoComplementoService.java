package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.EnderecoComplemento;
import br.com.kotar.web.repository.EnderecoComplementoRepository;

@Service
public class EnderecoComplementoService extends BaseService<EnderecoComplemento> {

	//@formatter:off
	@Autowired EnderecoComplementoRepository enderecoComplementoRepository;
	//@formatter:on

	@Override
	public BaseRepository<EnderecoComplemento> getRepository() {
		return enderecoComplementoRepository;
	}
}
