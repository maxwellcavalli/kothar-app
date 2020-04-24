package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "PRODUTO_IMAGEM")
public class ProdutoImagem extends BaseDomain implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PRODUTO_IMAGEM_ID", nullable = false)
	@JsonProperty("produto_imagem_id")
	private Long id;
	
	
	@Column(name = "NOME", nullable = false)
	@JsonProperty("produto_imagem_nome")
	private String nome;	

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUTO_ID", referencedColumnName = "PRODUTO_ID", foreignKey = @ForeignKey(name = "FK_prodimg_prod"), nullable=false)
	private Produto produto;
	
	@JsonIgnore
	@Lob
	@Column(name = "ARQUIVO")
	private byte[] arquivo;
	
	@Column(name = "PRINCIPAL", nullable = false)
	@JsonProperty("produto_imagem_principal")
	private boolean principal;	

	@JsonIgnore
	@Lob
	@Column(name = "THUMB")
	private byte[] thumb;
	
	@Column(name = "ATIVO")
	private boolean ativo;
	
	@JsonProperty("imagem64")
	public void setImagem64(String imagem) {
		if (imagem != null && !imagem.trim().isEmpty()) {
			this.arquivo = Base64.getDecoder().decode(imagem);
		}
	}

	@JsonProperty("imagem64")
	public String getImagem64() {
		if (this.arquivo != null) {
			return Base64.getEncoder().encodeToString(this.arquivo);
		}

		return "";
	}
	
	public ProdutoImagem(Long id, String nome) {
		super();
		this.id = id;
		this.nome = nome;
	}
	
	public ProdutoImagem(Long id, String nome, Boolean principal) {
		super();
		this.id = id;
		this.nome = nome;
		this.principal = principal;
	}
	
	public ProdutoImagem(Long id, String nome, byte[] thumb, Produto produto, Boolean principal) {
		super();
		this.id = id;
		this.nome = nome;
		this.thumb = thumb;
		this.produto = produto;
		this.principal = principal;
	}	

	public ProdutoImagem() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public byte[] getArquivo() {
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}
	
	
	@JsonProperty("thumb64")
	public void setThumb64(String imagem) {
		if (imagem != null && !imagem.trim().isEmpty()) {
			this.thumb = Base64.getDecoder().decode(imagem);
		}
	}

	@JsonProperty("thumb64")
	public String getThumb64() {
		if (this.thumb != null) {
			return Base64.getEncoder().encodeToString(this.thumb);
		}

		return "";
	}
	

	public byte[] getThumb() {
		return thumb;
	}

	public void setThumb(byte[] thumb) {
		this.thumb = thumb;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}