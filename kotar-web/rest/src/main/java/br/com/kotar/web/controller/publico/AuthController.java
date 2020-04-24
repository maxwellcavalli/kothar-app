package br.com.kotar.web.controller.publico;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.UsuarioService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@RestController
@RequestMapping(value = { "/api/authenticate" })
public class AuthController extends BaseLoggedUserController<Usuario> {

	//@formatter:off
	@Autowired UsuarioService usuarioService;
	@Autowired CacheManager cacheManager;
	//@formatter:on

	@Override
	protected Usuario onSave(Usuario domain) throws Exception {
		Usuario usuario = usuarioService.validateUser(domain.getUsername(), domain.getPasswordLogin());

		// System.out.println(cacheManager);
		Cache cache = cacheManager.getCache("userCache");
		Element element = new Element(usuario.getToken(), usuario);
		cache.put(element);

		return usuario;
	}

	@RequestMapping(value = "/profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Object>> getProfile() throws Exception {
		try {
			Usuario usuario = null;
			try {
				usuario = getLoggedUser();
			} catch (InvalidTokenException e) {
			} catch (Exception e) {
				throw e;
			}

			String retorno = "";

			if (usuario != null) {
				if (usuario.isAdmin()) {
					retorno = "ADMIN";
				} else if (usuario.isLoginCliente()) {
					retorno = "CLIENTE";
				}
			}

			Object obj = retorno;

			ResponseHelper<Object> ret = new ResponseHelper<>(obj);
			return new ResponseEntity<ResponseHelper<Object>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Object> ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Object>>(ret, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/logged", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Usuario>> getUser() throws Exception {
		try {
			Usuario usuario = null;
			try {
				usuario = getLoggedUser();
				if (usuario != null) {
					usuario.setToken(getToken());
				}

			} catch (InvalidTokenException ee) {
			} catch (Exception e) {
				throw e;
			}
			ResponseHelper<Usuario> ret = new ResponseHelper<>(usuario);
			return new ResponseEntity<ResponseHelper<Usuario>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Usuario> ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Usuario>>(ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
	
	@RequestMapping(value = "/clear-cache", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> clearCache() throws Exception {
		try {
			Cache cache = cacheManager.getCache("userCache");
			cache.removeAll();
			
			return new ResponseEntity<String>("OK", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("ERROR", HttpStatus.EXPECTATION_FAILED);
		}
	}

	@RequestMapping(value = "/validate-token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResponseHelper<Boolean>> validateToken() throws Exception {
		try {
			Usuario usuario = null;
			try {
				usuario = getLoggedUser();
			} catch (Exception e) {
			}

			Boolean isValid = usuario != null;

			ResponseHelper<Boolean> ret = new ResponseHelper<>(isValid);
			return new ResponseEntity<ResponseHelper<Boolean>>(ret, HttpStatus.OK);
		} catch (Exception e) {
			String message = ExceptionUtils.getRootCauseMessage(e);
			ResponseHelper<Boolean> ret = new ResponseHelper<>(message);
			return new ResponseEntity<ResponseHelper<Boolean>>(ret, HttpStatus.EXPECTATION_FAILED);
		}
	}
}
