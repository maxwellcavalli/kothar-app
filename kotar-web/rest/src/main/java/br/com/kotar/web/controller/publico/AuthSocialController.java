package br.com.kotar.web.controller.publico;

import java.util.Calendar;
import java.util.HashSet;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.kotar.core.helper.response.ResponseHelper;
import br.com.kotar.core.util.Constants;
import br.com.kotar.core.util.StringUtil;
import br.com.kotar.core.util.http.HttpUtil;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.security.LogUsuario;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.ClienteRepository;
import br.com.kotar.web.repository.UsuarioRepository;
import br.com.kotar.web.service.ClienteService;
import br.com.kotar.web.service.UsuarioService;
 
@RestController
@RequestMapping(value = { "/api/authenticatesocial" })
public class AuthSocialController {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	UsuarioService usuarioService;
	
	@Autowired
	ClienteService clienteService;

	@Autowired
	Environment env;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ResponseHelper<Usuario>> createDomain(@RequestBody Usuario domain) {
		try {
			if (Constants.HAS_PROXY == null) {
				HttpUtil.init(env);	
			}
			
			Usuario usuario = null;
			
			//   https://graph.facebook.com/me?locale=en_US&fields=name,email&access_token={token}
			if (StringUtil.temValor(domain.getIdFacebook())) {
				
				usuario = usuarioRepository.findByIdFacebook(domain.getIdFacebook());
				domain = usuarioService.validateFacebookTokenUser(domain.getIdFacebook(), domain.getTokenFacebook(), domain);
			} 
			else if (StringUtil.temValor(domain.getIdGoogle())) {
				
				usuario = usuarioRepository.findByIdGoogle(domain.getIdGoogle());
				domain = usuarioService.validateGoogleTokenUser(domain.getIdGoogle(), domain.getTokenGoogle(), domain);
			}
			
			if (usuario != null) {
				domain.setId(usuario.getId());
			}
			
			if (domain.getLogAcesso() == null) {
				domain.setLogAcesso(new HashSet<>());
			}
			domain.setLastLogin(Calendar.getInstance().getTime());
			domain.getLogAcesso().add(new LogUsuario(domain, Calendar.getInstance().getTime()));
			
			Cliente cliente = new Cliente();
			if (domain.getId() != null) {
				cliente = clienteRepository.findByUsuario(domain);
			}			
			cliente.setUsuario(domain);
			cliente.setNome(domain.getNome());
			clienteService.saveOrUpdate(cliente);				
			
			domain.setLoginCliente(true);						
			domain.setToken(usuarioService.generateToken(domain));
			
			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(domain), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			String message = ExceptionUtils.getRootCauseMessage(e);
			return new ResponseEntity<ResponseHelper<Usuario>>(new ResponseHelper<Usuario>(message), HttpStatus.EXPECTATION_FAILED);
		}
	}

}
