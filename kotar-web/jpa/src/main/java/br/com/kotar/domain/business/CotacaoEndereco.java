package br.com.kotar.domain.business;

import java.io.Serializable;

import javax.persistence.CascadeType;
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

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "cotacao_endereco")
public class CotacaoEndereco extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COTACAO_ENDERECO_ID", nullable = false)
	@JsonProperty("cotacao_endereco_id")
	private Long id;
	
	@Column(name = "ALIAS", nullable = false)
	@JsonProperty("cotacao_endereco_alias")
	private String alias;

	@JoinColumn(name = "CEP_ID", referencedColumnName = "CEP_ID", foreignKey = @ForeignKey(name = "FK_cotacao_ende_cep"), nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonProperty("cotacao_endereco_cep")
	private Cep cep;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ENDERECO_COMPLEMENTO_ID", referencedColumnName = "ENDERECO_COMPLEMENTO_ID", foreignKey = @ForeignKey(name = "FK_cotacao_ende_endcompl"), nullable = false)
	@JsonProperty("cotacao_endereco_complemento")
	private EnderecoComplemento enderecoComplemento;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cep getCep() {
		return cep;
	}

	public void setCep(Cep cep) {
		this.cep = cep;
	}

	public EnderecoComplemento getEnderecoComplemento() {
		return enderecoComplemento;
	}

	public void setEnderecoComplemento(EnderecoComplemento enderecoComplemento) {
		this.enderecoComplemento = enderecoComplemento;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}