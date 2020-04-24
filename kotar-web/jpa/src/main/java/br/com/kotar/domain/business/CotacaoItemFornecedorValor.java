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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "COTACAO_ITEM_FORNECEDOR_VALOR")
public class CotacaoItemFornecedorValor extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COTACAO_ITEM_FORNECEDOR_VALOR_ID", nullable = false)
	@JsonProperty("cotacao_item_fornecedor_valor_id")
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_ITEM_FORNECEDOR_ID", referencedColumnName = "COTACAO_ITEM_FORNECEDOR_ID", foreignKey = @ForeignKey(name = "FK_cotitemforval_cotitemforn"), nullable = false)
	private CotacaoItemFornecedor cotacaoItemFornecedor;

	@JsonProperty("valor_unitario")
	@Column(name = "UNITARIO", nullable = true, scale = 14, precision = 3, columnDefinition = "NUMERIC(14,3)")
	private BigDecimal unitario;

	@JsonProperty("marca_modelo")
	@Column(name = "MARCA_MODELO", nullable = true, length = 255)
	private String marcaModelo;

	@JsonProperty("data_atualizacao")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ATUALIZACAO", nullable = true)
	private Date dataAtualizacao;

	@JsonProperty("selecionado")
	@Column(name = "SELECIONADO", nullable = true)
	private boolean selecionado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CotacaoItemFornecedor getCotacaoItemFornecedor() {
		return cotacaoItemFornecedor;
	}

	public void setCotacaoItemFornecedor(CotacaoItemFornecedor cotacaoItemFornecedor) {
		this.cotacaoItemFornecedor = cotacaoItemFornecedor;
	}

	public BigDecimal getUnitario() {
		return unitario;
	}

	public void setUnitario(BigDecimal unitario) {
		this.unitario = unitario;
	}

	public String getMarcaModelo() {
		return marcaModelo;
	}

	public void setMarcaModelo(String marcaModelo) {
		this.marcaModelo = marcaModelo;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

}