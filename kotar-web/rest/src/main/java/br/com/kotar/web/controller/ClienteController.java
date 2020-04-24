package br.com.kotar.web.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.core.validation.ValidacaoCpf;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.LoggedUserController;
import br.com.kotar.web.service.ClienteService;

@RestController
@RequestMapping(value = { "/api/secure/cliente" })
public class ClienteController extends LoggedUserController<Cliente> {

	//@formatter:off
	@Autowired ClienteService clienteService;
	//@Autowired Environment env;
	//@formatter:on

	@Override
	public BaseCrudService<Cliente> getService() {
		return clienteService;
	}

	@Override
	public void validationBeforeSave(Cliente c) throws Exception {
		if (c.getCpf() == null || c.getCpf().trim().isEmpty()) {
			throw new Exception(messages.get("cliente.cpf.invalido"));
		}

		if (!ValidacaoCpf.isCpfValido(c.getCpf())) {
			throw new Exception(messages.get("cliente.cpf.invalido"));
		}
	}

	@Override
	protected Page<Cliente> onSearch(PageFilter pageFilter) throws Exception {
		String nome = (String) pageFilter.getFilterValue();
		String cpf = null;
		nome = nome == null ? "" : nome;
		nome = "%" + nome + "%";

		Page<Cliente> paged = null;
		try {
			Usuario usuario = getLoggedUser();
			paged = clienteService.findByNomeOrCpfLikeIgnoreCase(nome, cpf, usuario, getPageable(pageFilter));
		} catch (InvalidTokenException e) {
		} catch (Exception e) {
			throw e;
		}

		return paged;
	}

	@RequestMapping(value = "/logged", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Cliente>> getClienteLogado() {
		try {
			Cliente cliente = null;
			try {
				Usuario usuario = getLoggedUser();
				cliente = clienteService.findByUsuario(usuario);
			} catch (InvalidTokenException ee) {
			} catch (Exception e) {
				throw e;
			}
			return new ResponseEntity<ResponseHelper<Cliente>>(new ResponseHelper<Cliente>(cliente), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<Cliente>>(new ResponseHelper<Cliente>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@Override
	protected Cliente onSave(Cliente domain) throws Exception {
		// domain = verificarLatLng(domain);
		Cliente _c = clienteService.saveOrUpdate(domain);
		return clienteService.findById(_c.getId()).get();
	}

	@Override
	protected Cliente onUpdate(Cliente domain) throws Exception {
		// domain = verificarLatLng(domain);
		Cliente _c = clienteService.saveOrUpdate(domain);
		return clienteService.findById(_c.getId()).get();
	}

	@Override
	protected void onDelete(Cliente domain, long id) throws Exception {
		clienteService.delete(domain);
	}


}
