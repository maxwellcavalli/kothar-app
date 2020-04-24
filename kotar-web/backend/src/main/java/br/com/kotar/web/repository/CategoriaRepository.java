package br.com.kotar.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Categoria;

import java.util.Optional;

public interface CategoriaRepository extends CrudRepository<Categoria> {

	public Page<Categoria> findByNomeLikeIgnoreCase(@Param("nome") String nome, Pageable pageable);

	public Optional<Categoria> findById(@Param("id") Long id);

}