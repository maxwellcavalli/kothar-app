package br.com.kotar.domain.helper;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;

public class AvaliacaoFilter implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long idFornecedor;
	private Pageable pageable;

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public Long getIdFornecedor() {
		return idFornecedor;
	}

	public void setIdFornecedor(Long idFornecedor) {
		this.idFornecedor = idFornecedor;
	}
}