package br.com.kotar.core.domain;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public abstract Long getId();

	public abstract void setId(Long id);
}
