package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Categoria;
import br.com.kotar.web.repository.CategoriaRepository;

@Service
public class CategoriaService extends BaseCrudService<Categoria>{

	//@formatter:off
	@Autowired CategoriaRepository categoriaRepository;
	//@formatter:on

	@Override
	public CrudRepository<Categoria> getRepository() {
		return categoriaRepository;
	}
	
}
