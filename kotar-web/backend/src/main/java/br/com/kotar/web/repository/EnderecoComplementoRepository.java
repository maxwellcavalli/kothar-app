package br.com.kotar.web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.EnderecoComplemento;

import java.util.Optional;

public interface EnderecoComplementoRepository extends BaseRepository<EnderecoComplemento> {

	//@formatter:off

	@Query(value = "select enderecoComplemento "
			+ "       from EnderecoComplemento enderecoComplemento "
			+ "      where enderecoComplemento.id = :id "
			)
	public Optional<EnderecoComplemento> findById(@Param("id") Long id);
	

	
	//@formatter:on
}