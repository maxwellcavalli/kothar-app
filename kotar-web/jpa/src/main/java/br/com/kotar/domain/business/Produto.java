package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.CrudDomain;
import br.com.kotar.domain.business.type.SituacaoProdutoType;

@Entity
@Table(name = "produto", uniqueConstraints = { @UniqueConstraint(columnNames = { "nome" }, name = "uidx_produto_nome") })
public class Produto extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "produto_id")
	@JsonProperty("produto_id")
	private Long id;

	@Override
	@JsonProperty("produto_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@Column(name = "GENERICO", nullable = false)
	private boolean generico;

	@Column(name = "DETALHAMENTO", nullable = true, length = 2000)
	private String detalhamento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "grupo_produto_id", referencedColumnName = "grupo_produto_id", foreignKey = @ForeignKey(name = "FK_prod_grupo_prod"), nullable = false)
	@JsonProperty("produto_grupo_produto")
	private GrupoProduto grupoProduto;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "SITUACAO_PRODUTO", nullable = false)
	private SituacaoProdutoType situacaoProduto = SituacaoProdutoType.ANALISE;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "produto")
	private Set<LogSituacaoProduto> logSituacao;
	
	@Transient
	@JsonProperty("produto_imagens")
	private List<ProdutoImagem> imagens;
	
	@Transient
	@JsonProperty("produto_imagem_principal")
	private ProdutoImagem produtoImagem;

	
	@JsonProperty("produto_situacao")
	public String getSituacao() {
		if (situacaoProduto != null) {
			return situacaoProduto.getDescription();
		} else {
			return "";
		}
	}
	
	public Produto() {
	}
	
	public Produto(Long id, String nome) {
		super();
		this.id = id;
		this.setNome(nome);
	}

	public Produto(Long produto_id, String produto_nome, boolean produto_generico, SituacaoProdutoType produto_situacao, 
		GrupoProduto grupoProduto, Long produto_imagem_id) {
		
		this.id = produto_id;
		this.setNome(produto_nome);
		this.generico = produto_generico;
		this.situacaoProduto = produto_situacao;
		this.grupoProduto = grupoProduto;
		
		if (produto_imagem_id != null){
			ProdutoImagem _pi = new ProdutoImagem();
			_pi.setId(produto_imagem_id);
			this.produtoImagem = _pi;
		}
	}

	public boolean isGenerico() {
		return generico;
	}

	public void setGenerico(boolean generico) {
		this.generico = generico;
	}

	public GrupoProduto getGrupoProduto() {
		return grupoProduto;
	}

	public void setGrupoProduto(GrupoProduto grupoProduto) {
		this.grupoProduto = grupoProduto;
	}

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public SituacaoProdutoType getSituacaoProduto() {
		return situacaoProduto;
	}

	public void setSituacaoProduto(SituacaoProdutoType situacaoProduto) {
		this.situacaoProduto = situacaoProduto;
	}

	public Set<LogSituacaoProduto> getLogSituacao() {
		return logSituacao;
	}

	public void setLogSituacao(Set<LogSituacaoProduto> logSituacao) {
		this.logSituacao = logSituacao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ProdutoImagem> getImagens() {
		return imagens;
	}

	public void setImagens(List<ProdutoImagem> imagens) {
		this.imagens = imagens;
	}

	public ProdutoImagem getProdutoImagem() {
		return produtoImagem;
	}

	public void setProdutoImagem(ProdutoImagem produtoImagem) {
		this.produtoImagem = produtoImagem;
	}


	/*
	 * public Set<Categoria> getCategorias() { return categorias; }
	 * 
	 * public void setCategorias(Set<Categoria> categorias) { this.categorias =
	 * categorias; }
	 */

}