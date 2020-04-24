package br.com.kotar.web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.ConfiguracaoServidor;

import java.util.Optional;

public interface ConfiguracaoServidorRepository extends BaseRepository<ConfiguracaoServidor> {

	//@formatter:off
	@Query(value = "select configuracaoServidor "
			+ "       from ConfiguracaoServidor configuracaoServidor "
			+ "      where configuracaoServidor.id = :id "
			)
	public Optional<ConfiguracaoServidor> findById(@Param("id") Long id);
	//@formatter:on
}