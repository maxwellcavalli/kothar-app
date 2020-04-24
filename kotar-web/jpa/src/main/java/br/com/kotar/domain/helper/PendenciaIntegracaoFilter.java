package br.com.kotar.domain.helper;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;

import br.com.kotar.core.helper.filter.PeriodoHelper;

public class PendenciaIntegracaoFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private PeriodoHelper periodoCadastro;
	private Pageable pageable;

	public PeriodoHelper getPeriodoCadastro() {
		return periodoCadastro;
	}

	public void setPeriodoCadastro(PeriodoHelper periodoCadastro) {
		this.periodoCadastro = periodoCadastro;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}
}
