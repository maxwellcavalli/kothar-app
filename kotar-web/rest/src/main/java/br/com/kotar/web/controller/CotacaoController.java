package br.com.kotar.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.kotar.core.exception.RecordNotFound;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoItemArquivo;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.helper.CotacaoFilter;
import br.com.kotar.domain.helper.EstatisticasCotacaoClienteHelper;
import br.com.kotar.domain.helper.EstatisticasCotacaoFornecedorHelper;
import br.com.kotar.domain.helper.MinhasCotacoesVencedorasFornecedorHelper;
import br.com.kotar.domain.helper.SituacaoCotacaoHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.CotacaoItemArquivoService;
import br.com.kotar.web.service.CotacaoService;

@RestController
@RequestMapping(value = { "/api/secure/cotacao" })
public class CotacaoController extends BaseLoggedUserController<Cotacao> {

	//@formatter:off
	@Autowired CotacaoService cotacaoService;
	@Autowired CotacaoItemArquivoService cotacaoItemArquivoService;
	@Autowired ClienteService clienteService;
	//@formatter:on

	@Override
	public BaseService<Cotacao> getService() {
		return cotacaoService;
	}

	@RequestMapping(value = "/search/complex", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Page<Cotacao>>> searchComplex(@RequestBody PageFilter pageFilter) {

		ResponseHelper<Page<Cotacao>> retorno = null;
		try {
			Object filterValue = pageFilter.getFilterValue();
			CotacaoFilter cotacaoFilter = new CotacaoFilter();
			if (filterValue != null) {
				cotacaoFilter = (CotacaoFilter) filterValueToObject(filterValue, CotacaoFilter.class);
			}

			Usuario usuario = getLoggedUser();

			Pageable pageable = getPageable(pageFilter);
			cotacaoFilter.setPageable(pageable);
			
			Cliente cliente = null;
			if (!usuario.isAdmin()) {
				cliente = clienteService.findByUsuario(usuario);
				
				Page<Cotacao> paged = cotacaoService.findCotacaoByParams(cotacaoFilter, cliente);
				retorno = new ResponseHelper<Page<Cotacao>>(paged);
			} else {
				Page<Cotacao> paged = new PageImpl<>(new ArrayList<>());
				retorno = new ResponseHelper<Page<Cotacao>>(paged);
			}
			
		} catch (InvalidTokenException e) {
			//nothing
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Page<Cotacao>> helper = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Page<Cotacao>>>(helper, HttpStatus.EXPECTATION_FAILED);
		}
		
		return new ResponseEntity<ResponseHelper<Page<Cotacao>>>(retorno, HttpStatus.OK);
	}

	@RequestMapping(value = "/enviar-cotacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cotacao>> enviarCotacao(@RequestBody Cotacao cotacao) {

		try {
			Cotacao _cot = cotacaoService.enviarCotacao(cotacao);
			Optional<Cotacao> cotacaoOptional = cotacaoService.findById(_cot.getId());

			if (!cotacaoOptional.isPresent()){
				throw new RecordNotFound();
			}
			_cot = cotacaoOptional.get();
			
			ResponseHelper<Cotacao> _ret = new ResponseHelper<Cotacao>(_cot);

			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Cotacao> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/cancelar-cotacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cotacao>> cancelarCotacao(@RequestBody Cotacao cotacao) {

		try {
			Cotacao _cot = cotacaoService.cancelarCotacao(cotacao);
			if (_cot != null){
				Optional<Cotacao> cotacaoOptional = cotacaoService.findById(_cot.getId());

				if (!cotacaoOptional.isPresent()){
					throw new RecordNotFound();
				}

				_cot = cotacaoOptional.get();
			}
			
			ResponseHelper<Cotacao> _ret = new ResponseHelper<Cotacao>(_cot);

			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Cotacao> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/copiar-cotacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cotacao>> copiarCotacao(@RequestBody Cotacao cotacao) {

		try {
			Cotacao _cot = cotacaoService.copiarCotacao(cotacao);
			if (_cot != null){
				Optional<Cotacao> cotacaoOptional = cotacaoService.findById(_cot.getId());

				if (!cotacaoOptional.isPresent()){
					throw new RecordNotFound();
				}

				_cot = cotacaoOptional.get();
			}
			
			ResponseHelper<Cotacao> _ret = new ResponseHelper<Cotacao>(_cot);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Cotacao> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/get/situacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<SituacaoCotacaoHelper>>> getSituacoes() {
		List<SituacaoCotacaoHelper> list = cotacaoService.getSituacoes();
		ResponseHelper<List<SituacaoCotacaoHelper>> ret = new ResponseHelper<List<SituacaoCotacaoHelper>>(list);

		return new ResponseEntity<ResponseHelper<List<SituacaoCotacaoHelper>>>(ret, HttpStatus.OK);
	}

	@RequestMapping(value = "/get/arquivo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<CotacaoItemArquivo>> getArquivo(@PathVariable("id") long id) {
		try {
			Optional<CotacaoItemArquivo> cotacaoItemArquivoOptional = cotacaoItemArquivoService.findById(id);

			if (!cotacaoItemArquivoOptional.isPresent()){
				throw new RecordNotFound();
			}


			CotacaoItemArquivo cotacaoItemArquivo = cotacaoItemArquivoOptional.get();
			ResponseHelper<CotacaoItemArquivo> ret = new ResponseHelper<CotacaoItemArquivo>(cotacaoItemArquivo);
			return new ResponseEntity<ResponseHelper<CotacaoItemArquivo>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<CotacaoItemArquivo> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<CotacaoItemArquivo>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/get/thumb/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<CotacaoItemArquivo>> getThumb(@PathVariable("id") long id) {
		try {
			Optional<CotacaoItemArquivo> cotacaoItemArquivoOptional = cotacaoItemArquivoService.findById(id);

			if (!cotacaoItemArquivoOptional.isPresent()){
				throw new RecordNotFound();
			}

			CotacaoItemArquivo cotacaoItemArquivo = cotacaoItemArquivoOptional.get();
			cotacaoItemArquivo = cotacaoItemArquivoService.createThumbIfNull(cotacaoItemArquivo);
			
			ResponseHelper<CotacaoItemArquivo> ret = new ResponseHelper<CotacaoItemArquivo>(cotacaoItemArquivo);
			return new ResponseEntity<ResponseHelper<CotacaoItemArquivo>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<CotacaoItemArquivo> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<CotacaoItemArquivo>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}	
	
	
	@RequestMapping(value = "/find-minhas-cotacoes-vencedoras-fornecedor", 
			method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<MinhasCotacoesVencedorasFornecedorHelper>>> findMinhasCotacoesVencedorasFornecedor(@RequestBody Fornecedor fornecedor) {

		try {
			List<MinhasCotacoesVencedorasFornecedorHelper> _l = cotacaoService.findMinhasCotacoesVencedorasFornecedorPorMes(fornecedor);
			
			ResponseHelper<List<MinhasCotacoesVencedorasFornecedorHelper>> _ret = new ResponseHelper<List<MinhasCotacoesVencedorasFornecedorHelper>>(_l);
			return new ResponseEntity<ResponseHelper<List<MinhasCotacoesVencedorasFornecedorHelper> >>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<List<MinhasCotacoesVencedorasFornecedorHelper>> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<List<MinhasCotacoesVencedorasFornecedorHelper> >>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/find-estatisticas-cotacao-fornecedor", 
			method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<EstatisticasCotacaoFornecedorHelper>>> findEstatisticasCotacaoFornecedor(@RequestBody Fornecedor fornecedor) {

		try {
			List<EstatisticasCotacaoFornecedorHelper> _l = cotacaoService.findEstatisticasCotacaoFornecedor(fornecedor);
			
			ResponseHelper<List<EstatisticasCotacaoFornecedorHelper>> _ret = new ResponseHelper<List<EstatisticasCotacaoFornecedorHelper>>(_l);
			return new ResponseEntity<ResponseHelper<List<EstatisticasCotacaoFornecedorHelper> >>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<List<EstatisticasCotacaoFornecedorHelper>> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<List<EstatisticasCotacaoFornecedorHelper> >>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/find-estatisticas-cotacao-cliente", 
			method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<EstatisticasCotacaoClienteHelper>>> findEstatisticasCotacaoCliente() {
		try {
			Usuario usuario = getLoggedUser();
			Cliente _cli = clienteService.findByUsuario(usuario);
			
			List<EstatisticasCotacaoClienteHelper> _l = cotacaoService.findEstatisticasCotacaoCliente(_cli);
			
			ResponseHelper<List<EstatisticasCotacaoClienteHelper>> _ret = new ResponseHelper<List<EstatisticasCotacaoClienteHelper>>(_l);
			return new ResponseEntity<ResponseHelper<List<EstatisticasCotacaoClienteHelper> >>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<List<EstatisticasCotacaoClienteHelper>> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<List<EstatisticasCotacaoClienteHelper> >>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	
	@Override
	protected Cotacao onSave(Cotacao domain) throws Exception {
		try {
			Usuario usuario = getLoggedUser();
			if (usuario.isAdmin()) {
				throw new Exception(messages.get("cotacao.admin"));
			}
			Cotacao cotacao = cotacaoService.saveOrUpdate(domain, usuario);
			Long id = cotacao.getId();
			return cotacaoService.findById(id).get();
		} catch (InvalidTokenException e) {
			//noting;
		} catch (Exception e) {
			throw e;
		}
		
		return domain;
	}

	@Override
	protected Cotacao onUpdate(Cotacao domain) throws Exception {
		return onSave(domain);
	}

	@Override
	protected Optional<Cotacao> onGet(Long id) throws Exception {
		return cotacaoService.findById(id);
	}

	@Override
	protected void onDelete(Cotacao domain, long id) throws Exception {
		cotacaoService.delete(domain);
	}

}
