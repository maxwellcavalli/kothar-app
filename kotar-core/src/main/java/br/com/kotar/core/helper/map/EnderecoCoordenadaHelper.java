package br.com.kotar.core.helper.map;

import java.io.Serializable;

public class EnderecoCoordenadaHelper implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String cdCache;
	private String dsCidade;
	private String dsLogradouro;
	private String nrNumero;
	private String nrLatitude;
	private String nrLongitude;

	public EnderecoCoordenadaHelper() {
		super();
	}

	public EnderecoCoordenadaHelper(String cdCache, String dsCidade,
			String dsLogradouro, String nrNumero, String nrLatitude,
			String nrLongitude) {
		super();
		this.cdCache = cdCache;
		this.setDsCidade(dsCidade);
		this.dsLogradouro = dsLogradouro;
		this.nrNumero = nrNumero;
		this.nrLatitude = nrLatitude;
		this.nrLongitude = nrLongitude;
	}

	public String getCdCache() {
		return cdCache;
	}

	public void setCdCache(String cdCache) {
		this.cdCache = cdCache;
	}

	public String getDsLogradouro() {
		return dsLogradouro;
	}

	public void setDsLogradouro(String dsLogradouro) {
		this.dsLogradouro = dsLogradouro;
	}

	public String getNrNumero() {
		return nrNumero;
	}

	public void setNrNumero(String nrNumero) {
		this.nrNumero = nrNumero;
	}

	public String getNrLatitude() {
		return nrLatitude;
	}

	public void setNrLatitude(String nrLatitude) {
		this.nrLatitude = nrLatitude;
	}

	public String getNrLongitude() {
		return nrLongitude;
	}

	public void setNrLongitude(String nrLongitude) {
		this.nrLongitude = nrLongitude;
	}

	public String getDsCidade() {
		return dsCidade;
	}

	public void setDsCidade(String dsCidade) {
		this.dsCidade = dsCidade;
	}
}
