package br.com.kotar.web.controller;

import br.com.kotar.core.exception.RecordNotFound;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemArquivo;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;
import br.com.kotar.domain.helper.ConsultaRespostaFilter;
import br.com.kotar.domain.helper.CotacaoFornecedorFilter;
import br.com.kotar.domain.helper.RetornoConsultaCotacaoHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.ConsultaRespostaService;
import br.com.kotar.web.service.CotacaoFornecedorService;
import br.com.kotar.web.service.CotacaoItemArquivoService;
import br.com.kotar.web.service.CotacaoItemFornecedorValorService;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

@RestController
@RequestMapping(value = { "/api/secure/consulta-resposta" })
public class ConsultaRespostaController extends BaseLoggedUserController<Cotacao> {

	//@formatter:off
	@Autowired ConsultaRespostaService consultaRespostaService;
	@Autowired CotacaoItemArquivoService cotacaoItemArquivoService;
	@Autowired ClienteService clienteService;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	@Autowired CotacaoItemFornecedorValorService cotacaoItemFornecedorValorService;
	//@formatter:on

	@Override
	public BaseService<Cotacao> getService() {
		return null;
	}

	@RequestMapping(value = "/search/complex", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Page<Cotacao>>> searchComplex(@RequestBody PageFilter pageFilter) {

		ResponseHelper<Page<Cotacao>> retorno = null;
		try {

			Object filterValue = pageFilter.getFilterValue();
			ConsultaRespostaFilter consultaRespostaFilter = new ConsultaRespostaFilter();
			if (filterValue != null) {
				consultaRespostaFilter = (ConsultaRespostaFilter) filterValueToObject(filterValue,
						ConsultaRespostaFilter.class);
			}
			Usuario usuario = getLoggedUser();
			Pageable pageable = getPageable(pageFilter);
			consultaRespostaFilter.setPageable(pageable);
			Cliente cliente = null;
			if (!usuario.isAdmin()) {
				cliente = clienteService.findByUsuario(usuario);
			}
			Page<Cotacao> paged = consultaRespostaService.findConsultaRespostaByParams(consultaRespostaFilter, cliente);
			retorno = new ResponseHelper<Page<Cotacao>>(paged);
		} catch (InvalidTokenException e) {
			// nothing
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Page<Cotacao>> helper = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Page<Cotacao>>>(helper, HttpStatus.EXPECTATION_FAILED);
		}

		return new ResponseEntity<ResponseHelper<Page<Cotacao>>>(retorno, HttpStatus.OK);
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

	@RequestMapping(value = "/get/resposta/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>> getConsulta(@PathVariable("id") long id)
			throws Exception {
		try {
			System.out.println("Fetching Domain with id " + id);
			RetornoConsultaCotacaoHelper domain = consultaRespostaService.findByCotacaoWithFornecedores(id);

			if (domain == null) {
				System.out.println("Domain with id " + id + " not found");
				return new ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>>(
					new ResponseHelper<RetornoConsultaCotacaoHelper>(domain), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>>(
					new ResponseHelper<RetornoConsultaCotacaoHelper>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/alterar-valor-selecionado", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>> alterarValorSelecionado(
			@RequestBody CotacaoItemFornecedorValor cotacaoItemFornecedorValor) {
		try {
			boolean selecionado = cotacaoItemFornecedorValor.isSelecionado();
			Optional<CotacaoItemFornecedorValor> cotacaoItemFornecedorValorOptional = cotacaoItemFornecedorValorService.findById(cotacaoItemFornecedorValor.getId());

			if (!cotacaoItemFornecedorValorOptional.isPresent()){
				throw new RecordNotFound();
			}

			cotacaoItemFornecedorValor = cotacaoItemFornecedorValorOptional.get();
			CotacaoItemFornecedor cotacaoItemFornecedor = cotacaoItemFornecedorValor.getCotacaoItemFornecedor();
			CotacaoItem cotacaoItem = cotacaoItemFornecedor.getCotacaoItem();
			Cotacao _cot = cotacaoItem.getCotacao();

			if (_cot.isSituacaoFinalizada()) {
				throw new Exception(messages.get("consulta.resposta.cotacao.finalizada"));
			}

			cotacaoItemFornecedorValor.setSelecionado(selecionado);

			consultaRespostaService.alterarValorSelecionado(cotacaoItemFornecedorValor);

			RetornoConsultaCotacaoHelper domain = consultaRespostaService.findByCotacaoWithFornecedores(_cot.getId());
			return new ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>>(
					new ResponseHelper<RetornoConsultaCotacaoHelper>(domain), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>>(
					new ResponseHelper<RetornoConsultaCotacaoHelper>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/aceitar-proposta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>> aceitarPropostaMobile(
			@RequestBody CotacaoFornecedor cotacaoFornecedor) {
		try {
			Optional<CotacaoFornecedor> cotacaoFornecedorOptional = cotacaoFornecedorService.findById(cotacaoFornecedor.getId());

			if (!cotacaoFornecedorOptional.isPresent()){
				throw new RecordNotFound();
			}

			cotacaoFornecedor = cotacaoFornecedorOptional.get();
			Cotacao _cot = cotacaoFornecedor.getCotacao();

			consultaRespostaService.finalizar(_cot, cotacaoFornecedor);
			RetornoConsultaCotacaoHelper domain = consultaRespostaService.findByCotacaoWithFornecedores(_cot.getId());

			return new ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>>(
					new ResponseHelper<RetornoConsultaCotacaoHelper>(domain), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<RetornoConsultaCotacaoHelper>>(
					new ResponseHelper<RetornoConsultaCotacaoHelper>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@ApiIgnore
	@RequestMapping(value = "/search/cotacao-vencedora",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Page<CotacaoFornecedor>>> searchCotacaoVencedoraComplex(
			@RequestBody PageFilter pageFilter) {

		ResponseHelper<Page<CotacaoFornecedor>> retorno = null;
		try {

			Object filterValue = pageFilter.getFilterValue();
			CotacaoFornecedorFilter cotacaoFornecedorFilter = new CotacaoFornecedorFilter();
			if (filterValue != null) {
				cotacaoFornecedorFilter = (CotacaoFornecedorFilter) filterValueToObject(filterValue,
						CotacaoFornecedorFilter.class);
			}
			Usuario usuario = getLoggedUser();
			Pageable pageable = getPageable(pageFilter);
			cotacaoFornecedorFilter.setPageable(pageable);
			Cliente cliente = clienteService.findByUsuario(usuario);
			cotacaoFornecedorFilter.setCliente(cliente);

			Page<CotacaoFornecedor> paged = cotacaoFornecedorService.findCotacaoFornecedorVencedorByCliente(cliente,
					cotacaoFornecedorFilter);
			retorno = new ResponseHelper<Page<CotacaoFornecedor>>(paged);
		} catch (InvalidTokenException e) {
			// nothing
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Page<CotacaoFornecedor>> helper = new ResponseHelper<>(message);
			return new ResponseEntity<>(helper, HttpStatus.EXPECTATION_FAILED);
		}

		return new ResponseEntity<>(retorno, HttpStatus.OK);
	}

	@Override
	protected Cotacao onSave(Cotacao domain) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	protected Cotacao onUpdate(Cotacao domain) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	protected Optional<Cotacao> onGet(Long id) throws Exception {
		throw new NotImplementedException();
	}

	@Override
	protected void onDelete(Cotacao domain, long id) throws Exception {
		throw new NotImplementedException();
	}

}
