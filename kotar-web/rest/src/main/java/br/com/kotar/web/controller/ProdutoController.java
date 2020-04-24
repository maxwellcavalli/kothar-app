package br.com.kotar.web.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.kotar.core.exception.RecordNotFound;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.helper.SimilarityObject;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.ProdutoImagem;
import br.com.kotar.domain.helper.ProdutoFilter;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.LoggedUserController;
import br.com.kotar.web.service.ProdutoImagemService;
import br.com.kotar.web.service.ProdutoService;

@RestController
@RequestMapping(value = { "/api/secure/produto" })
public class ProdutoController extends LoggedUserController<Produto> {

	//@formatter:off
	@Autowired ProdutoService produtoService;
	@Autowired ProdutoImagemService produtoImagemService;
	//@formatter:on
	
	@Override
	public BaseCrudService<Produto> getService() {
		return produtoService;
	}

	@Override
	public void validationBeforeSave(Produto c) throws Exception {
		if (c.getGrupoProduto() == null || c.getGrupoProduto().getId() == null || c.getGrupoProduto().getId().longValue() == 0) {
			throw new Exception(messages.get("produto.grupo.invalido"));
		}
	}

	@RequestMapping(value = "/novo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Produto>> newProduct() {

		return new ResponseEntity<ResponseHelper<Produto>>(new ResponseHelper<Produto>(new Produto()), HttpStatus.OK);
	}

	@Override
	protected Produto onSave(Produto domain) throws Exception {
		try {
			Usuario usuario = getLoggedUser();
			domain = produtoService.saveOrUpdate(domain, usuario);
			return produtoService.findById(domain.getId()).get();
		} catch (InvalidTokenException e) {
		} catch (Exception e) {
			throw e;
		}
		
		return null;
	}

	@Override
	protected Produto onUpdate(Produto domain) throws Exception {
		return onSave(domain);
	}
	
	@Override
	protected Page<Produto> onSearch(PageFilter pageFilter) throws Exception {
		Object filterValue = pageFilter.getFilterValue();
		ProdutoFilter produtoFilter = new ProdutoFilter();
		if (filterValue != null) {
			produtoFilter.setNome(filterValue.toString());
		}

		produtoFilter.setPageable(getPageable(pageFilter));

		Page<Produto> paged = produtoService.findByParams(produtoFilter);
		return paged;
	}
	
	@RequestMapping(value = "/get/imagem/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<ProdutoImagem>> getImagem(@PathVariable("id") long id) throws Exception {
		try {
			Optional<ProdutoImagem> produtoImagemOptional = produtoImagemService.findById(id);

			if (!produtoImagemOptional.isPresent()){
				throw new RecordNotFound();
			}

			ProdutoImagem _pi = produtoImagemOptional.get();
			ResponseHelper<ProdutoImagem> ret = new ResponseHelper<ProdutoImagem>(_pi);
			return new ResponseEntity<ResponseHelper<ProdutoImagem>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<ProdutoImagem> helper = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<ProdutoImagem>>(helper, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/get/thumb/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<ProdutoImagem>> getThumb(@PathVariable("id") long id) throws Exception {
		try {
			ProdutoImagem _pi = produtoImagemService.findThumbsById(id);
			
			_pi = produtoImagemService.createThumbIfNullAndPrincipal(_pi);
			
			ResponseHelper<ProdutoImagem> ret = new ResponseHelper<ProdutoImagem>(_pi);
			return new ResponseEntity<ResponseHelper<ProdutoImagem>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<ProdutoImagem> helper = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<ProdutoImagem>>(helper, HttpStatus.EXPECTATION_FAILED);
		}
	}

	
	@RequestMapping(value = "/get/complete/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Produto>> getDomainComplete(@PathVariable("id") long id) throws Exception {
		try {
			System.out.println("Fetching Domain with id " + id);
			Produto domain = produtoService.findCompleteById(id);

			if (domain == null) {
				System.out.println("Domain with id " + id + " not found");
				return new ResponseEntity<ResponseHelper<Produto>>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<ResponseHelper<Produto>>(new ResponseHelper<Produto>(domain), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<Produto>>(new ResponseHelper<Produto>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/get/similarity/{nome}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<SimilarityObject<Produto>>>> getSimilarity(@PathVariable("nome") String nome) throws Exception {
		try {
			List<SimilarityObject<Produto>> _l = produtoService.findSimilarity(nome, .80);			
			List<SimilarityObject<Produto>> ret = _l.stream().limit(10).collect(Collectors.toList());
			
			return new ResponseEntity<ResponseHelper<List<SimilarityObject<Produto>>>>(new ResponseHelper<List<SimilarityObject<Produto>>>(ret), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<List<SimilarityObject<Produto>>>>(new ResponseHelper<List<SimilarityObject<Produto>>>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@Override
	protected void onDelete(Produto domain, long id) throws Exception {
		produtoService.delete(domain);		
	}
	
}