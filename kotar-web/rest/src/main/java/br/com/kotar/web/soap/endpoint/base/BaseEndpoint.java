package br.com.kotar.web.soap.endpoint.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.kotar.core.component.Messages;
import br.com.kotar.domain.security.Usuario;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Component
public class BaseEndpoint {

	@Autowired protected CacheManager cacheManager;
	@Autowired protected Messages messages;
	
	protected Usuario validateToken(String token) throws Exception{
		
		if (token == null || token.trim().isEmpty()){
			throw new Exception("Invalid token");
		}
		
		Cache cache = cacheManager.getCache("userCache");
		Usuario user = null;
		if (cache.isElementInMemory(token)){
			Element element = cache.get(token);
			user = (Usuario) element.getObjectValue();
		}
				
		if (user == null){
			throw new Exception("Invalid token");
		}
		
		return user;
	}
	
}
