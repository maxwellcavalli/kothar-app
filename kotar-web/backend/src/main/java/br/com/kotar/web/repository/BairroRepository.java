package br.com.kotar.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Bairro;

import java.util.Optional;

public interface BairroRepository extends CrudRepository<Bairro>{
	
	//@formatter:off
	@Query(value = "select bairro "
			+ "       from Bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado "
			+ "      where upper(bairro.nome) like upper(:bairro) "
			+ "        and upper(cidade.nome) like upper(:cidade) ",
			
			countQuery="select count(bairro) "
					+ "       from Bairro bairro "
					+ "      inner join bairro.cidade cidade "
					+ "      inner join cidade.estado "
					+ "      where upper(bairro.nome) like upper(:bairro) "
					+ "        and upper(cidade.nome) like upper(:cidade) "
			)
	public Page<Bairro> findByBairroAndCidadeLikeIgnoreCase(@Param("bairro") String bairro, @Param("cidade") String cidade, Pageable pageable);

	@Query(value = "select bairro "
			+ "       from Bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado "
			+ "      where bairro.id = :id ")
	public Optional<Bairro> findById(@Param("id") Long id);
	
	
	@Query(value = "select bairro "
			+ "       from Bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado "
			+ "      where upper(bairro.nome) like upper(:bairro) "
			+ "        and upper(cidade.id) like upper(:cidade) ",
			countQuery="select count(bairro) "
					+ "       from Bairro bairro "
					+ "      inner join bairro.cidade cidade "
					+ "      inner join cidade.estado "
					+ "      where upper(bairro.nome) like upper(:bairro) "
					+ "        and upper(cidade.id) like upper(:cidade) "
					)
	public Page<Bairro> findByBairroIdCidadeLikeIgnoreCase(@Param("bairro") String bairro, 
			@Param("cidade") Long cidade, Pageable pageable);
	
	//@formatter:on
	
	
}