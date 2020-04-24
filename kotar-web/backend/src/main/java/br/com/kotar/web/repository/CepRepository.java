package br.com.kotar.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Cep;

import java.util.Optional;

public interface CepRepository extends CrudRepository<Cep> {

	//@formatter:off
	@Query(value = "select cep "
			+ "       from Cep cep "
			+ "      inner join fetch cep.bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado "
			+ "      where upper(cep.nome) like upper(:cep) "
			+ "        and upper(bairro.nome) like upper(:bairro) "
			+ "        and upper(cidade.nome) like upper(:cidade) ",
			
			countQuery="select count(cep)"
					+ "       from Cep cep "
					+ "      inner join cep.bairro bairro "
					+ "      inner join bairro.cidade cidade "
					+ "      inner join cidade.estado "
					+ "      where upper(cep.nome) like upper(:cep) "
					+ "        and upper(bairro.nome) like upper(:bairro) "
					+ "        and upper(cidade.nome) like upper(:cidade) "
			)
	public Page<Cep> findByCepAndBairroAndCidadeLikeIgnoreCase(@Param("cep") String cep, @Param("bairro") String bairro, 
			@Param("cidade") String cidade, Pageable pageable);

	@Query(value = "select cep "
			+ "       from Cep cep "
			+ "      inner join fetch cep.bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado "
			+ "      where cep.id = :id "
			)
	public Optional<Cep> findById(@Param("id") Long id);
	
	@Query(value = "select cep "
			+ "       from Cep cep "
			+ "      inner join fetch cep.bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado "
			+ "      where cep.codigoPostal = :codigoPostal "
			)
	public Cep findByCodigoPostal(@Param("codigoPostal") String codigoPostal);
	
	//@formatter:on
}