package br.com.kotar.domain.helper;

import java.io.Serializable;

import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.Produto;

public class PendenciaIntegracaoHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private Produto produto;
	private Fornecedor fornecedor;
	private Double precoUnitario;
	private String identificacaoProduto;

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Double getPrecoUnitario() {
		return precoUnitario;
	}

	public void setPrecoUnitario(Double precoUnitario) {
		this.precoUnitario = precoUnitario;
	}

	public String getIdentificacaoProduto() {
		return identificacaoProduto;
	}

	public void setIdentificacaoProduto(String identificacaoProduto) {
		this.identificacaoProduto = identificacaoProduto;
	}
}
