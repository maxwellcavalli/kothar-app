package br.com.kotar.core.repository.impl;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.domain.CrudDomain;
import br.com.kotar.core.repository.CrudRepository;

public abstract class BaseCrudRepositoryImpl<T extends CrudDomain> extends BaseRepositoryImpl<T> implements CrudRepository<T> {

	//@formatter:off
	@Autowired protected EntityManager em;	
	@Autowired protected Messages messages;
	//@formatter:on

	public abstract CrudRepository<T> getRepository();
	
	public T findByNome(String nome) {
		return getRepository().findByNome(nome);
	}

	@Override
	public Page<T> findByNomeLikeIgnoreCase(String name, Pageable pageable) {
		return getRepository().findByNomeLikeIgnoreCase(name, pageable);
	}
	
}
