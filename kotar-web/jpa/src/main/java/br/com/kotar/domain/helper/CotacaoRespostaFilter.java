package br.com.kotar.domain.helper;

import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.type.SituacaoRespostaFornecedorType;

public class CotacaoRespostaFilter extends CotacaoFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Fornecedor fornecedor;
	private SituacaoRespostaFornecedorType situacaoRespostaFornecedor; 

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public SituacaoRespostaFornecedorType getSituacaoRespostaFornecedor() {
		return situacaoRespostaFornecedor;
	}

	public void setSituacaoRespostaFornecedor(SituacaoRespostaFornecedorType situacaoRespostaFornecedor) {
		this.situacaoRespostaFornecedor = situacaoRespostaFornecedor;
	}
	

}
