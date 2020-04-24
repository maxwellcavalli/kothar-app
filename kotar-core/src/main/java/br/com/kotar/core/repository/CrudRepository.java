package br.com.kotar.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.domain.CrudDomain;

@NoRepositoryBean
public interface CrudRepository<T extends CrudDomain> extends BaseRepository<T>{
	
	T findByNome(@Param("nome") String nome);
	
	Page<T> findByNomeLikeIgnoreCase(@Param("nome") String name, Pageable pageable);
	
} 