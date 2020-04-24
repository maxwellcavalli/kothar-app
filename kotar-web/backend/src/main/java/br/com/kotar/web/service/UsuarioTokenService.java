package br.com.kotar.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.domain.security.UsuarioToken;
import br.com.kotar.web.repository.UsuarioTokenRepository;

@Service
public class UsuarioTokenService extends BaseService<UsuarioToken> {

	//@formatter:off
	@Autowired UsuarioTokenRepository usuarioTokenRepository;
	//@formatter:on

	@Override
	public BaseRepository<UsuarioToken> getRepository() {
		return usuarioTokenRepository;
	}
	
	public List<UsuarioToken> findByUsuario(Usuario usuario) {
		return usuarioTokenRepository.findByUsuario(usuario);
	}
	
	public UsuarioToken findByKotharToken(String kotharToken) {
		return usuarioTokenRepository.findByKotharToken(kotharToken);
	}
	
	public UsuarioToken findByPushToken(String pushToken) {
		return usuarioTokenRepository.findByPushToken(pushToken);
	}
	
	public void criarUsuarioToken(String pushTokenId, Usuario usuario, String kotharToken, String kotharSource) throws Exception {
		UsuarioToken usuarioToken = usuarioTokenRepository.findByPushToken(pushTokenId);
		if (usuarioToken != null) {
			usuarioTokenRepository.delete(usuarioToken);
		}
		
		usuarioToken = new UsuarioToken();
		usuarioToken.setUsuario(usuario);
		usuarioToken.setPushTokenId(pushTokenId);
		usuarioToken.setKotharToken(kotharToken);
		usuarioToken.setOrigem(kotharSource);
		saveOrUpdate(usuarioToken);
	}	
}