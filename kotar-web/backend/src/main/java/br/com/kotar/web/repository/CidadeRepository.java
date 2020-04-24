package br.com.kotar.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Cidade;

import java.util.Optional;

public interface CidadeRepository extends CrudRepository<Cidade>{
	
	//@formatter:off
	@Query(value = "select cidade "
			+ "       from Cidade cidade "
			+ "      inner join fetch cidade.estado"
			+ "      where upper(cidade.nome) like upper(:nome) ",
			countQuery="select count(cidade) "
					+ "       from Cidade cidade "
					+ "      where upper(cidade.nome) like upper(:nome) ")
	public Page<Cidade> findByNomeLikeIgnoreCase(@Param("nome") String name, Pageable pageable);

	@Query(value = "select cidade "
			+ "       from Cidade cidade "
			+ "      inner join fetch cidade.estado"
			+ "      where cidade.id = :id ")
	public Optional<Cidade> findById(@Param("id") Long id);
	
	
	@Query(value = "select cidade "
			+ "       from Cidade cidade "
			+ "      inner join fetch cidade.estado estado"
			+ "      where upper(cidade.nome) like upper(:cidade) "
			+ "        and upper(estado.nome) like upper(:estado) ",
			countQuery="select count(cidade) "
					+ "       from Cidade cidade "
					+ "      inner join cidade.estado estado"
					+ "      where upper(cidade.nome) like upper(:cidade) "
					+ "        and upper(estado.nome) like upper(:estado) "
					)
	public Page<Cidade> findByCidadeEstadoLikeIgnoreCase(@Param("cidade") String cidade, 
			@Param("estado") String estado, Pageable pageable);
	
	
	@Query(value = "select cidade "
			+ "       from Cidade cidade "
			+ "      inner join fetch cidade.estado estado"
			+ "      where upper(cidade.nome) like upper(:cidade) "
			+ "        and estado.id = :estado ",
			countQuery="select count(cidade) "
					+ "       from Cidade cidade "
					+ "      inner join cidade.estado estado"
					+ "      where upper(cidade.nome) like upper(:cidade) "
					+ "        and estado.id  = :estado "
					)
	public Page<Cidade> findByCidadeIdEstadoLikeIgnoreCase(@Param("cidade") String cidade, 
			@Param("estado") Long estado, Pageable pageable);
	
	//@formatter:on
	
}