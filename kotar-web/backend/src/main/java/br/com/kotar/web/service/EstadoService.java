package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Estado;
import br.com.kotar.web.repository.EstadoRepository;

@Service
public class EstadoService extends BaseCrudService<Estado>{

	//@formatter:off
	@Autowired EstadoRepository estadoRepository;
	//@formatter:on

	@Override
	public CrudRepository<Estado> getRepository() {
		return estadoRepository;
	}

}
