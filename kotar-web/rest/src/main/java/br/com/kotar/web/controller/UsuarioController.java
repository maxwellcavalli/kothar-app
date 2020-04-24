package br.com.kotar.web.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.core.util.StringUtil;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.domain.security.UsuarioToken;
import br.com.kotar.domain.security.type.SituacaoUsuarioType;
import br.com.kotar.web.controller.base.LoggedUserController;
import br.com.kotar.web.service.UsuarioService;
import br.com.kotar.web.service.UsuarioTokenService;

@RestController
@RequestMapping(value = { "/api/secure/usuario" })
public class UsuarioController extends LoggedUserController<Usuario> {

	//@formatter:off
	@Autowired UsuarioService usuarioService;
	@Autowired UsuarioTokenService usuarioTokenService;
	//@formatter:on

	@Override
	public BaseCrudService<Usuario> getService() {
		return usuarioService;
	}

	@Override
	protected Usuario onSave(Usuario domain) throws Exception {
		return usuarioService.saveOrUpdate(domain);
	}

	@Override
	protected Usuario onUpdate(Usuario domain) throws Exception {
		getLoggedUser();
		return usuarioService.saveOrUpdate(domain);
	}

	@RequestMapping(value = "/novo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Usuario>> newUser() {
		return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(new Usuario()), HttpStatus.OK);
	}

	@RequestMapping(value = "/get/situacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<List<SituacaoUsuarioType>>> getSituacoes() {
		List<SituacaoUsuarioType> list = Arrays.asList(SituacaoUsuarioType.values());
		ResponseHelper<List<SituacaoUsuarioType>> ret = new ResponseHelper<List<SituacaoUsuarioType>>(list);

		return new ResponseEntity<ResponseHelper<List<SituacaoUsuarioType>>>(ret, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/atualizar-push-token/{token}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Boolean>> atualizarUsuarioPushToken(@PathVariable("token") String pushTokenId) throws Exception {
		try {			
			
			if (!StringUtil.temValor(pushTokenId)) {
				String message = messages.get("usuario.token.invalido");
				throw new Exception(message);
			}
			
			Usuario usuario = getLoggedUser();
			
			String kotharToken = usuario.getToken();
			
			UsuarioToken usuarioToken = usuarioTokenService.findByKotharToken(kotharToken);
			
			if (usuarioToken != null) {
				String pushToken = usuarioToken.getPushTokenId(); 
				if (!(StringUtil.temValor(pushToken) && pushToken.trim().compareTo(pushTokenId.trim()) == 0)) {
					usuarioToken.setPushTokenId(pushTokenId);
					usuarioTokenService.saveOrUpdate(usuarioToken);
				}
			}
			else {
				String kotharSource = request.getHeader("Kothar-Source");
				usuarioTokenService.criarUsuarioToken(pushTokenId, usuario, kotharToken, kotharSource);
			}			
			
			return new ResponseEntity<ResponseHelper<Boolean>>(new ResponseHelper<Boolean>(Boolean.TRUE), HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<Boolean>>(new ResponseHelper<Boolean>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}
}