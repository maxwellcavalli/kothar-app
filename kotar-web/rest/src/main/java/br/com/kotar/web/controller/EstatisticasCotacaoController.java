package br.com.kotar.web.controller;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.helper.EstatisticasCotacaoFornecedorHelper;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.CotacaoService;

@RestController
@RequestMapping(value = { "/api/secure/estatisticas-cotacao" })
public class EstatisticasCotacaoController extends BaseLoggedUserController<Cotacao> {

	//@formatter:off
	@Autowired CotacaoService cotacaoService;
	//@formatter:on

	@Override
	public BaseService<Cotacao> getService() {
		return cotacaoService;
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
