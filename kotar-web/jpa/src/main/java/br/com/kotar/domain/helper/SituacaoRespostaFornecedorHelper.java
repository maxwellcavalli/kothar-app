package br.com.kotar.domain.helper;

import java.io.Serializable;

import br.com.kotar.domain.business.type.SituacaoRespostaFornecedorType;

public class SituacaoRespostaFornecedorHelper implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private SituacaoRespostaFornecedorType value;
	private String description;

	public SituacaoRespostaFornecedorType getValue() {
		return value;
	}

	public void setValue(SituacaoRespostaFornecedorType value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
