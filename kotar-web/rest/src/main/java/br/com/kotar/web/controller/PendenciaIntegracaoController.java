package br.com.kotar.web.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.PendenciaIntegracao;
import br.com.kotar.domain.helper.PendenciaIntegracaoFilter;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.PendenciaIntegracaoService;

@RestController
@RequestMapping(value = { "/api/secure/pendencia-integracao" })
public class PendenciaIntegracaoController extends BaseLoggedUserController<PendenciaIntegracao> {

	//@formatter:off
	@Autowired PendenciaIntegracaoService pendenciaIntegracaoProdutoService;
	//@formatter:on

	@Override
	public BaseService<PendenciaIntegracao> getService() {
		return pendenciaIntegracaoProdutoService;
	}

	@Override
	protected Page<PendenciaIntegracao> onSearch(PageFilter pageFilter) throws Exception {
		Object filterValue = pageFilter.getFilterValue();
		PendenciaIntegracaoFilter pendenciaIntegracaoProdutoFilter = new PendenciaIntegracaoFilter();
		if (filterValue != null) {
			pendenciaIntegracaoProdutoFilter = (PendenciaIntegracaoFilter) filterValueToObject(filterValue, PendenciaIntegracaoFilter.class);
		}

		Pageable pageable = getPageable(pageFilter);
		pendenciaIntegracaoProdutoFilter.setPageable(pageable);

		return pendenciaIntegracaoProdutoService.findPedencias(pendenciaIntegracaoProdutoFilter);
	}

	@Override
	protected PendenciaIntegracao onSave(PendenciaIntegracao domain) throws Exception {
		try {
			Usuario usuario = getLoggedUser();
			PendenciaIntegracao _pend = pendenciaIntegracaoProdutoService.persistirNovoProduto(domain, usuario);
			_pend = pendenciaIntegracaoProdutoService.findById(domain.getId()).get();
			
			return _pend; 
		} catch (InvalidTokenException e) {
			// Nothing
		} catch (Exception e) {
			throw e;
		}

		return domain;
	}

	@RequestMapping(value = "/vincular-produto", method = RequestMethod.POST)
	public ResponseEntity<ResponseHelper<PendenciaIntegracao>> vincularProduto(@RequestBody PendenciaIntegracao domain, UriComponentsBuilder ucBuilder) {
		try {
			Usuario usuario = getLoggedUser();
			PendenciaIntegracao _pend = pendenciaIntegracaoProdutoService.vincularProduto(domain, usuario);
			_pend = pendenciaIntegracaoProdutoService.findById(domain.getId()).get();
			
			return new ResponseEntity<ResponseHelper<PendenciaIntegracao>>(new ResponseHelper<PendenciaIntegracao>(_pend), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<PendenciaIntegracao>>(new ResponseHelper<PendenciaIntegracao>(message),
					HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/finalizar-pendencia", method = RequestMethod.POST)
	public ResponseEntity<ResponseHelper<PendenciaIntegracao>> finalizarPendencia(@RequestBody PendenciaIntegracao domain, UriComponentsBuilder ucBuilder) {
		try {
			Usuario usuario = getLoggedUser();
			PendenciaIntegracao _pend = pendenciaIntegracaoProdutoService.finalizarPendencia(domain, usuario);
			_pend = pendenciaIntegracaoProdutoService.findById(domain.getId()).get();
			
			return new ResponseEntity<ResponseHelper<PendenciaIntegracao>>(new ResponseHelper<PendenciaIntegracao>(_pend), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<PendenciaIntegracao>>(new ResponseHelper<PendenciaIntegracao>(message),
					HttpStatus.EXPECTATION_FAILED);
		}
	}

}
