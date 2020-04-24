package br.com.kotar.web.controller.publico;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.service.UsuarioService;

@RestController
@RequestMapping(value = { "/api/esquecisenha" })
public class EsqueciSenhaController {

	//@formatter:off
	@Autowired UsuarioService usuarioService;
	@Autowired Messages messages;
	//@formatter:on

	@RequestMapping(value = "/enviar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Usuario>> resetPassword(@RequestBody Usuario domain) throws Exception {

		Usuario usuario = usuarioService.findByEmail(domain.getEmail());

		if (usuario != null) {
			usuarioService.resetPassword(usuario);
		} else {
			String message = messages.get("cliente.email.inexistente");
			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(message), HttpStatus.EXPECTATION_FAILED);
		}

		return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(new Usuario()), HttpStatus.OK);
	}

	@RequestMapping(value = "/trocar-senha", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Boolean>> trocarSenha(@RequestBody Usuario usuario) throws Exception {

		try {
			usuarioService.trocarSenha(usuario);

			return new ResponseEntity<ResponseHelper<Boolean>>(new ResponseHelper<Boolean>(Boolean.TRUE), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<Boolean>>(new ResponseHelper<Boolean>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/get-by-reset-token/{uid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Usuario>> findByReset(@PathVariable("uid") String uid) throws Exception {

		try {
			Usuario _u = usuarioService.findByResetToken(uid);

			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(_u), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}

}
