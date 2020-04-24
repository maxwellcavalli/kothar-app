package br.com.kotar.web.soap.endpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import br.com.kotar.domain.business.Bairro;
import br.com.kotar.domain.business.Cep;
import br.com.kotar.domain.business.Cidade;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoEndereco;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;
import br.com.kotar.domain.business.EnderecoComplemento;
import br.com.kotar.domain.business.Estado;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorProduto;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.helper.CotacaoFilter;
import br.com.kotar.web.service.CotacaoFornecedorService;
import br.com.kotar.web.service.CotacaoService;
import br.com.kotar.web.service.FornecedorProdutoService;
import br.com.kotar.web.service.FornecedorService;
import br.com.kotar.web.service.RespostaCotacaoService;
import br.com.kotar.web.soap.endpoint.base.BaseEndpoint;
import br.com.kotar.web.soap.schema.cotacao.GetCotacoesRequest;
import br.com.kotar.web.soap.schema.cotacao.GetCotacoesResponse;
import br.com.kotar.web.soap.schema.cotacao.GetInfoCotacaoRequest;
import br.com.kotar.web.soap.schema.cotacao.GetInfoCotacaoResponse;

@Endpoint
public class CotacaoEndpoint extends BaseEndpoint {

	//@formatter:off
	@Autowired FornecedorService fornecedorService;
	@Autowired CotacaoService cotacaoService;
	@Autowired RespostaCotacaoService respostaCotacaoService;
	@Autowired FornecedorProdutoService fornecedorProdutoService;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	//@formatter:on

