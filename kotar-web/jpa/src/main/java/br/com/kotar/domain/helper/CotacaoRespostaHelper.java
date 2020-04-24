package br.com.kotar.domain.helper;

import java.io.Serializable;

import br.com.kotar.domain.business.Fornecedor;

public class CotacaoRespostaHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private Fornecedor fornecedor;
	
	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
