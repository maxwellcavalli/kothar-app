package br.com.kotar.web.controller.publico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.UsuarioRepository;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.UsuarioService;

@RestController
@RequestMapping(value = { "/api/cadastro" })
public class AppCadastroUsuarioController {

	@Autowired
	protected Messages messages;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	ClienteService clienteService;

	@RequestMapping(value = "/cliente", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Usuario>> cadastrarUsuario(@RequestBody Cliente domain) throws Exception {

		String email = "";
		if (domain.getUsuario() != null) {
			email = domain.getUsuario().getEmail();
		}

		Usuario usuario = usuarioRepository.findByEmail(email);

		if (usuario == null) {
			domain = clienteService.saveOrUpdate(domain);
			Usuario ret = domain.getUsuario();

			String token = usuarioService.generateToken(ret);
			ret.setToken(token);

			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(ret), HttpStatus.OK);
		} else {
			String message = messages.get("cliente.email.existente");
			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(message), HttpStatus.EXPECTATION_FAILED);
		}

	}

}
