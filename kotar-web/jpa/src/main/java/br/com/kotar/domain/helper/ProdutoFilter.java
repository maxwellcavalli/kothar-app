package br.com.kotar.domain.helper;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;

public class ProdutoFilter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String nome;
	private Pageable pageable;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
	

}
