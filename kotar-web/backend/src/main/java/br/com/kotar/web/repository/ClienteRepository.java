package br.com.kotar.web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.security.Usuario;

import java.util.Optional;

public interface ClienteRepository extends CrudRepository<Cliente> {

	//@formatter:off
	@Query(value = "select cliente "
			+ "       from Cliente cliente "
			+ "      inner join fetch cliente.usuario "
			//+ "       left join fetch cliente.cep cep "
			//+ "       left join fetch cep.bairro bairro "
			//+ "       left join fetch bairro.cidade cidade "
			//+ "       left join fetch cidade.estado estado "
			//+ "       left join fetch cliente.enderecoComplemento "	
			+ "      where cliente.id = :id ")
	public Optional<Cliente> findById(@Param("id") Long id);
	
	@Query(value = "select cliente "
			+ "       from Cliente cliente "
			+ "      inner join fetch cliente.usuario "
			+ "      where cliente.usuario = :usuario ") 
	public Cliente findByUsuario(@Param("usuario") Usuario usuario);
	
	//@formatter:on

}