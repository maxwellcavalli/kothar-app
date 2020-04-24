package br.com.kotar.domain.helper;

import java.io.Serializable;

public class EstatisticasCotacaoFornecedorHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long recusado;

	private Long pendente;

	private Long perdedor;

	private Long vencedor;

	private Long total;

	public Long getRecusado() {
		return recusado;
	}

	public void setRecusado(Long recusado) {
		this.recusado = recusado;
	}

	public Long getPendente() {
		return pendente;
	}

	public void setPendente(Long pendente) {
		this.pendente = pendente;
	}

	public Long getPerdedor() {
		return perdedor;
	}

	public void setPerdedor(Long perdedor) {
		this.perdedor = perdedor;
	}

	public Long getVencedor() {
		return vencedor;
	}

	public void setVencedor(Long vencedor) {
		this.vencedor = vencedor;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

}
