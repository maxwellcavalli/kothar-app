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
import br.com.kotar.domain.business.Bairro;
import br.com.kotar.web.service.BairroService;

@RestController
@RequestMapping(value = { "/api/secure/bairro" })
public class BairroController extends CrudController<Bairro> {

	//@formatter:off
	@Autowired BairroService bairroService;
	//@formatter:on

	@Override
	public BaseCrudService<Bairro> getService() {
		return bairroService;
	}

	@Override
	public void beforeSave(Bairro c) {
	}

	@Override
	public void validationBeforeSave(Bairro c) throws Exception {
		if (c.getCidade() == null || c.getCidade().getId() == null || c.getCidade().getId().longValue() == 0) {
			throw new Exception(messages.get("bairro.cidade.invalida"));
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/search/complex", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Page<Bairro>>> searchComplex(@RequestBody PageFilter pageFilter) {

		Map<String, String> map = (Map<String, String>) pageFilter.getFilterValue();

		String bairro = "";
		if (map.containsKey("bairro") && map.get("bairro") != null)
			bairro = map.get("bairro");

		String cidade = "";
		if (map.containsKey("cidade") && map.get("cidade") != null)
			cidade = map.get("cidade");

		bairro = "%" + bairro + "%";
		cidade = "%" + cidade + "%";

		Page<Bairro> paged = bairroService.findByBairroAndCidadeLikeIgnoreCase(bairro, cidade, getPageable(pageFilter));
		ResponseHelper<Page<Bairro>> retorno = new ResponseHelper<Page<Bairro>>(paged);
		return new ResponseEntity<ResponseHelper<Page<Bairro>>>(retorno, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/search/autocomplete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Page<Bairro>>> searchAutoComplete(@RequestBody PageFilter pageFilter) {

		Map<String, Object> filters = (Map<String, Object>) pageFilter.getFilterValue();
		String bairro = "";
		if (filters.containsKey("bairro"))
			bairro = (String) filters.get("bairro");

		Long cidade = 0l;
		if (filters.containsKey("cidade"))
			cidade = ((Integer) filters.get("cidade")).longValue();

		bairro = "%" + bairro + "%";

		Page<Bairro> paged = bairroService.findByBairroIdCidadeLikeIgnoreCase(bairro, cidade, getPageable(pageFilter));
		ResponseHelper<Page<Bairro>> retorno = new ResponseHelper<Page<Bairro>>(paged);
		return new ResponseEntity<ResponseHelper<Page<Bairro>>>(retorno, HttpStatus.OK);
	}

}
