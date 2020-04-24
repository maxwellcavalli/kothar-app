package br.com.kotar.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.core.validation.ValidacaoCNPJ;
import br.com.kotar.core.validation.ValidacaoCpf;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.helper.AvaliacaoFornecedorHelper;
import br.com.kotar.domain.helper.MelhoresClientesFornecedorHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.LoggedUserController;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.FornecedorService;

@RestController
@RequestMapping(value = { "/api/secure/fornecedor" })
public class FornecedorController extends LoggedUserController<Fornecedor> {

	//@formatter:off
	@Autowired FornecedorService fornecedorService;
	@Autowired ClienteService clienteService;
	//@formatter:on

	@Override
	public BaseCrudService<Fornecedor> getService() {
		return fornecedorService;
	}

	@Override
	public void validationBeforeSave(Fornecedor c) throws Exception {
		if (c.getCep() == null || c.getCep().getId() == null || c.getCep().getId().longValue() == 0) {
			throw new Exception(messages.get("fornecedor.endereco.invalido"));
		}

		if (c.getCpfCnpj() == null || c.getCpfCnpj().trim().isEmpty()) {
			throw new Exception(messages.get("fornecedor.cpf.cnpj.invalido"));
		}
		
		String cpfCnpj = c.getCpfCnpj();
		cpfCnpj = cpfCnpj.replaceAll("\\.", "");
		cpfCnpj = cpfCnpj.replaceAll("-", "");
		cpfCnpj = cpfCnpj.replaceAll("/", "");
		
		c.setCpfCnpj(cpfCnpj);

		if (c.getCpfCnpj().length() == 11) {
			if (!ValidacaoCpf.isCpfValido(c.getCpfCnpj())) {
				throw new Exception(messages.get("fornecedor.cpf.cnpj.invalido"));
			}
		} else {
			if (!ValidacaoCNPJ.valida(c.getCpfCnpj())) {
				throw new Exception(messages.get("fornecedor.cpf.cnpj.invalido"));
			}
		}
	}

	@Override
	protected Page<Fornecedor> onSearch(PageFilter pageFilter) throws Exception {
		Page<Fornecedor> paged = null;
		try {
			String nome = (String) pageFilter.getFilterValue();
			String cpfCnpj = null;
			nome = nome == null ? "" : nome;
			nome = "%" + nome + "%";
			Usuario usuario = getLoggedUser();
			paged = fornecedorService.findByNomeOrCpfCnpjLikeIgnoreCase(nome, cpfCnpj, usuario, getPageable(pageFilter));
		} catch (InvalidTokenException e) {
			// nothing
		} catch (Exception e) {
			throw e;
		}

		return paged;
	}

	@RequestMapping(value = "/search/fornecedores", method = RequestMethod.GET)
	public ResponseEntity<ResponseHelper<List<Fornecedor>>> getFornecedoresByCliente() {
		ResponseHelper<List<Fornecedor>> retorno = new ResponseHelper<List<Fornecedor>>(new ArrayList<>());
		try {
			List<Fornecedor> list = new ArrayList<>();
			Usuario usuario = getLoggedUser();
			if (!usuario.isAdmin()) {
				Cliente cliente = clienteService.findByUsuario(usuario);
				if (cliente != null) {
					list = fornecedorService.findByCliente(cliente);
				}
			}

			retorno = new ResponseHelper<List<Fornecedor>>(list);
		} catch (InvalidTokenException e) {
			// nothing
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<List<Fornecedor>>>(new ResponseHelper<List<Fornecedor>>(message), HttpStatus.EXPECTATION_FAILED);
		}

		return new ResponseEntity<ResponseHelper<List<Fornecedor>>>(retorno, HttpStatus.OK);
	}

	@Override
	protected Fornecedor onSave(Fornecedor domain) throws Exception {
		try {
			Usuario usuario = getLoggedUser();
			Fornecedor _f = fornecedorService.saveOrUpdate(domain, usuario);
			
			return onGet(_f.getId()).get();
		} catch (InvalidTokenException e) {
			// nothing
		} catch (Exception e) {
			throw e;
		}

		return domain;
	}
	
	@RequestMapping(value = "/find-avaliacao-fornecedor", 
			method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<AvaliacaoFornecedorHelper>>> findAvaliacaoFornecedor(@RequestBody Fornecedor fornecedor) {

		try {
			List<AvaliacaoFornecedorHelper> _l = fornecedorService.findAvaliacaoFornecedor(fornecedor);
			
			ResponseHelper<List<AvaliacaoFornecedorHelper>> _ret = new ResponseHelper<List<AvaliacaoFornecedorHelper>>(_l);
			return new ResponseEntity<ResponseHelper<List<AvaliacaoFornecedorHelper> >>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<List<AvaliacaoFornecedorHelper>> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<List<AvaliacaoFornecedorHelper> >>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/find-melhores-clientes-fornecedor", 
			method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<MelhoresClientesFornecedorHelper>>> findMelhoresClientesFornecedor(@RequestBody Fornecedor fornecedor) {

		try {
			List<MelhoresClientesFornecedorHelper> _l = fornecedorService.findMelhoresClientes(fornecedor);
			
			ResponseHelper<List<MelhoresClientesFornecedorHelper>> _ret = new ResponseHelper<List<MelhoresClientesFornecedorHelper>>(_l);
			return new ResponseEntity<ResponseHelper<List<MelhoresClientesFornecedorHelper> >>(_ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<List<MelhoresClientesFornecedorHelper>> _ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<List<MelhoresClientesFornecedorHelper> >>(_ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	

	@Override
	protected Fornecedor onUpdate(Fornecedor domain) throws Exception {
		return onSave(domain);
	}

	@Override
	protected void onDelete(Fornecedor domain, long id) throws Exception {
		try {
			Usuario usuario = getLoggedUser();
			fornecedorService.delete(domain, usuario);
		} catch (InvalidTokenException e) {
			// nothing
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	protected Optional<Fornecedor> onGet(Long id) throws Exception {
		try {
			Usuario usuario = getLoggedUser();
			return fornecedorService.findById(id, usuario);
		} catch (InvalidTokenException e) {
			// nothing
		} catch (Exception e) {
			throw e;
		}

		return null;
	}

}
