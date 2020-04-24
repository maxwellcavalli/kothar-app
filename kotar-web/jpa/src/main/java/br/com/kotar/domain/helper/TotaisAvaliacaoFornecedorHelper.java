package br.com.kotar.domain.helper;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.kotar.domain.business.Fornecedor;

public class TotaisAvaliacaoFornecedorHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long total1Estrela;
	
	private Long total2Estrelas;
	
	private Long total3Estrelas;
	
	private Long total4Estrelas;
	
	private Long total5Estrelas;
	
	private Long totalAvaliacoes;
	
	private Fornecedor fornecedor;
	
	private BigDecimal mediaAvaliacoes;
	
	public TotaisAvaliacaoFornecedorHelper() {}

	public Long getTotal1Estrela() {
		return total1Estrela;
	}

	public void setTotal1Estrela(Long total1Estrela) {
		this.total1Estrela = total1Estrela;
	}

	public Long getTotal2Estrelas() {
		return total2Estrelas;
	}

	public void setTotal2Estrelas(Long total2Estrelas) {
		this.total2Estrelas = total2Estrelas;
	}

	public Long getTotal3Estrelas() {
		return total3Estrelas;
	}

	public void setTotal3Estrelas(Long total3Estrelas) {
		this.total3Estrelas = total3Estrelas;
	}

	public Long getTotal4Estrelas() {
		return total4Estrelas;
	}

	public void setTotal4Estrelas(Long total4Estrelas) {
		this.total4Estrelas = total4Estrelas;
	}

	public Long getTotal5Estrelas() {
		return total5Estrelas;
	}

	public void setTotal5Estrelas(Long total5Estrelas) {
		this.total5Estrelas = total5Estrelas;
	}

	public Long getTotalAvaliacoes() {
		return totalAvaliacoes;
	}

	public void setTotalAvaliacoes(Long totalAvaliacoes) {
		this.totalAvaliacoes = totalAvaliacoes;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public BigDecimal getMediaAvaliacoes() {
		return mediaAvaliacoes;
	}

	public void setMediaAvaliacoes(BigDecimal mediaAvaliacoes) {
		this.mediaAvaliacoes = mediaAvaliacoes;
	}
}
