package br.com.kotar.core.helper.filter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author maxwell.cavalli
 *
 */
public class PeriodoHelper implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date inicio;
	private Date fim;

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

}
