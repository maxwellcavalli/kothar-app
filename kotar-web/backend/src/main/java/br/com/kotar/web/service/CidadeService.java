package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Cidade;
import br.com.kotar.web.repository.CidadeRepository;

@Service
public class CidadeService extends BaseCrudService<Cidade> {

	//@formatter:off
	@Autowired CidadeRepository cidadeRepository;
	//@formatter:on

	@Override
	public CrudRepository<Cidade> getRepository() {
		return cidadeRepository;
	}

	public Page<Cidade> findByCidadeIdEstadoLikeIgnoreCase(String cidade, Long estado, Pageable pageable) {
		return cidadeRepository.findByCidadeIdEstadoLikeIgnoreCase(cidade, estado, pageable);
	}

	public Page<Cidade> findByCidadeEstadoLikeIgnoreCase(String cidade, String estado, Pageable pageable) {
		return cidadeRepository.findByCidadeEstadoLikeIgnoreCase(cidade, estado, pageable);
	}

}
