package br.com.kotar.domain.helper;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;

import br.com.kotar.core.helper.filter.PeriodoHelper;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;

public class CotacaoFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome;
	private PeriodoHelper periodoCadastro;
	private PeriodoHelper periodoEnvio;
	private PeriodoHelper periodoRetorno;
	private SituacaoCotacaoType situacaoCotacao;
	private Boolean negociosFechados = false;
	private Boolean avaliacoes = false;
	private Boolean somentePendenteDeAvaliacao = false;
	private Boolean pendenciasDeAvaliacao = false;
	
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

	public SituacaoCotacaoType getSituacaoCotacao() {
		return situacaoCotacao;
	}

	public void setSituacaoCotacao(SituacaoCotacaoType situacaoCotacao) {
		this.situacaoCotacao = situacaoCotacao;
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

	public Boolean getNegociosFechados() {
		return negociosFechados;
	}

	public void setNegociosFechados(Boolean negociosFechados) {
		this.negociosFechados = negociosFechados;
	}

	public Boolean getSomentePendenteDeAvaliacao() {
		return somentePendenteDeAvaliacao;
	}

	public void setSomentePendenteDeAvaliacao(Boolean somentePendenteDeAvaliacao) {
		this.somentePendenteDeAvaliacao = somentePendenteDeAvaliacao;
	}

	public Boolean getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Boolean avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public Boolean getPendenciasDeAvaliacao() {
		return pendenciasDeAvaliacao;
	}

	public void setPendenciasDeAvaliacao(Boolean pendenciasDeAvaliacao) {
		this.pendenciasDeAvaliacao = pendenciasDeAvaliacao;
	}
}