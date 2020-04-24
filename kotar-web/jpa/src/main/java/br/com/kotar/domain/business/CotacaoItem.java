package br.com.kotar.domain.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "COTACAO_ITEM")
public class CotacaoItem extends BaseDomain implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COTACAO_ITEM_ID", nullable = false)
	@JsonProperty("cotacao_item_id")
	private Long id;

	// @JsonIgnore
	@JsonBackReference("cotacao")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_ID", referencedColumnName = "COTACAO_ID", foreignKey = @ForeignKey(name = "FK_cot_item_cotacao"), nullable = false)
	private Cotacao cotacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUTO_ID", referencedColumnName = "PRODUTO_ID", foreignKey = @ForeignKey(name = "FK_cot_item_produto"), nullable = false)
	@JsonProperty("cotacao_item_produto")
	private Produto produto;

	@Column(name = "QUANTIDADE", nullable = false, scale = 14, precision = 3, columnDefinition = "NUMERIC(14,3)")
	private BigDecimal quantidade;

	@Column(name = "OBSERVACAO", nullable = true, length = 2000)
	private String observacao;

	@Transient
	@JsonProperty("cotacao_item_arquivos")
	private List<CotacaoItemArquivo> arquivos;

	@Transient
	@JsonProperty("cotacao_item_fornecedor")
	private List<CotacaoItemFornecedor> listCotacaoFornecedor;

	@Transient
	@JsonProperty("cotacao_item_pendencias_valores")
	private boolean valoresComPendencias;
	
	@Transient
	@JsonProperty("cotacao_item_fornecedor_vencedor")
	private CotacaoItemFornecedor fornecedorVencedor;
	

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

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<CotacaoItemArquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<CotacaoItemArquivo> arquivos) {
		this.arquivos = arquivos;
	}

	@Override
	public CotacaoItem clone() throws CloneNotSupportedException {
		return (CotacaoItem) super.clone();
	}

	public boolean isValoresComPendencias() {
		return valoresComPendencias;
	}

	public void setValoresComPendencias(boolean valoresComPendencias) {
		this.valoresComPendencias = valoresComPendencias;
	}

	public List<CotacaoItemFornecedor> getListCotacaoFornecedor() {
		return listCotacaoFornecedor;
	}

	public void setListCotacaoFornecedor(List<CotacaoItemFornecedor> listCotacaoFornecedor) {
		this.listCotacaoFornecedor = listCotacaoFornecedor;
	}

	public CotacaoItemFornecedor getFornecedorVencedor() {
		return fornecedorVencedor;
	}

	public void setFornecedorVencedor(CotacaoItemFornecedor fornecedorVencedor) {
		this.fornecedorVencedor = fornecedorVencedor;
	}

	

}