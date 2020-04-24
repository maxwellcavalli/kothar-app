package br.com.kotar.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.controller.CrudController;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.web.service.GrupoProdutoService;

@RestController
@RequestMapping(value = { "/api/secure/grupoProduto" })
public class GrupoProdutoController extends CrudController<GrupoProduto> {

	//@formatter:off
	@Autowired GrupoProdutoService grupoProdutoService;
	//@formatter:on

	@Override
	public BaseCrudService<GrupoProduto> getService() {
		return grupoProdutoService;
	}

	@Override
	protected GrupoProduto onSave(GrupoProduto domain) throws Exception {
		return grupoProdutoService.saveOrUpdate(domain);
	}

	@Override
	protected GrupoProduto onUpdate(GrupoProduto domain) throws Exception {
		return grupoProdutoService.saveOrUpdate(domain);
	}

	@RequestMapping(value = "/getGrouped", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<GrupoProduto>>> getAll() throws Exception {
		try {
			List<GrupoProduto> list = grupoProdutoService.findGrouped();
			ResponseHelper<List<GrupoProduto>> ret = new ResponseHelper<List<GrupoProduto>>(list);

			return new ResponseEntity<ResponseHelper<List<GrupoProduto>>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			throw e;
		}
	}

	@RequestMapping(value = "/get/default", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<GrupoProduto>> getDefault() throws Exception {
		try {
			GrupoProduto _g = grupoProdutoService.findDefault();
			ResponseHelper<GrupoProduto> ret = new ResponseHelper<GrupoProduto>(_g);

			return new ResponseEntity<ResponseHelper<GrupoProduto>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			throw e;
		}
	}

}