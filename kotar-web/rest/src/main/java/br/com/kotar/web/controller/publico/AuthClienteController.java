package br.com.kotar.web.controller.publico;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.controller.base.BaseLoggedUserController;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.UsuarioService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
 
@RestController
@RequestMapping(value = { "/api/authenticatecliente" })
public class AuthClienteController extends BaseLoggedUserController<Usuario> {
	
	//@formatter:off
	@Autowired UsuarioService usuarioService;
	@Autowired ClienteService clienteService;
	//@formatter:on
	
	@Override
	protected Usuario onSave(Usuario domain) throws Exception {
		Usuario usuario = usuarioService.validateUser(domain.getUsername(), domain.getPasswordLogin());			
		
		// System.out.println(cacheManager);
		Cache cache = cacheManager.getCache("userCache");
		Element element = new Element(usuario.getToken(), usuario);
		cache.put(element);
		 
		if (usuario.getId() != null) { 
			Cliente cliente = clienteService.findByUsuario(usuario);
			
			if (cliente == null) {
				cliente = new Cliente();
				cliente.setUsuario(usuario);
				cliente.setNome(usuario.getNome());
				cliente = clienteService.saveOrUpdate(cliente);
				
				usuario = cliente.getUsuario(); 
			}
		}
		
		return usuario;
	}
	
	
//	@RequestMapping(method = RequestMethod.POST)
//	public ResponseEntity<ResponseHelper<Usuario>> createDomain(@RequestBody Usuario domain) {
//		try {
//			
//			
//			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(usuario), HttpStatus.OK);
//		} catch (Exception e) {
//			String message = ExceptionUtils.getRootCauseMessage(e);
//			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(message), HttpStatus.EXPECTATION_FAILED);
//		}
//	}
}
