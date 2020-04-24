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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

@Entity
@Table(name = "cliente_endereco")
public class ClienteEndereco extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "CLIENTE_ENDERECO_ID", nullable = false)
	@JsonProperty("cliente_endereco_id")
	private Long id;
	
	@Column(name = "ALIAS", nullable = false)
	@JsonProperty("cliente_endereco_alias")
	private String alias;

	@JoinColumn(name = "CEP_ID", referencedColumnName = "CEP_ID", foreignKey = @ForeignKey(name = "FK_cliente_ende_cep"), nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonProperty("cliente_endereco_cep")
	private Cep cep;
	
	@JsonIgnore
	@JoinColumn(name = "CLIENTE_ID", referencedColumnName = "CLIENTE_ID", foreignKey = @ForeignKey(name = "FK_cliente_ende_cli"), nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)	
	private Cliente cliente;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ENDERECO_COMPLEMENTO_ID", referencedColumnName = "ENDERECO_COMPLEMENTO_ID", foreignKey = @ForeignKey(name = "FK_cliente_ende_endcompl"), nullable = false)
	@JsonProperty("cliente_endereco_complemento")
	private EnderecoComplemento enderecoComplemento;
	
	@Column(name = "PRINCIPAL", nullable = false)
	@JsonProperty("cliente_endereco_principal")
	private boolean principal;

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

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	
	

}