	private static final String NAMESPACE_URI = "http://kotar.com.br/web/soap/schema/cotacao";

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCotacoesRequest")
	@ResponsePayload
	public GetCotacoesResponse getCotacoes(@RequestPayload GetCotacoesRequest request) throws Exception {
		GetCotacoesResponse response = new GetCotacoesResponse();

		String token = request.getToken();
		validateToken(token);

		if (request.getUuidFornecedor() == null || request.getUuidFornecedor().isEmpty()) {
			throw new Exception(messages.get("soap.identificador.fornecedor.invalido"));
		}

		Fornecedor fornecedor = fornecedorService.findByUuid(request.getUuidFornecedor());
		if (fornecedor == null) {
			throw new Exception(messages.get("soap.identificador.fornecedor.invalido"));
		}

		CotacaoFilter cotacaoFilter = new CotacaoFilter();
		cotacaoFilter.setNome(request.getNome());

		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
		cotacaoFilter.setPageable(pageable);

		Page<Cotacao> list = cotacaoService.pesquisarCotacoesFinalizadasFornecedor(fornecedor, cotacaoFilter);
		List<br.com.kotar.web.soap.schema.common.Cotacao> ret = new ArrayList<>();

		list.forEach(el -> {
			br.com.kotar.web.soap.schema.common.Cotacao _cot = convertCotacao(el, null);
			ret.add(_cot);
		});

		response.getCotacao().addAll(ret);

		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getInfoCotacaoRequest")
	@ResponsePayload
	public GetInfoCotacaoResponse getInfoCotacao(@RequestPayload GetInfoCotacaoRequest request) throws Exception {
		GetInfoCotacaoResponse response = new GetInfoCotacaoResponse();
 
		String token = request.getToken();
		validateToken(token);

		if (request.getUuidFornecedor() == null || request.getUuidFornecedor().isEmpty()) {
			throw new Exception(messages.get("soap.identificador.fornecedor.invalido"));
		}

		Fornecedor fornecedor = fornecedorService.findByUuid(request.getUuidFornecedor());
		if (fornecedor == null) {
			throw new Exception(messages.get("soap.identificador.fornecedor.invalido"));
		}

		Long id = request.getId();
		Cotacao cotacao = respostaCotacaoService.findByResposta(id, fornecedor);
		
		CotacaoFornecedor cotacaoFornecedor = cotacaoFornecedorService.findByCotacaoAndFornecedor(cotacao, fornecedor);
		if (cotacaoFornecedor == null || cotacaoFornecedor.getVencedorByUser().booleanValue() == false){
			throw new Exception(messages.get("soap.integracao.cotacao.fornecedor.invalido"));
		}
		
		br.com.kotar.web.soap.schema.common.Cotacao _cot = convertCotacao(cotacao, cotacaoFornecedor);		
		response.setCotacao(_cot);
		return response;
	}

	private br.com.kotar.web.soap.schema.common.Cotacao convertCotacao(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) {
		br.com.kotar.web.soap.schema.common.Cotacao _cot = new br.com.kotar.web.soap.schema.common.Cotacao();
		_cot.setCotacaoId(cotacao.getId());
		_cot.setCotacaoNome(cotacao.getNome());
		
		if (cotacaoFornecedor != null){	
			_cot.setCodigoVoucher(cotacaoFornecedor.getCodigoAprovacaoVencedor());
		}

		br.com.kotar.web.soap.schema.common.Cliente _cli = new br.com.kotar.web.soap.schema.common.Cliente();
		_cli.setClienteId(cotacao.getCliente().getId());
		_cli.setClienteNome(cotacao.getCliente().getNome());
		_cli.setCpf(cotacao.getCliente().getCpfFormat());
		_cot.setCliente(_cli);

		CotacaoEndereco cotacaoEndereco = cotacao.getCotacaoEndereco();
		Cep cep = cotacaoEndereco.getCep();
		Bairro bairro = cep.getBairro();
		Cidade cidade = bairro.getCidade();
		Estado estado = cidade.getEstado();

		EnderecoComplemento enderecoComplemento = cotacaoEndereco.getEnderecoComplemento();

		br.com.kotar.web.soap.schema.common.Estado _est = new br.com.kotar.web.soap.schema.common.Estado();
		_est.setEstadoNome(estado.getNome());
		_est.setSigla(estado.getSigla());

		br.com.kotar.web.soap.schema.common.Cidade _cid = new br.com.kotar.web.soap.schema.common.Cidade();
		_cid.setCidadeEstado(_est);
		_cid.setCidadeNome(cidade.getNome());

		br.com.kotar.web.soap.schema.common.Bairro _bai = new br.com.kotar.web.soap.schema.common.Bairro();
		_bai.setBairroCidade(_cid);
		_bai.setBairroNome(bairro.getNome());

		br.com.kotar.web.soap.schema.common.Cep _cep = new br.com.kotar.web.soap.schema.common.Cep();
		_cep.setCepBairro(_bai);
		_cep.setCepNome(cep.getNome());
		_cep.setCodigoPostal(cep.getCodigoPostal());

		br.com.kotar.web.soap.schema.common.EnderecoComplemento _endC = new br.com.kotar.web.soap.schema.common.EnderecoComplemento();
		_endC.setComplemento(enderecoComplemento.getComplemento());
		_endC.setNumero(enderecoComplemento.getNumero());

		br.com.kotar.web.soap.schema.common.CotacaoEndereco _cotE = new br.com.kotar.web.soap.schema.common.CotacaoEndereco();
		_cotE.setCep(_cep);
		_cotE.setComplemento(_endC);

		_cot.setCotacaoEndereco(_cotE);

		List<br.com.kotar.web.soap.schema.common.CotacaoItemFornecedor> _fItens = new ArrayList<>();

		if (cotacao.getForncedorItens() != null) {
			cotacao.getForncedorItens().forEach(it -> {
				CotacaoItem _cotIt = it.getCotacaoItem();
				Produto _prod = _cotIt.getProduto();

				FornecedorProduto _fp = null;
				if (cotacaoFornecedor != null) {
					Fornecedor _f = cotacaoFornecedor.getFornecedor();
					
					_fp = fornecedorProdutoService.findByFornecedorAndProduto(_f, _prod);
				}

				br.com.kotar.web.soap.schema.common.CotacaoItemFornecedor _cif = new br.com.kotar.web.soap.schema.common.CotacaoItemFornecedor();

				br.com.kotar.web.soap.schema.common.Produto _p = new br.com.kotar.web.soap.schema.common.Produto();
				_p.setNome(_prod.getNome());

				if (_fp != null) {
					_p.setIdentificador(_fp.getIdentificacao());
				}

				br.com.kotar.web.soap.schema.common.CotacaoItem _ci = new br.com.kotar.web.soap.schema.common.CotacaoItem();
				_ci.setObservacao(_cotIt.getObservacao());
				_ci.setQuantidade(_cotIt.getQuantidade().doubleValue());
				_ci.setProduto(_p);

				_cif.setCotacaoItem(_ci);
				List<CotacaoItemFornecedorValor> _lVal = it.getValores().stream().filter(filter -> filter.isSelecionado()).collect(Collectors.toList());
				if (!_lVal.isEmpty()){
					_cif.setMarcaModelo(_lVal.get(0).getMarcaModelo());
					_cif.setValorUnitario(_lVal.get(0).getUnitario().doubleValue());
				}
				
				_fItens.add(_cif);
			});
		}
		
		_cot.getItens().addAll(_fItens);

		return _cot;
	}

}
