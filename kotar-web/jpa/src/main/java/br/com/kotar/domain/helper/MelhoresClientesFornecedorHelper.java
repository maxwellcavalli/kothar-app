package br.com.kotar.domain.helper;

import java.io.Serializable;

public class MelhoresClientesFornecedorHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome;
	private String valor;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
