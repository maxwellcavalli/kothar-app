package br.com.kotar.domain.helper;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;

import br.com.kotar.core.helper.filter.PeriodoHelper;

public class ConsultaRespostaFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome;
	private PeriodoHelper periodoCadastro;
	private PeriodoHelper periodoEnvio;
	private PeriodoHelper periodoRetorno;
	
	private Pageable pageable;
			
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public PeriodoHelper getPeriodoCadastro() {
		return periodoCadastro;
	}

	public void setPeriodoCadastro(PeriodoHelper periodoCadastro) {
		this.periodoCadastro = periodoCadastro;
	}

	public PeriodoHelper getPeriodoEnvio() {
		return periodoEnvio;
	}

	public void setPeriodoEnvio(PeriodoHelper periodoEnvio) {
		this.periodoEnvio = periodoEnvio;
	}

	public PeriodoHelper getPeriodoRetorno() {
		return periodoRetorno;
	}

	public void setPeriodoRetorno(PeriodoHelper periodoRetorno) {
		this.periodoRetorno = periodoRetorno;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

}
