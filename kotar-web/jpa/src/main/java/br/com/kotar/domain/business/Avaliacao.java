package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.kotar.core.domain.BaseDomain;
import br.com.kotar.domain.business.type.MotivoDesistenciaCompraType;
import br.com.kotar.domain.helper.MotivoDesistenciaCompraHelper;
import br.com.kotar.domain.jackson.MotivoDesistenciaCompraTypeDeserializer;

@Entity
@Table(name = "avaliacao",     
	   uniqueConstraints = @UniqueConstraint(columnNames={"COTACAO_FORNECEDOR_ID"}, name="uidx_cotacao_fornecedor_avaliacao"))
public class Avaliacao extends BaseDomain implements Serializable {
 
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "avaliacao_id")
	@JsonProperty("avaliacao_id")
	private Long id;

	@Column(name = "DETALHAMENTO", nullable = true, length = 2000)
	private String detalhamento;

	@Column(name = "numero_estrelas")
	@JsonProperty("numero_estrelas")
	private Integer numeroEstrelas;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_FORNECEDOR_ID", referencedColumnName = "COTACAO_FORNECEDOR_ID", foreignKey = @ForeignKey(name = "FK_avaliacao_cotacao_fornecedor"), nullable = false)
	private CotacaoFornecedor cotacaoFornecedor;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "MOTIVO_DESISTENCIA", nullable = true)
	@JsonDeserialize(using = MotivoDesistenciaCompraTypeDeserializer.class)
	private MotivoDesistenciaCompraType motivoDesistenciaCompraType;

	@Transient
	@JsonProperty("motivo_desistencia_compra_str")
	private String motivoDesistenciaCompraStr;
	
	@Transient
	@JsonProperty("motivo_desistencia_compra_helper")
	private MotivoDesistenciaCompraHelper motivoDesistenciaCompraHelper;
	
	@Transient
	@JsonProperty("nome_usuario")
	private String nomeUsuario;
	
	@Transient
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
	@JsonProperty("data_avaliacao")
	private Date dataAvaliacao;
	
	public Avaliacao() {
	}
	
	public Avaliacao(Long id) {
		super();
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public Integer getNumeroEstrelas() {
		return numeroEstrelas;
	}

	public void setNumeroEstrelas(Integer numeroEstrelas) {
		this.numeroEstrelas = numeroEstrelas;
	}

	public CotacaoFornecedor getCotacaoFornecedor() {
		return cotacaoFornecedor;
	}

	public void setCotacaoFornecedor(CotacaoFornecedor cotacaoFornecedor) {
		this.cotacaoFornecedor = cotacaoFornecedor;
	}

	public MotivoDesistenciaCompraType getMotivoDesistenciaCompraType() {
		return motivoDesistenciaCompraType;
	}

	public void setMotivoDesistenciaCompraType(MotivoDesistenciaCompraType motivoDesistenciaCompraType) {
		this.motivoDesistenciaCompraType = motivoDesistenciaCompraType;
	}

	public String getMotivoDesistenciaCompraStr() {
		return motivoDesistenciaCompraStr;
	}

	public void setMotivoDesistenciaCompraStr(String motivoDesistenciaCompraStr) {
		this.motivoDesistenciaCompraStr = motivoDesistenciaCompraStr;
	}

	public MotivoDesistenciaCompraHelper getMotivoDesistenciaCompraHelper() {
		return motivoDesistenciaCompraHelper;
	}

	public void setMotivoDesistenciaCompraHelper(MotivoDesistenciaCompraHelper motivoDesistenciaCompraHelper) {
		this.motivoDesistenciaCompraHelper = motivoDesistenciaCompraHelper;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public Date getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}


}