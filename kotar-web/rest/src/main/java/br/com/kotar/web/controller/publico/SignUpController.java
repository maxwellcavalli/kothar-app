package br.com.kotar.web.controller.publico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.validation.ValidacaoCpf;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.ClienteService;

@RestController
@RequestMapping(value = { "/api/signup" })
public class SignUpController extends BaseLoggedUserController<Cliente> {

	//@formatter:off
	@Autowired ClienteService clienteService;
	//@formatter:on

	@Override
	protected Cliente onSave(Cliente domain) throws Exception {
		validationBeforeSave(domain);
		Cliente _c = clienteService.saveOrUpdate(domain);
		return clienteService.findById(_c.getId()).get();
	}

	public void validationBeforeSave(Cliente c) throws Exception {
		if (c.getCpf() == null || c.getCpf().trim().isEmpty()) {
			throw new Exception(messages.get("cliente.cpf.invalido"));
		}

		if (!ValidacaoCpf.isCpfValido(c.getCpf())) {
			throw new Exception(messages.get("cliente.cpf.invalido"));
		}
	}

}
