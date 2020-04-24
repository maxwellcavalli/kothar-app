package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "COTACAO_ITEM_ARQUIVO")
public class CotacaoItemArquivo extends BaseDomain implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COTACAO_ITEM_ARQUIVO_ID", nullable = false)
	@JsonProperty("cotacao_item_arquivo_id")
	private Long id;
	
	
	@Column(name = "NOME", nullable = false)
	@JsonProperty("cotacao_item_arquivo_nome")
	private String nome;	

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_ITEM_ID", referencedColumnName = "COTACAO_ITEM_ID", foreignKey = @ForeignKey(name = "FK_cotitemarq_cotitem"), nullable=false)
	private CotacaoItem cotacaoItem;
	
	@JsonProperty("data_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ATUALIZACAO", nullable = true)
	private Date dataAtualizacao;

	@JsonIgnore
	@Lob
	@Column(name = "ARQUIVO")
	private byte[] arquivo;
	
	@JsonIgnore
	@Lob
	@Column(name = "THUMB")
	private byte[] thumb;
	
	
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
	
	@JsonProperty("thumb64")
	public void setThumb64(String thumb) {
		if (thumb != null && !thumb.trim().isEmpty()) {
			this.thumb = Base64.getDecoder().decode(thumb);
		}
	}

	@JsonProperty("thumb64")
	public String getThumb64() {
		if (this.thumb != null) {
			return Base64.getEncoder().encodeToString(this.thumb);
		}

		return "";
	}
	
	public CotacaoItemArquivo() {
	}
	

	public CotacaoItemArquivo(Long id, String nome, CotacaoItem cotacaoItem) {
		super();
		this.id = id;
		this.nome = nome;
		this.cotacaoItem = cotacaoItem;
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

	public CotacaoItem getCotacaoItem() {
		return cotacaoItem;
	}

	public void setCotacaoItem(CotacaoItem cotacaoItem) {
		this.cotacaoItem = cotacaoItem;
	}

	public byte[] getArquivo() {
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	@Override
	public CotacaoItemArquivo clone() throws CloneNotSupportedException {
		return (CotacaoItemArquivo) super.clone();
	}

	public byte[] getThumb() {
		return thumb;
	}

	public void setThumb(byte[] thumb) {
		this.thumb = thumb;
	}

}