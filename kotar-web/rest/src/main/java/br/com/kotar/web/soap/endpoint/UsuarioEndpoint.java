package br.com.kotar.web.soap.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.service.UsuarioService;
import br.com.kotar.web.soap.endpoint.base.BaseEndpoint;
import br.com.kotar.web.soap.schema.usuario.GetTokenRequest;
import br.com.kotar.web.soap.schema.usuario.GetTokenResponse;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

@Endpoint 
public class UsuarioEndpoint extends BaseEndpoint {

	//@formatter:off
	@Autowired UsuarioService usuarioService;
	//@formatter:on

	private static final String NAMESPACE_URI = "http://kotar.com.br/web/soap/schema/usuario";

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getTokenRequest")
	@ResponsePayload
	public GetTokenResponse getProdutos(@RequestPayload GetTokenRequest request) throws Exception {
		GetTokenResponse response = new GetTokenResponse();
		
		String username = request.getLogin();
		String password = request.getPassword();
		
		Usuario usuario = null;
		try {
			usuario = usuarioService.validateUser(username, password);
			
			Cache cache = cacheManager.getCache("userCache");		
			Element element = new Element(usuario.getToken(), usuario);
			cache.put(element);
			
		} catch (Exception e) {
			response.setMensagem(e.getMessage());
			response.setError(true);
		}
		
		if (usuario != null){
			response.setToken(usuario.getToken());
			response.setError(false);
		}
		
		return response;
	}

	
}
