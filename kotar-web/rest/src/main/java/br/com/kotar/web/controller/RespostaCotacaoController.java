package br.com.kotar.web.controller;

import java.util.List;
import java.util.Optional;

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
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItemArquivo;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.helper.CotacaoRespostaFilter;
import br.com.kotar.domain.helper.CotacaoRespostaHelper;
import br.com.kotar.domain.helper.SituacaoRespostaFornecedorHelper;
import br.com.kotar.domain.helper.TipoJurosPagamentoHelper;
import br.com.kotar.domain.helper.TipoPagamentoHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.CotacaoItemArquivoService;
import br.com.kotar.web.service.RespostaCotacaoService;

@RestController
@RequestMapping(value = { "/api/secure/resposta-cotacao" })
public class RespostaCotacaoController extends BaseLoggedUserController<Cotacao> {

	//@formatter:off
	@Autowired RespostaCotacaoService respostaCotacaoService;
	@Autowired CotacaoItemArquivoService cotacaoItemArquivoService;
	//@formatter:on

	@Override
	public BaseService<Cotacao> getService() {
		return null;
	}

	@RequestMapping(value = "/search/complex-resposta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Page<Cotacao>>> searchComplexResposta(@RequestBody PageFilter pageFilter) {

		ResponseHelper<Page<Cotacao>> retorno = null;
		try {
			Object filterValue = pageFilter.getFilterValue();
			CotacaoRespostaFilter cotacaoFilter = new CotacaoRespostaFilter();
			if (filterValue != null) {
				cotacaoFilter = (CotacaoRespostaFilter) filterValueToObject(filterValue, CotacaoRespostaFilter.class);
			}

			Usuario usuario = getLoggedUser();

			Pageable pageable = getPageable(pageFilter);
			cotacaoFilter.setPageable(pageable);

			Page<Cotacao> paged = respostaCotacaoService.findCotacaoRespostaByParams(cotacaoFilter, usuario);
			retorno = new ResponseHelper<Page<Cotacao>>(paged);
		} catch (InvalidTokenException e) {
			//Nothing
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

	@RequestMapping(value = "/get/situacoes-resposta", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<SituacaoRespostaFornecedorHelper>>> getSituacoesResposta() {
		List<SituacaoRespostaFornecedorHelper> list = respostaCotacaoService.getSituacoesResposta();
		ResponseHelper<List<SituacaoRespostaFornecedorHelper>> ret = new ResponseHelper<List<SituacaoRespostaFornecedorHelper>>(list);

		return new ResponseEntity<ResponseHelper<List<SituacaoRespostaFornecedorHelper>>>(ret, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get/tipos-pagamento", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<TipoPagamentoHelper>>> getTiposPagamento() {
		List<TipoPagamentoHelper> list = respostaCotacaoService.getTiposPagamento();
		ResponseHelper<List<TipoPagamentoHelper>> ret = new ResponseHelper<List<TipoPagamentoHelper>>(list);

		return new ResponseEntity<ResponseHelper<List<TipoPagamentoHelper>>>(ret, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/get/tipos-juros-pagamento", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<TipoJurosPagamentoHelper>>> getTiposJurosPagamento() {
		List<TipoJurosPagamentoHelper> list = respostaCotacaoService.getTiposJurosPagamento();
		ResponseHelper<List<TipoJurosPagamentoHelper>> ret = new ResponseHelper<List<TipoJurosPagamentoHelper>>(list);

		return new ResponseEntity<ResponseHelper<List<TipoJurosPagamentoHelper>>>(ret, HttpStatus.OK);
	}

	@RequestMapping(value = "/load-cotacao-resposta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cotacao>> loadCotacaoResposta(@RequestBody CotacaoRespostaHelper cotacaoResposta) {

		try {
			Cotacao _cot = respostaCotacaoService.findByResposta(cotacaoResposta.getId(), cotacaoResposta.getFornecedor());

			ResponseHelper<Cotacao> _ret = new ResponseHelper<Cotacao>(_cot);

			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Cotacao> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/archive-resposta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cotacao>> archiveRespota(@RequestBody Cotacao cotacao) {

		try {
			CotacaoFornecedor cotacaoFornecedor = cotacao.getCotacaoFornecedor();
			Fornecedor fornecedor = cotacaoFornecedor.getFornecedor();
			Long id = cotacao.getId();
			
			respostaCotacaoService.archiveResposta(cotacao, cotacaoFornecedor);

			
			Cotacao _cot = respostaCotacaoService.findByResposta(id, fornecedor);			
			ResponseHelper<Cotacao> _ret = new ResponseHelper<Cotacao>(_cot);

			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Cotacao> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/enviar-resposta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cotacao>> enviarRespota(@RequestBody Cotacao cotacao) {

		try {
			CotacaoFornecedor cotacaoFornecedor = cotacao.getCotacaoFornecedor();
			Long id = cotacao.getId();
			Fornecedor fornecedor = cotacaoFornecedor.getFornecedor();
			
			respostaCotacaoService.enviarResposta(cotacao, cotacaoFornecedor);
			
			Cotacao _cot = respostaCotacaoService.findByResposta(id, fornecedor);
			
			ResponseHelper<Cotacao> _ret = new ResponseHelper<Cotacao>(_cot);

			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Cotacao> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/recusar-cotacao", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cotacao>> recusar(@RequestBody Cotacao cotacao) {

		try {
			CotacaoFornecedor cotacaoFornecedor = cotacao.getCotacaoFornecedor();
			Long id = cotacao.getId();
			Fornecedor fornecedor = cotacaoFornecedor.getFornecedor();
			
			respostaCotacaoService.recusarCotacao(cotacao, cotacaoFornecedor);

			Cotacao _cot = respostaCotacaoService.findByResposta(id, fornecedor);
			
			ResponseHelper<Cotacao> _ret = new ResponseHelper<Cotacao>(_cot);

			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Cotacao> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Cotacao>>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
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
