package br.com.kotar.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.controller.CrudController;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Cep;
import br.com.kotar.web.service.CepService;

@RestController
@RequestMapping(value = { "/api/secure/cep" })
public class CepController extends CrudController<Cep> {

	//@formatter:off
	@Autowired CepService cepService;
	//@formatter:on
	
	@Override
	public BaseCrudService<Cep> getService() {
		return cepService;
	}

	@Override
	public void validationBeforeSave(Cep c) throws Exception {
		if (c.getBairro() == null || c.getBairro().getId() == null || c.getBairro().getId().longValue() == 0) {
			throw new Exception(messages.get("cep.bairro.invalido"));
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/search/complex", 
		method = RequestMethod.POST, 
		consumes = MediaType.APPLICATION_JSON_VALUE, 
		produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Page<Cep>>> searchComplex(@RequestBody PageFilter pageFilter) {

		Map<String, String> map = (Map<String, String>) pageFilter.getFilterValue();

		String cep = "";
		if (map.containsKey("cep") && map.get("cep") != null)
			cep = map.get("cep");

		String bairro = "";
		if (map.containsKey("bairro") && map.get("bairro") != null)
			bairro = map.get("bairro");

		String cidade = "";
		if (map.containsKey("cidade") && map.get("cidade") != null)
			cidade = map.get("cidade");

		cep = "%" + cep + "%";
		bairro = "%" + bairro + "%";
		cidade = "%" + cidade + "%";

		Page<Cep> paged = cepService.findByCepAndBairroAndCidadeLikeIgnoreCase(cep, bairro, cidade, getPageable(pageFilter));
		ResponseHelper<Page<Cep>> retorno = new ResponseHelper<Page<Cep>>(paged);
		return new ResponseEntity<ResponseHelper<Page<Cep>>>(retorno, HttpStatus.OK);
	}

	@RequestMapping(value = "/search/code", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cep>> searchCode(@RequestBody PageFilter pageFilter) {

		String codigoPostal = (String) pageFilter.getFilterValue();

		Cep cep = cepService.findByCodigoPostal(codigoPostal);
		ResponseHelper<Cep> retorno = new ResponseHelper<Cep>(cep);
		return new ResponseEntity<ResponseHelper<Cep>>(retorno, HttpStatus.OK);
	}

	
}
