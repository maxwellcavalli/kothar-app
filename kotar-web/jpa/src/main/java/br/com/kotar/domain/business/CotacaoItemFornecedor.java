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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

//@formatter:off
@Entity
@Table(name = "COTACAO_ITEM_FORNECEDOR",
	indexes={
		@Index(columnList="COTACAO_ITEM_ID, COTACAO_FORNECEDOR_ID", name="idx_cotacao_item_fornecedor")
		
	}
)
//@formatter:on
public class CotacaoItemFornecedor extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COTACAO_ITEM_FORNECEDOR_ID", nullable = false)
	@JsonProperty("cotacao_item_fornecedor_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_ITEM_ID", referencedColumnName = "COTACAO_ITEM_ID", foreignKey = @ForeignKey(name = "FK_cotitemfor_cotitem"), nullable = false)
	@JsonProperty("cotacao_item")
	private CotacaoItem cotacaoItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COTACAO_FORNECEDOR_ID", referencedColumnName = "COTACAO_FORNECEDOR_ID", foreignKey = @ForeignKey(name = "FK_cotitemfor_forn"), nullable = false)
	@JsonProperty("cotacao_fornecedor")
	private CotacaoFornecedor cotacaoFornecedor;

	@JsonProperty("ignorar_item")
	@Column(name = "IGNORAR_ITEM", nullable = false)
	private boolean ignorarItem;

	@Transient
	@JsonProperty("valor_unitario")
	private BigDecimal valorUnitario;

	@Transient
	@JsonProperty("valores")
	private List<CotacaoItemFornecedorValor> valores;
	
	@Transient
	@JsonProperty("melhor_valor")
	private CotacaoItemFornecedorValor cotacaoItemFornecedorValor;
	
	@Transient
	private boolean hasPendencias;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CotacaoItem getCotacaoItem() {
		return cotacaoItem;
	}

	public void setCotacaoItem(CotacaoItem cotacaoItem) {
		this.cotacaoItem = cotacaoItem;
	}

	public boolean isIgnorarItem() {
		return ignorarItem;
	}

	public void setIgnorarItem(boolean ignorarItem) {
		this.ignorarItem = ignorarItem;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public List<CotacaoItemFornecedorValor> getValores() {
		return valores;
	}

	public void setValores(List<CotacaoItemFornecedorValor> valores) {
		this.valores = valores;
	}

	public CotacaoFornecedor getCotacaoFornecedor() {
		return cotacaoFornecedor;
	}

	public void setCotacaoFornecedor(CotacaoFornecedor cotacaoFornecedor) {
		this.cotacaoFornecedor = cotacaoFornecedor;
	}

	public CotacaoItemFornecedorValor getCotacaoItemFornecedorValor() {
		return cotacaoItemFornecedorValor;
	}

	public void setCotacaoItemFornecedorValor(CotacaoItemFornecedorValor cotacaoItemFornecedorValor) {
		this.cotacaoItemFornecedorValor = cotacaoItemFornecedorValor;
	}

	public boolean isHasPendencias() {
		return hasPendencias;
	}

	public void setHasPendencias(boolean hasPendencias) {
		this.hasPendencias = hasPendencias;
	}

}