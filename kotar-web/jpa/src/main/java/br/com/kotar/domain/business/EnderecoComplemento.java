package br.com.kotar.domain.business;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "endereco_complemento")
public class EnderecoComplemento extends BaseDomain implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ENDERECO_COMPLEMENTO_ID", nullable = false)
	@JsonProperty("endereco_complemento_id")
	private Long id;

	@Column(name = "NUMERO", nullable = true, length = 10)
	private String numero;

	@Column(name = "COMPLEMENTO", nullable = true, length = 2000)
	private String complemento;
	
	@Column(name = "LATITUDE", nullable = true, scale=14, precision=10, columnDefinition="NUMERIC(14,10)")
	private BigDecimal latitude;
	
	@Column(name = "LONGITUDE", nullable = true, scale=14, precision=10, columnDefinition="NUMERIC(14,10)")
	private BigDecimal longitude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	@Override
	public EnderecoComplemento clone() throws CloneNotSupportedException {
		return (EnderecoComplemento) super.clone();
	}

}