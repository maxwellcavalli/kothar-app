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
import br.com.kotar.domain.business.Cidade;
import br.com.kotar.web.service.CidadeService;

@RestController
@RequestMapping(value = { "/api/secure/cidade" })
public class CidadeController extends CrudController<Cidade> {

	//@formatter:off
	@Autowired CidadeService cidadeService;
	//@formatter:on
	
	@Override
	public BaseCrudService<Cidade> getService() {
		return cidadeService;
	}
	
	@Override
	public void validationBeforeSave(Cidade c) throws Exception {
		if (c.getEstado() == null || c.getEstado().getId() == null || c.getEstado().getId().longValue() == 0){
			throw new Exception(messages.get("cidade.estado.invalido"));	
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/search/autocomplete", method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)	
	public ResponseEntity<ResponseHelper<Page<Cidade>>> searchAutoComplete(@RequestBody PageFilter pageFilter){
		
		Map<String, Object> filters = (Map<String, Object>) pageFilter.getFilterValue();
		String cidade = "";
		if (filters.containsKey("cidade"))
			cidade = (String) filters.get("cidade");
		
		Long estado = 0l;
		if (filters.containsKey("estado"))
			estado =  ((Integer) filters.get("estado")).longValue();
		
		cidade = "%"+cidade+"%";
		
		Page<Cidade> paged = cidadeService.findByCidadeIdEstadoLikeIgnoreCase(cidade, estado, getPageable(pageFilter));
		ResponseHelper<Page<Cidade>> retorno = new ResponseHelper<Page<Cidade>>(paged);
		return new ResponseEntity<ResponseHelper<Page<Cidade>>>(retorno, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/search/complex", method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)	
	public ResponseEntity<ResponseHelper<Page<Cidade>>> searchComplex(@RequestBody PageFilter pageFilter){
		
		Map<String, Object> filters = (Map<String, Object>) pageFilter.getFilterValue();
		String cidade = "";
		if (filters.containsKey("cidade") && filters.get("cidade") != null)
			cidade = (String) filters.get("cidade");
		
		String estado = "";
		if (filters.containsKey("estado") && filters.get("estado") != null)
			estado =  (String) filters.get("estado");
		
		
		cidade = "%"+cidade+"%";
		estado = "%"+estado+"%";
		
		Page<Cidade> paged = cidadeService.findByCidadeEstadoLikeIgnoreCase(cidade, estado, getPageable(pageFilter));
		ResponseHelper<Page<Cidade>> retorno = new ResponseHelper<Page<Cidade>>(paged);
		return new ResponseEntity<ResponseHelper<Page<Cidade>>>(retorno, HttpStatus.OK);
	}

	

}