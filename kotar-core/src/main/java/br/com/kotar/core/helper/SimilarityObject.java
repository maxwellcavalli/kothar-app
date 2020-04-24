package br.com.kotar.core.helper;

import java.io.Serializable;

public class SimilarityObject<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T data;
	private Double distance;

	public double getPercentual() {
		return distance * 100;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

}
