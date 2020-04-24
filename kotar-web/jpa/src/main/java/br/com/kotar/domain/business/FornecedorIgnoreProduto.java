package br.com.kotar.domain.business;

import java.io.Serializable;

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
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

//@formatter:off
@Entity
@Table(name = "fornecedor_ignore_produto", 
	uniqueConstraints = { 
			@UniqueConstraint(columnNames = { "FORNECEDOR_ID", "PRODUTO_ID" }, name = "uidx_forn_ignore_prod") }) 
	
//@formatter:on
public class FornecedorIgnoreProduto extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fornecedor_ignore_produto_id")
	@JsonProperty("fornecedor_ignore_produto_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FORNECEDOR_ID", referencedColumnName = "FORNECEDOR_ID", foreignKey = @ForeignKey(name = "FK_forig_forn"), nullable = false)
	private Fornecedor fornecedor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUTO_ID", referencedColumnName = "PRODUTO_ID", foreignKey = @ForeignKey(name = "FK_forig_prod"), nullable = false)
	private Produto produto;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

}