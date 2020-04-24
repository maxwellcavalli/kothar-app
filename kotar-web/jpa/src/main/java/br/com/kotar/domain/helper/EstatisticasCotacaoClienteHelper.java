package br.com.kotar.domain.helper;

import java.io.Serializable;

public class EstatisticasCotacaoClienteHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long pendente;

	private Long enviadoSemResposta;

	private Long enviadoComResposta;
	
	private Long cancelado;

	private Long total;

	public Long getPendente() {
		return pendente;
	}

	public void setPendente(Long pendente) {
		this.pendente = pendente;
	}

	public Long getEnviadoSemResposta() {
		return enviadoSemResposta;
	}

	public void setEnviadoSemResposta(Long enviadoSemResposta) {
		this.enviadoSemResposta = enviadoSemResposta;
	}

	public Long getEnviadoComResposta() {
		return enviadoComResposta;
	}

	public void setEnviadoComResposta(Long enviadoComResposta) {
		this.enviadoComResposta = enviadoComResposta;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getCancelado() {
		return cancelado;
	}

	public void setCancelado(Long cancelado) {
		this.cancelado = cancelado;
	}

}
