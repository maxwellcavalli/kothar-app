package br.com.kotar.domain.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.kotar.core.domain.BaseDomain;
import br.com.kotar.domain.business.type.TipoJurosPagamentoType;
import br.com.kotar.domain.business.type.TipoPagamentoType;
import br.com.kotar.domain.jackson.TipoJurosPagamentoTypeDeserializer;
import br.com.kotar.domain.jackson.TipoPagamentoTypeDeserializer;

//@formatter:off
@Entity
@Table(name = "COTACAO_FORNECEDOR")
//@formatter:on
public class CotacaoFornecedor extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COTACAO_FORNECEDOR_ID", nullable = false)
	@JsonProperty("cotacao_fornecedor_id")
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_ID", referencedColumnName = "COTACAO_ID", foreignKey = @ForeignKey(name = "FK_cotforn_cot"), nullable = false)
	@JsonProperty("cotacao_fornecedor_cotacao")
	private Cotacao cotacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FORNECEDOR_ID", referencedColumnName = "FORNECEDOR_ID", foreignKey = @ForeignKey(name = "FK_cotforn_forn"), nullable = false)
	@JsonProperty("fornecedor")
	private Fornecedor fornecedor;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_RECUSA", nullable = true)
	@JsonProperty("data_recusa")
	private Date dataRecusa;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_RESPOSTA", nullable = true)
	@JsonProperty("data_resposta")
	private Date dataResposta;

	@JsonProperty("enviado")
	@Column(name = "ENVIADO", nullable = false)
	private boolean enviado;

	@Column(name = "ENTREGA", nullable = true, length = 2000)
	private String entrega;

	@JsonIgnore
	@Column(name = "VENCEDOR_BY_USER")
	private Boolean vencedorByUser = false;	
	
	@Column(name = "CODIGO_APROVACAO_VENCEDOR", nullable = true, length = 10, unique = true)
	@JsonProperty("codigo_aprovacao_vencedor")
	private String codigoAprovacaoVencedor; 

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_VALIDADE", nullable = true)
	@JsonProperty("data_validade")
	private Date dataValidade;	
	
	@JsonProperty("tipo_pagamento")
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "TIPO_PAGAMENTO", nullable = true)
	@JsonDeserialize(using = TipoPagamentoTypeDeserializer.class)
	private TipoPagamentoType tipoPagamento; 
	
	@JsonProperty("numero_parcelas")
	@Column(name = "NUMERO_PARCELAS", nullable = true)
	private Integer numeroParcelas;	
	
	@JsonProperty("juros_parcelamento")
	@Column(name = "JUROS_PARCELAMENTO", nullable = true)
	private BigDecimal jurosPacelamento;

	@JsonProperty("tipo_juros_pagamento")
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "TIPO_JUROS", nullable = true)
	@JsonDeserialize(using = TipoJurosPagamentoTypeDeserializer.class)
	private TipoJurosPagamentoType tipoJurosPagamento; 
	
	@Column(name = "DISTANCIA_METROS", nullable = true, length = 10)
	@JsonProperty("distancia_metros")
	private Integer distancia;
	
	@JsonProperty("push_enviado")
	@Column(name = "PUSH_ENVIADO", nullable = false)
	private boolean pushEnviado;
	
	@Transient
	@JsonProperty("itens_fornecedor")
	private List<CotacaoItemFornecedor> itensFornecedor;

	@JsonIgnore
	@Transient
	//@JsonProperty("fornecedor_valor_global")
	private BigDecimal valorGlobal;
	
	@JsonIgnore
	@Transient
	//@JsonProperty("fornecedor_colocacao_global")
	private Integer colocacaoGlobal;

	@JsonIgnore
	@Transient
	private boolean fornecedorComPendencias;

	@JsonProperty("valor_total_aprovado")
	@Column(name = "VALOR_TOTAL_APROVADO", nullable = true)
	private BigDecimal valorTotalAprovado;
	
	@JsonIgnore
	@Transient
	private BigDecimal variacaoMin = BigDecimal.ZERO;
	
	@JsonIgnore
	@Transient
	private BigDecimal variacaoMax = BigDecimal.ZERO;
	
	@JsonProperty("condicoes_pagamento_tipo")
	@Transient
	private String tipoPagamentoStr;
	
	@JsonIgnore
	@Transient
	private String tipoJurosPagamentoStr;
	
	@JsonProperty("condicoes_pagamento_texto_prazo")
	@Transient
	private String textoPrazo;
	
	@Transient
	@JsonProperty("nome_fornecedor_str")
	private String nomeFornecedor; 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cotacao getCotacao() {
		return cotacao;
	}

	public void setCotacao(Cotacao cotacao) {
		this.cotacao = cotacao;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Date getDataRecusa() {
		return dataRecusa;
	}

	public void setDataRecusa(Date dataRecusa) {
		this.dataRecusa = dataRecusa;
	}

	public boolean isEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	public String getEntrega() {
		return entrega;
	}

	public void setEntrega(String entrega) {
		this.entrega = entrega;
	}
	
	public List<CotacaoItemFornecedor> getItensFornecedor() {
		return itensFornecedor;
	}

	public void setItensFornecedor(List<CotacaoItemFornecedor> itensFornecedor) {
		this.itensFornecedor = itensFornecedor;
	}

	public BigDecimal getValorGlobal() {
		return valorGlobal;
	}

	public void setValorGlobal(BigDecimal valorGlobal) {
		this.valorGlobal = valorGlobal;
	}

	public Integer getColocacaoGlobal() {
		return colocacaoGlobal;
	}

	public void setColocacaoGlobal(Integer colocacaoGlobal) {
		this.colocacaoGlobal = colocacaoGlobal;
	}

	public Date getDataResposta() {
		return dataResposta;
	}

	public void setDataResposta(Date dataResposta) {
		this.dataResposta = dataResposta;
	}

	public boolean isFornecedorComPendencias() {
		return fornecedorComPendencias;
	}

	public void setFornecedorComPendencias(boolean fornecedorComPendencias) {
		this.fornecedorComPendencias = fornecedorComPendencias;
	}

	public Boolean getVencedorByUser() {
		return vencedorByUser;
	}

	public void setVencedorByUser(Boolean vencedorByUser) {
		this.vencedorByUser = vencedorByUser;
	}

	public BigDecimal getVariacaoMin() {
		return variacaoMin;
	}

	public void setVariacaoMin(BigDecimal variacaoMin) {
		this.variacaoMin = variacaoMin;
	}

	public BigDecimal getVariacaoMax() {
		return variacaoMax;
	}

	public void setVariacaoMax(BigDecimal variacaoMax) {
		this.variacaoMax = variacaoMax;
	}

	public String getCodigoAprovacaoVencedor() {
		return codigoAprovacaoVencedor;
	}

	public void setCodigoAprovacaoVencedor(String codigoAprovacaoVencedor) {
		this.codigoAprovacaoVencedor = codigoAprovacaoVencedor;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public TipoPagamentoType getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(TipoPagamentoType tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public Integer getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(Integer numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}

	public BigDecimal getJurosPacelamento() {
		return jurosPacelamento;
	}

	public void setJurosPacelamento(BigDecimal jurosPacelamento) {
		this.jurosPacelamento = jurosPacelamento;
	}

	public TipoJurosPagamentoType getTipoJurosPagamento() {
		return tipoJurosPagamento;
	}

	public void setTipoJurosPagamento(TipoJurosPagamentoType tipoJurosPagamento) {
		this.tipoJurosPagamento = tipoJurosPagamento;
	}

	public String getTipoPagamentoStr() {
		return tipoPagamentoStr;
	}

	public void setTipoPagamentoStr(String tipoPagamentoStr) {
		this.tipoPagamentoStr = tipoPagamentoStr;
	}

	public String getTipoJurosPagamentoStr() {
		return tipoJurosPagamentoStr;
	}

	public void setTipoJurosPagamentoStr(String tipoJurosPagamentoStr) {
		this.tipoJurosPagamentoStr = tipoJurosPagamentoStr;
	}

	public Integer getDistancia() {
		return distancia;
	}

	public void setDistancia(Integer distancia) {
		this.distancia = distancia;
	} 
	
	@Transient 
	@JsonProperty("distancia_formatada")
	public String getDistanciaFmt() {
		String retorno = "";
		if (distancia != null) {
			if (distancia < 1000) {
				retorno = distancia + " m";
			}
			else {
				Double calculo = new Double (distancia / 1000.0);
			    Double arredondado =new BigDecimal(calculo).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
				retorno = arredondado + " km";
			}
		}
		return retorno;
	}

	public BigDecimal getValorTotalAprovado() {
		return valorTotalAprovado;
	}

	public void setValorTotalAprovado(BigDecimal valorTotalAprovado) {
		this.valorTotalAprovado = valorTotalAprovado;
	}

	public String getTextoPrazo() {
		return textoPrazo;
	}

	public void setTextoPrazo(String textoPrazo) {
		this.textoPrazo = textoPrazo;
	}

	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}

	public boolean isPushEnviado() {
		return pushEnviado;
	}

	public void setPushEnviado(boolean pushEnviado) {
		this.pushEnviado = pushEnviado;
	}


}