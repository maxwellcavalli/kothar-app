package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.kotar.core.domain.CrudDomain;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;
import br.com.kotar.domain.helper.SituacaoCotacaoHelper;
import br.com.kotar.domain.jackson.SituacaoCotacaoTypeDeserializer;

//@formatter:off
@Entity
@Table(name = "cotacao", 
	indexes = { 
			@Index(columnList = "DATA_CADASTRO", name = "idx_cotacao_data_cad"),
			@Index(columnList = "NOME", name = "idx_cotacao_nome") })
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "cotacao_id")) })
//@formatter:on
public class Cotacao extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cotacao_id")
	@JsonProperty("cotacao_id")
	private Long id;

	@Override
	@JsonProperty("cotacao_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_CADASTRO", nullable = false)
	private Date dataCadastro;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_LIMITE_RETORNO", nullable = false)
	private Date dataLimiteRetorno;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ENVIO", nullable = true)
	private Date dataEnvio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLIENTE_ID", referencedColumnName = "CLIENTE_ID", foreignKey = @ForeignKey(name = "FK_cotacao_cliente"), nullable = false)
	@JsonProperty("cotacao_cliente")
	private Cliente cliente;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "SITUACAO_COTACAO", nullable = false)
	@JsonDeserialize(using = SituacaoCotacaoTypeDeserializer.class)
	private SituacaoCotacaoType situacaoCotacao;

	@Column(name = "OBSERVACAO", nullable = true, length = 2000)
	private String observacao;

	@JsonProperty("cotacao_endereco")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_ENDERECO_ID", referencedColumnName = "COTACAO_ENDERECO_ID", foreignKey = @ForeignKey(name = "FK_cotacao_ende_cot"), nullable = true)
	private CotacaoEndereco cotacaoEndereco;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FINALIZADO", nullable = true)
	private Date dataFinalizado;
	
	@JsonProperty("cotacao_data_lembrete_avaliacao")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_LEMBRETE_AVALIACAO", nullable = true)
	private Date dataLembreteAvaliacao;

	@JsonProperty("cotacao_data_avaliacao")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_AVALIACAO", nullable = true)
	private Date dataAvaliacao;
	
	@Transient
	@JsonManagedReference("cotacao")
	@JsonProperty("cotacao_item")
	private List<CotacaoItem> itens;

	@Transient
	@JsonProperty("cotacao_fornecedor_item")
	private List<CotacaoItemFornecedor> forncedorItens;

	@Transient
	@JsonProperty("cotacao_quantidade_respostas")
	private Long quantidadeRespostas;

	@Transient
	@JsonProperty("cotacao_permite_resposta")
	private boolean permiteResposta;

	@Transient
	@JsonProperty("cotacao_fornecedor")
	private CotacaoFornecedor cotacaoFornecedor;

	@Transient
	@JsonProperty("cotacao_permite_cancelar")
	private boolean permiteCancelar;

	@Transient
	@JsonProperty("cotacao_permite_recusar")
	private boolean permiteRecusar;

	@JsonIgnore
	@Transient
	private Integer codeSituacaoResposta;

	@Transient
	@JsonProperty("cotacao_situacao_resposta_fornecedor")
	private String situacaoRespostaFornecedor;

	@Transient
	@JsonProperty("cotacao_situacao_str")
	private String situacaoStr;

	@Transient
	@JsonProperty("cotacao_situacao_helper")
	private SituacaoCotacaoHelper situacaoCotacaoHelper;

	@Transient
	private boolean podeFinalizar;
	
	@Transient
	@JsonProperty("cotacao_fornecedor_vencedor")
	private CotacaoFornecedor cotacaoFornecedorVencedor;
	
	@Transient
	@JsonProperty("avaliacao")
	private Avaliacao avaliacao;
	
	@Transient
	@JsonProperty("cotacao_permite_alteracoes")
	public boolean getPermiteAlteracoes() {
		return situacaoCotacao.equals(SituacaoCotacaoType.PENDENTE);
	}

	public Cotacao() {
	}

	public Cotacao(Long id, Long quantidadeRespostas) {
		super();
		this.id = id;
		this.quantidadeRespostas = quantidadeRespostas;
	} 

	public Cotacao(Long id, String nome, Date dataCadastro, Date dataLimiteRetorno, Date dataEnvio, SituacaoCotacaoType situacaoCotacao,
			Long quantidadeRespostas, CotacaoFornecedor cotacaoFornecedorVencedor) {
		super();
		this.id = id;
		this.setNome(nome);
		this.dataCadastro = dataCadastro;
		this.dataLimiteRetorno = dataLimiteRetorno;
		this.dataEnvio = dataEnvio;
		this.situacaoCotacao = situacaoCotacao;
		this.quantidadeRespostas = quantidadeRespostas;
		this.setCotacaoFornecedorVencedor(cotacaoFornecedorVencedor); 
	}
	
	public Cotacao(Long id, String nome, Date dataCadastro, Date dataLimiteRetorno, Date dataEnvio, SituacaoCotacaoType situacaoCotacao,
			Long quantidadeRespostas, CotacaoFornecedor cotacaoFornecedorVencedor, Avaliacao avaliacao, Fornecedor fornecedor) {
		super();
		this.id = id;
		this.setNome(nome);
		this.dataCadastro = dataCadastro;
		this.dataLimiteRetorno = dataLimiteRetorno;
		this.dataEnvio = dataEnvio;
		this.situacaoCotacao = situacaoCotacao;
		this.quantidadeRespostas = quantidadeRespostas;
		this.setCotacaoFornecedorVencedor(cotacaoFornecedorVencedor);		 
		
		if (cotacaoFornecedorVencedor != null) {
			
			this.cotacaoFornecedorVencedor.setNomeFornecedor(fornecedor.getNome());
			
			if (avaliacao != null) {
				this.avaliacao = avaliacao;				
			}
		}
	}
	
	public Cotacao(Long id, String nome, Date dataCadastro, Date dataLimiteRetorno, Date dataEnvio, SituacaoCotacaoType situacaoCotacao,
			Integer codeSituacaoResposta) {
		super();
		this.id = id;
		setNome(nome);
		this.dataCadastro = dataCadastro;
		this.dataLimiteRetorno = dataLimiteRetorno;
		this.dataEnvio = dataEnvio;
		this.situacaoCotacao = situacaoCotacao;
		this.codeSituacaoResposta = codeSituacaoResposta;
	}

	public Cotacao(Long id, String nome, Date dataCadastro, Date dataLimiteRetorno, Date dataEnvio, SituacaoCotacaoType situacaoCotacao,
			Long quantidadeRespostas) {
		super();
		this.id = id;
		this.setNome(nome);
		this.dataCadastro = dataCadastro;
		this.dataLimiteRetorno = dataLimiteRetorno;
		this.dataEnvio = dataEnvio;
		this.situacaoCotacao = situacaoCotacao;
		this.quantidadeRespostas = quantidadeRespostas;
	}
	
	@Transient
	public boolean isSituacaoPendenteCanceladaFinalizada(){
		return getSituacaoCotacao().equals(SituacaoCotacaoType.FINALIZADA) ||
                getSituacaoCotacao().equals(SituacaoCotacaoType.PENDENTE) ||
				getSituacaoCotacao().equals(SituacaoCotacaoType.CANCELADA);
	}

	@Transient
	public boolean isSituacaoFinalizada(){
		return getSituacaoCotacao().equals(SituacaoCotacaoType.FINALIZADA);
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataLimiteRetorno() {
		return dataLimiteRetorno;
	}

	public void setDataLimiteRetorno(Date dataLimiteRetorno) {
		this.dataLimiteRetorno = dataLimiteRetorno;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public SituacaoCotacaoType getSituacaoCotacao() {
		return situacaoCotacao;
	}

	public void setSituacaoCotacao(SituacaoCotacaoType situacaoCotacao) {
		this.situacaoCotacao = situacaoCotacao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<CotacaoItem> getItens() {
		return itens;
	}

	public void setItens(List<CotacaoItem> itens) {
		this.itens = itens;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<CotacaoItemFornecedor> getForncedorItens() {
		return forncedorItens;
	}

	public void setForncedorItens(List<CotacaoItemFornecedor> forncedorItens) {
		this.forncedorItens = forncedorItens;
	}

	public boolean isPermiteResposta() {
		return permiteResposta;
	}

	public void setPermiteResposta(boolean permiteResposta) {
		this.permiteResposta = permiteResposta;
	}

	public CotacaoFornecedor getCotacaoFornecedor() {
		return cotacaoFornecedor;
	}

	public void setCotacaoFornecedor(CotacaoFornecedor cotacaoFornecedor) {
		this.cotacaoFornecedor = cotacaoFornecedor;
	}

	public boolean isPermiteCancelar() {
		return permiteCancelar;
	}

	public void setPermiteCancelar(boolean permiteCancelar) {
		this.permiteCancelar = permiteCancelar;
	}

	public boolean isPermiteRecusar() {
		return permiteRecusar;
	}

	public void setPermiteRecusar(boolean permiteRecusar) {
		this.permiteRecusar = permiteRecusar;
	}

	public Integer getCodeSituacaoResposta() {
		return codeSituacaoResposta;
	}

	public void setCodeSituacaoResposta(Integer codeSituacaoResposta) {
		this.codeSituacaoResposta = codeSituacaoResposta;
	}

	public String getSituacaoRespostaFornecedor() {
		return situacaoRespostaFornecedor;
	}

	public void setSituacaoRespostaFornecedor(String situacaoRespostaFornecedor) {
		this.situacaoRespostaFornecedor = situacaoRespostaFornecedor;
	}

	public String getSituacaoStr() {
		return situacaoStr;
	}

	public void setSituacaoStr(String situacaoStr) {
		this.situacaoStr = situacaoStr;
	}

	public SituacaoCotacaoHelper getSituacaoCotacaoHelper() {
		return situacaoCotacaoHelper;
	}

	public void setSituacaoCotacaoHelper(SituacaoCotacaoHelper situacaoCotacaoHelper) {
		this.situacaoCotacaoHelper = situacaoCotacaoHelper;
	}

	public CotacaoEndereco getCotacaoEndereco() {
		return cotacaoEndereco;
	}

	public void setCotacaoEndereco(CotacaoEndereco cotacaoEndereco) {
		this.cotacaoEndereco = cotacaoEndereco;
	}

	public boolean isPodeFinalizar() {
		return podeFinalizar;
	}

	public void setPodeFinalizar(boolean podeFinalizar) {
		this.podeFinalizar = podeFinalizar;
	}

	public Long getQuantidadeRespostas() {
		return quantidadeRespostas;
	}

	public void setQuantidadeRespostas(Long quantidadeRespostas) {
		this.quantidadeRespostas = quantidadeRespostas;
	}

	public CotacaoFornecedor getCotacaoFornecedorVencedor() {
		return cotacaoFornecedorVencedor;
	}

	public void setCotacaoFornecedorVencedor(CotacaoFornecedor cotacaoFornecedorVencedor) {
		this.cotacaoFornecedorVencedor = cotacaoFornecedorVencedor;
	}

	public Date getDataFinalizado() {
		return dataFinalizado;
	}

	public void setDataFinalizado(Date dataFinalizado) {
		this.dataFinalizado = dataFinalizado;
	}

	public Date getDataLembreteAvaliacao() {
		return dataLembreteAvaliacao;
	}

	public void setDataLembreteAvaliacao(Date dataLembreteAvaliacao) {
		this.dataLembreteAvaliacao = dataLembreteAvaliacao;
	}

	public Date getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}
	

	public Avaliacao getAvaliacao() {
		return avaliacao;
	}

	public void setAvaliacao(Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}
}