package br.com.kotar.core.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;


@MappedSuperclass
public abstract class CrudDomain extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Column(name = "nome")
	private String nome;
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
