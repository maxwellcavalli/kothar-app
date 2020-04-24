package br.com.kotar.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.controller.CrudController;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Categoria;
import br.com.kotar.web.service.CategoriaService;

@RestController
@RequestMapping(value = { "/api/secure/categoria" })
public class CategoriaController extends CrudController<Categoria> {

	//@formatter:off
	@Autowired CategoriaService categoriaService;
	//@formatter:on

	@Override
	public BaseCrudService<Categoria> getService() {
		return categoriaService;
	}

}
