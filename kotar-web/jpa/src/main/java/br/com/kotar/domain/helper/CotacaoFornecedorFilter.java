package br.com.kotar.domain.helper;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;

import br.com.kotar.domain.business.Cliente;

public class CotacaoFornecedorFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Cliente cliente;
	
	private Pageable pageable;

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

}
