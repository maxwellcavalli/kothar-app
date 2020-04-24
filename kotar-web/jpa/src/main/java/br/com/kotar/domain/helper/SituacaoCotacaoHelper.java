package br.com.kotar.domain.helper;

import java.io.Serializable;

import br.com.kotar.domain.business.type.SituacaoCotacaoType;

public class SituacaoCotacaoHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	private SituacaoCotacaoType value;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SituacaoCotacaoType getValue() {
		return value;
	}

	public void setValue(SituacaoCotacaoType value) {
		this.value = value;
	}

}
