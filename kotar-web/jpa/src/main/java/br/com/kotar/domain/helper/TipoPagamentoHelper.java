package br.com.kotar.domain.helper;

import java.io.Serializable;

import br.com.kotar.domain.business.type.TipoPagamentoType;

public class TipoPagamentoHelper implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private TipoPagamentoType value;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TipoPagamentoType getValue() {
		return value;
	}

	public void setValue(TipoPagamentoType value) {
		this.value = value;
	}

}
