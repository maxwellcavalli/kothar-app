package br.com.kotar.core.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T> extends PagingAndSortingRepository<T, Long>{

	Optional<T> findById(@Param("id") Long id);

}