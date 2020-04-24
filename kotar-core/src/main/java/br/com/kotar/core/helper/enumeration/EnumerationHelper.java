package br.com.kotar.core.helper.enumeration;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.enumeration.BaseEnum;

public class EnumerationHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	private BaseEnum enumeration;
	
	public EnumerationHelper() {
	}
	
	public EnumerationHelper(BaseEnum enumeration) {
		super();
		this.enumeration = enumeration;
	}

	@JsonProperty("description")
	public String getDescription() {
		return enumeration.getDescription();
	}

	public BaseEnum getEnumeration() {
		return enumeration;
	}

	public void setEnumeration(BaseEnum enumeration) {
		this.enumeration = enumeration;
	}
}
