package br.com.kotar.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.domain.security.UsuarioToken;

public interface UsuarioTokenRepository extends BaseRepository<UsuarioToken> {

	//@formatter:off
	@Query(value = "select usuarioToken "
			+ "       from UsuarioToken usuarioToken "
			+ "      where usuarioToken.usuario = :usuario "
			)
	public List<UsuarioToken> findByUsuario(@Param("usuario") Usuario usuario);
	
	@Query(value = "select usuarioToken "
			+ "       from UsuarioToken usuarioToken "
			+ "      where usuarioToken.kotharToken = :kotharToken "
			)
	public UsuarioToken findByKotharToken(@Param("kotharToken") String kotharToken);
	
	@Query(value = "select usuarioToken "
			+ "       from UsuarioToken usuarioToken "
			+ "      where usuarioToken.pushTokenId = :pushToken "
			)
	public UsuarioToken findByPushToken(@Param("pushToken") String pushToken);		
	
	//@formatter:on
}