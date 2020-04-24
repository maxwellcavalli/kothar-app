package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.CrudDomain;

@Entity
@Table(name = "grupo_produto", uniqueConstraints = { @UniqueConstraint(columnNames = { "nome" }, name = "uidx_grupo_produto_nome") })
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "grupo_produto_id")) })
public class GrupoProduto extends CrudDomain implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "grupo_produto_id")
	@JsonProperty("grupo_produto_id")
	private Long id;

	@Override
	@JsonProperty("grupo_produto_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_produto_parent_id", referencedColumnName = "grupo_produto_id", foreignKey = @ForeignKey(name = "Fk_grupo_produto_parent"))
	@JsonProperty("grupo_produto_parent")
	private GrupoProduto grupoProdutoParent;

	@Transient
	@JsonProperty("grupo_produto_children")
	private List<GrupoProduto> children;

	@Transient
	private boolean selecionado;

	@Override
	public String toString() {
		return "GrupoProduto [grupoProdutoParent=" + grupoProdutoParent + ", getId()=" + getId() + ", getNome()=" + getNome() + "]";
	}

	public GrupoProduto getGrupoProdutoParent() {
		return grupoProdutoParent;
	}

	public void setGrupoProdutoParent(GrupoProduto grupoProdutoParent) {
		this.grupoProdutoParent = grupoProdutoParent;
	}

	public List<GrupoProduto> getChildren() {
		return children;
	}

	public void setChildren(List<GrupoProduto> children) {
		this.children = children;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public GrupoProduto clone() throws CloneNotSupportedException {
		return (GrupoProduto) super.clone();
	}
}