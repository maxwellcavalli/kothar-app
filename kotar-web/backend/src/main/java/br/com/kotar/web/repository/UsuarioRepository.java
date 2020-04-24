package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.security.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario> {

	//@formatter:off
	@Query(value = "select usuario "
			+ "       from Usuario usuario "
			+ "      where upper(usuario.nome) like upper(:nome) ",
			countQuery="select count(usuario) "
					+ "   from Usuario usuario "
					+ "  where upper(usuario.nome) like upper(:nome) ")
	public Page<Usuario> findByNomeLikeIgnoreCase(@Param("nome") String nome, Pageable pageable);

	@Query(value = "select usuario "
			+ "       from Usuario usuario "
			+ "      where usuario.id = :id ")
	public Optional<Usuario> findById(@Param("id") Long id);
	
	public List<Usuario> findByUsernameEqualsIgnoreCase(@Param("username") String username);
	
	public Usuario findByUsernameAndPassword(String username, String password);
	
	public Usuario findByEmail(String email);
	
	public Usuario findByIdFacebook(String idFacebook);
	
	public Usuario findByIdGoogle(String idGoogle);
	
	public Usuario findByResetToken(String resetToken);

		
	//@formatter:on
}