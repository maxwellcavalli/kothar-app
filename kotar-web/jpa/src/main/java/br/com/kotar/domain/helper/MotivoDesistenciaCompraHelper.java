package br.com.kotar.domain.helper;

import java.io.Serializable;

import br.com.kotar.domain.business.type.MotivoDesistenciaCompraType;

public class MotivoDesistenciaCompraHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	private MotivoDesistenciaCompraType value;
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MotivoDesistenciaCompraType getValue() {
		return value;
	}

	public void setValue(MotivoDesistenciaCompraType value) {
		this.value = value;
	}

}
