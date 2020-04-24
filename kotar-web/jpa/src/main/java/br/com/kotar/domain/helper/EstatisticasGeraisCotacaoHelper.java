package br.com.kotar.domain.helper;

import java.io.Serializable;
import java.math.BigDecimal;

public class EstatisticasGeraisCotacaoHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigDecimal menorValor;
	private BigDecimal maiorValor;
	private BigDecimal percentual;
	
	private Integer ano;
	private Integer mes;
	
	private Long cotacaoId;
	private String cotacaoNome;
	
	

	public BigDecimal getMenorValor() {
		return menorValor;
	}

	public void setMenorValor(BigDecimal menorValor) {
		this.menorValor = menorValor;
	}

	public BigDecimal getMaiorValor() {
		return maiorValor;
	}

	public void setMaiorValor(BigDecimal maiorValor) {
		this.maiorValor = maiorValor;
	}

	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Long getCotacaoId() {
		return cotacaoId;
	}

	public void setCotacaoId(Long cotacaoId) {
		this.cotacaoId = cotacaoId;
	}

	public String getCotacaoNome() {
		return cotacaoNome;
	}

	public void setCotacaoNome(String cotacaoNome) {
		this.cotacaoNome = cotacaoNome;
	}

}
