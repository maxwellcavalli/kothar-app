package br.com.kotar.domain.helper;

import java.io.Serializable;
import java.math.BigDecimal;

public class AvaliacaoFornecedorHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long total;
	
	private BigDecimal estrelas;

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public BigDecimal getEstrelas() {
		return estrelas;
	}

	public void setEstrelas(BigDecimal estrelas) {
		this.estrelas = estrelas;
	}

	
}
