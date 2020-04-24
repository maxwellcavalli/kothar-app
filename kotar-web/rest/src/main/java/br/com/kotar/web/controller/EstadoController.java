package br.com.kotar.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.controller.CrudController;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Estado;
import br.com.kotar.web.service.EstadoService;

@RestController
@RequestMapping(value = { "/api/secure/estado" })
public class EstadoController extends CrudController<Estado> {

	//@formatter:off
	@Autowired EstadoService estadoService;
	//@formatter:on

	@Override
	public BaseCrudService<Estado> getService() {
		return estadoService;
	}

}