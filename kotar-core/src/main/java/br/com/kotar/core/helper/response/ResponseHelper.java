package br.com.kotar.core.helper.response;

import java.io.Serializable;


public class ResponseHelper<T extends Object> implements Serializable {
	private static final long serialVersionUID = 1L;

	private T object;
	private String errorMessage;

	public ResponseHelper() {
	}

	public ResponseHelper(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public ResponseHelper(T object) {
		super();
		this.object = object;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
