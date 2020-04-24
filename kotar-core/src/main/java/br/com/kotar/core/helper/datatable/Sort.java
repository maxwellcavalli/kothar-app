package br.com.kotar.core.helper.datatable;

import java.io.Serializable;

public class Sort implements Serializable {

	private static final long serialVersionUID = 1L;

	private String propertyName;
	private String orderType; // asc - desc

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

}
