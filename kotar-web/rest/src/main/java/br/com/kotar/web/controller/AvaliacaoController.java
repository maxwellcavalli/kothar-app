package br.com.kotar.web.controller;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Avaliacao;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.helper.AvaliacaoFilter;
import br.com.kotar.domain.helper.MotivoDesistenciaCompraHelper;
import br.com.kotar.domain.helper.TotaisAvaliacaoFornecedorHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.AvaliacaoService;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.CotacaoService;

@RestController
@RequestMapping(value = { "/api/secure/avaliacao" })
public class AvaliacaoController extends BaseLoggedUserController<Avaliacao> {

	//@formatter:off
	@Autowired AvaliacaoService avaliacaoService;
	@Autowired CotacaoService cotacaoService;
	@Autowired ClienteService clienteService;
	//@formatter:on

	@Override
	public BaseService<Avaliacao> getService() {
		return avaliacaoService;
	}

	@Override
	protected Avaliacao onSave(Avaliacao domain) throws Exception {
		return avaliacaoService.saveOrUpdate(domain);
	}

	@Override
	protected Avaliacao onUpdate(Avaliacao domain) throws Exception {
		getLoggedUser();
		return avaliacaoService.saveOrUpdate(domain);
	}
	
	@Override
	protected Page<Avaliacao> onSearch(PageFilter pageFilter) throws Exception {
		Page<Avaliacao> paged = null;
		try {
			Usuario usuario = getLoggedUser();
			
			Object filterValue = pageFilter.getFilterValue();
			AvaliacaoFilter filter = new AvaliacaoFilter();
			if (filterValue != null) {
				filter = (AvaliacaoFilter) filterValueToObject(filterValue, AvaliacaoFilter.class);
			}
			
			Pageable pageable = getPageable(pageFilter);
			filter.setPageable(pageable);
			
			paged = avaliacaoService.findAvaliacao(usuario, filter);
		} catch (InvalidTokenException e) {
		} catch (Exception e) {
			throw e;
		}

		return paged;
	} 
	
	
	@RequestMapping(value = "/atualizar-lembrete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Boolean>> atualizarLembrete(@RequestBody CotacaoFornecedor cotacaoFornecedor) throws Exception {
		try {
			if (cotacaoFornecedor != null) {
				avaliacaoService.atualizarLembrete(cotacaoFornecedor);
			}
			
			ResponseHelper<Boolean> ret = new ResponseHelper<>(Boolean.TRUE);
			
			return new ResponseEntity<ResponseHelper<Boolean>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Boolean> ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Boolean>>(ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/find-by-cotacao", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Avaliacao>> findByCotacao(@RequestBody Cotacao cotacao) throws Exception {
		try {
			Avaliacao avaliacao = avaliacaoService.findByCotacao(cotacao);
			
			ResponseHelper<Avaliacao> ret = new ResponseHelper<>(avaliacao);
			return new ResponseEntity<ResponseHelper<Avaliacao>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Avaliacao> ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Avaliacao>>(ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/get/motivos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<MotivoDesistenciaCompraHelper>>> getMotivos() {
		List<MotivoDesistenciaCompraHelper> list = avaliacaoService.getMotivos();
		ResponseHelper<List<MotivoDesistenciaCompraHelper>> ret = new ResponseHelper<List<MotivoDesistenciaCompraHelper>>(list);

		return new ResponseEntity<ResponseHelper<List<MotivoDesistenciaCompraHelper>>>(ret, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/find-totais-by-fornecedor", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<TotaisAvaliacaoFornecedorHelper>> findTotaisByFornecedor(@RequestBody Fornecedor fornecedor) throws Exception {
		try {
			TotaisAvaliacaoFornecedorHelper r = avaliacaoService.findTotaisByFornecedor(fornecedor);
			
			ResponseHelper<TotaisAvaliacaoFornecedorHelper> ret = new ResponseHelper<>(r);
			return new ResponseEntity<ResponseHelper<TotaisAvaliacaoFornecedorHelper>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<TotaisAvaliacaoFornecedorHelper> ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<TotaisAvaliacaoFornecedorHelper>>(ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	
	
	@RequestMapping(value = "/get-quantidade-avaliacoes-pendentes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Long>> getQuantidadeAvaliacoesPendentes() throws Exception {
		try {
			Usuario usuario = getLoggedUser();
			
			Cliente cliente = clienteService.findByUsuario(usuario);
			
			Long avaliacoesPendentes = avaliacaoService.findQuantidadeAvaliacoesPendentes(cliente);
				
			ResponseHelper<Long> ret = new ResponseHelper<>(avaliacoesPendentes);			
			return new ResponseEntity<ResponseHelper<Long>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Long> ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Long>>(ret, HttpStatus.EXPECTATION_FAILED);
		}
	}	
	

}