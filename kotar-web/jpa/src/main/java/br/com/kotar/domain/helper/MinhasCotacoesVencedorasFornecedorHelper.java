package br.com.kotar.domain.helper;

import java.io.Serializable;

public class MinhasCotacoesVencedorasFornecedorHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer mes;
	private Integer ano;
	private Long quantidade;
	private Long total;
	
	public String getMesAno() {
		return this.mes + "/"+ this.ano;
	}
	
	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
