package br.com.kotar.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Estado;

import java.util.Optional;

public interface EstadoRepository extends CrudRepository<Estado>{

	public Page<Estado> findByNomeLikeIgnoreCase(@Param("nome") String name, Pageable pageable);

	public Optional<Estado> findById(@Param("id") Long id);
	
}