package br.com.kotar.domain.helper;

import java.io.Serializable;

import br.com.kotar.domain.business.type.TipoJurosPagamentoType;

public class TipoJurosPagamentoHelper implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private TipoJurosPagamentoType value;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TipoJurosPagamentoType getValue() {
		return value;
	}

	public void setValue(TipoJurosPagamentoType value) {
		this.value = value;
	}
}