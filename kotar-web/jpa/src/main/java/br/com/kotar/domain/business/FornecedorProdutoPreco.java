package br.com.kotar.domain.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

//@formatter:off
@Entity
@Table(name = "fornecedor_produto_preco")
//@formatter:on
public class FornecedorProdutoPreco extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fornecedor_produto_preco_id")
	@JsonProperty("fornecedor_produto_preco_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FORNECEDOR_PRODUTO_ID", referencedColumnName = "FORNECEDOR_PRODUTO_ID", foreignKey = @ForeignKey(name = "FK_fornprodprec_fornprod"), nullable = false)
	private FornecedorProduto fornecedorProduto;

	@Column(name = "UNITARIO", nullable = true, scale = 14, precision = 3, columnDefinition = "NUMERIC(14,3)")
	private BigDecimal unitario;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	private Date dataAtualizacao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public FornecedorProduto getFornecedorProduto() {
		return fornecedorProduto;
	}

	public void setFornecedorProduto(FornecedorProduto fornecedorProduto) {
		this.fornecedorProduto = fornecedorProduto;
	}

	public BigDecimal getUnitario() {
		return unitario;
	}

	public void setUnitario(BigDecimal unitario) {
		this.unitario = unitario;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	
	
}