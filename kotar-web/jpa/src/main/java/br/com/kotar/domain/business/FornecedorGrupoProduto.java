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

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "fornecedor_grupo_produto")
public class FornecedorGrupoProduto extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fornecedor_grupo_produto_id")
	@JsonProperty("fornecedor_grupo_produto_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FORNECEDOR_ID", referencedColumnName = "FORNECEDOR_ID", foreignKey = @ForeignKey(name = "FK_forngrpprod_forn"))
	private Fornecedor fornecedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GRUPO_PRODUTO_ID", referencedColumnName = "GRUPO_PRODUTO_ID", foreignKey = @ForeignKey(name = "FK_forngrpprod_grp_prod"))
	private GrupoProduto grupoProduto;

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

	public GrupoProduto getGrupoProduto() {
		return grupoProduto;
	}

	public void setGrupoProduto(GrupoProduto grupoProduto) {
		this.grupoProduto = grupoProduto;
	}
}