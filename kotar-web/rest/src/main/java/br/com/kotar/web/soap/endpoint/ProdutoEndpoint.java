package br.com.kotar.web.soap.endpoint;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.ProdutoImagem;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.service.FornecedorService;
import br.com.kotar.web.service.ProdutoService;
import br.com.kotar.web.soap.endpoint.base.BaseEndpoint;
import br.com.kotar.web.soap.schema.produto.GetProdutoRequest;
import br.com.kotar.web.soap.schema.produto.GetProdutoResponse;
import br.com.kotar.web.soap.schema.produto.PersistirProdutoRequest;
import br.com.kotar.web.soap.schema.produto.PersistirProdutoResponse;

@Endpoint
public class ProdutoEndpoint extends BaseEndpoint {

	//@formatter:off
	@Autowired ProdutoService produtoService;
	@Autowired FornecedorService fornecedorService;
	//@formatter:on

	private static final String NAMESPACE_URI = "http://kotar.com.br/web/soap/schema/produto";

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProdutoRequest")
	@ResponsePayload
	public GetProdutoResponse getProdutos(@RequestPayload GetProdutoRequest request) throws Exception {
		GetProdutoResponse response = new GetProdutoResponse();
		String token = request.getToken();

		validateToken(token);

		List<br.com.kotar.web.soap.schema.common.Produto> _l = findProdutos(request.getProdutoFilter());

		response.getProduto().addAll(_l);
		return response;
	}
 
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "persistirProdutoRequest")
	@ResponsePayload
	public PersistirProdutoResponse persistirProdutos(@RequestPayload PersistirProdutoRequest request) throws Exception {
		PersistirProdutoResponse response = new PersistirProdutoResponse();
		String token = request.getToken();

		Usuario user = validateToken(token);

		if (request.getUuidFornecedor() == null || request.getUuidFornecedor().isEmpty()) {
			throw new Exception(messages.get("soap.identificador.fornecedor.invalido"));
		}

		Fornecedor fornecedor = fornecedorService.findByUuid(request.getUuidFornecedor());
		if (fornecedor == null) {
			throw new Exception(messages.get("soap.identificador.fornecedor.invalido"));
		} 
		
		String identificacaoProduto = request.getIdentificador();		

		Produto _p = new Produto();
		_p.setNome(request.getNome());
		_p.setDetalhamento(request.getDetalhamento());

		List<ProdutoImagem> _images = new ArrayList<>();

		if (request.getFoto() != null && !request.getFoto().isEmpty()) {
			request.getFoto().forEach(el -> {

				ProdutoImagem _pi = new ProdutoImagem();
				_pi.setImagem64(el.getConteudo());
				_pi.setNome(el.getNome());
				_pi.setAtivo(false);

				_images.add(_pi);
			});
		}
		
		_p.setImagens(_images);

		Double precoUnitario = request.getPrecoUnitario();
		
		boolean hasPendencias = produtoService.persistirProdutoSoap(_p, fornecedor, precoUnitario, user, identificacaoProduto);
		if (hasPendencias){
			String _msg = messages.get("soap.integracao.produto.pendencias");
			response.setMensagem(_msg);
			response.setError(true);
		} else {
			String _msg = messages.get("soap.integracao.produto.sucesso");
			response.setMensagem(_msg);
			response.setError(false);
		}

		return response;
	}
	
	private List<br.com.kotar.web.soap.schema.common.Produto> findProdutos(br.com.kotar.web.soap.schema.produto.ProdutoFilter produtoFilter)
			throws Exception {

		// Sort sort = new Sort(Direction.ASC, "produto.nome");
		Pageable pageable = PageRequest.of(produtoFilter.getPageable().getPageNumber(), produtoFilter.getPageable().getPageSize());

		br.com.kotar.domain.helper.ProdutoFilter pf = new br.com.kotar.domain.helper.ProdutoFilter();
		pf.setNome(produtoFilter.getNome());
		pf.setPageable(pageable);

		List<br.com.kotar.web.soap.schema.common.Produto> list = new ArrayList<>();

		Page<Produto> paged = produtoService.findByParams(pf);
		paged.forEach(el -> {
			br.com.kotar.web.soap.schema.common.Produto _p = new br.com.kotar.web.soap.schema.common.Produto();
			_p.setId(el.getId());
			_p.setNome(el.getNome());
			_p.setDetalhamento(el.getDetalhamento());

			list.add(_p);
		});

		return list;
	}

}
