package br.com.kotar.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.kotar.core.domain.CrudDomain;
import br.com.kotar.core.repository.CrudRepository;

public abstract class BaseCrudService<T extends CrudDomain> extends BaseService<T>{
	
	public abstract CrudRepository<T> getRepository();
		
	public T findByNome(String nome){
		return (T) getRepository().findByNome(nome);
	}
	
	public Page<T> findByNomeLikeIgnoreCase(String name, Pageable pageable){
		return (Page<T>) getRepository().findByNomeLikeIgnoreCase(name, pageable);
	}

}
