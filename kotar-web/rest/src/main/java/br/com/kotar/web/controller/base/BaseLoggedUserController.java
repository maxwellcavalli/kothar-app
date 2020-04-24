package br.com.kotar.web.controller.base;

import javax.servlet.http.HttpServletRequest;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.kotar.core.controller.BaseController;
import br.com.kotar.core.domain.BaseDomain;
import br.com.kotar.core.exception.InvalidTokenException;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.service.UsuarioService;
import br.com.kotar.web.service.UsuarioTokenService;
import br.com.kotar.domain.security.UsuarioToken;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.util.Optional;

public abstract class BaseLoggedUserController<T extends BaseDomain> extends BaseController<T> {

	//@formatter:off
	@Autowired protected HttpServletRequest request;
	@Autowired protected CacheManager cacheManager;
	
	@Autowired protected UsuarioTokenService usuarioTokenService;
	@Autowired protected UsuarioService usuarioService;
	
	//@formatter:on

	protected Usuario getLoggedUser() throws InvalidTokenException {
		final String token = getToken();
		
		Usuario user = null;
		Cache cache = cacheManager.getCache("userCache");
		if (cache.isElementInMemory(token)){
			Element element = cache.get(token);
			user = (Usuario) element.getObjectValue();
		}
		
		if (user == null){
			
			UsuarioToken usuarioToken = usuarioTokenService.findByKotharToken(token);
			if (usuarioToken != null) {
				Optional<Usuario> usuarioOptional = usuarioService.findById(usuarioToken.getUsuario().getId());

				if (!usuarioOptional.isPresent()){
					throw new RecordNotFound();
				}

				Usuario usuario = usuarioOptional.get();
				usuario.setToken(token);
				user = usuario;
				
				Element element = new Element(usuario.getToken(), usuario);
				cache.put(element);
			}
			else {
				throw new InvalidTokenException();
			}
		}

		return user;
	}
	
	protected String getToken(){
		final String authHeader = request.getHeader("authorization");
		final String token = authHeader.substring(7);
		
		return token;
	}

	@Override
	public void beforeSave(T t) {
	}

	@Override
	public void validationBeforeSave(T t) throws Exception {
	}
	
	@Override
	public BaseService<T> getService() {
		return null;
	}

}
