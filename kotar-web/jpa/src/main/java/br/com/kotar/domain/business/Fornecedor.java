package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
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
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.CrudDomain;
import br.com.kotar.core.util.StringUtil;
import br.com.kotar.core.util.formatter.CpfCnpjFormatter;

//@formatter:off
@Entity
@Table(name = "fornecedor", 
	uniqueConstraints = { 
			@UniqueConstraint(columnNames = { "nome", "cpf_cnpj" }, name = "uidx_fornecedor") }, 
	indexes = {
		@Index(columnList = "nome", name = "idx_fornecedor_nome"), 
		@Index(columnList = "cpf_cnpj", name = "idx_fornecedor_cpf_cnpj") })
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "fornecedor_id")) })
//@formatter:on
public class Fornecedor extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	public Fornecedor() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "fornecedor_id")
	@JsonProperty("fornecedor_id")
	private Long id;

	@Override
	@JsonProperty("fornecedor_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@JsonProperty("cpf_cnpj")
	@Column(name = "cpf_cnpj", length = 14, nullable = false)
	private String cpfCnpj;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CEP_ID", referencedColumnName = "CEP_ID", foreignKey = @ForeignKey(name = "FK_fornecedor_cep"), nullable = false)
	@JsonProperty("fornecedor_cep")
	private Cep cep;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "ENDERECO_COMPLEMENTO_ID", referencedColumnName = "ENDERECO_COMPLEMENTO_ID", foreignKey = @ForeignKey(name = "FK_forn_end_compl"))
	@JsonProperty("fornecedor_endereco")
	private EnderecoComplemento enderecoComplemento;

	@Column(name = "email", length = 100, unique = false, nullable = true)
	private String email;

	@Column(name = "telefone", length = 50, unique = false, nullable = true)
	private String telefone;
	
	@Column(name = "uuid", length = 30, unique = false, nullable = true)
	private String uuid;

	@Transient
	@JsonProperty("fornecedor_grupo")
	private List<GrupoProduto> grupos;

	@Transient
	private List<ClienteFornecedor> clientes;

	@JsonProperty("cpf_cnpj_format")
	public String getCpfFormat() {
		String cpf = this.cpfCnpj;
		if (cpf != null) {
			cpf = CpfCnpjFormatter.format(this.cpfCnpj);
		}

		return cpf;
	}

	@Transient
	public String getEnderecoFmt() {
		String retorno = "";
		
		if (cep != null) {
			try {
				StringBuilder builder = new StringBuilder();
				
				if (StringUtil.temValor(cep.getNome())) {
					builder.append(cep.getNome());
				}
		
				if (enderecoComplemento != null) {
		
					if (enderecoComplemento.getNumero() != null && !enderecoComplemento.getNumero().trim().isEmpty()) {
						builder.append(", ");
						builder.append(enderecoComplemento.getNumero());
					}
		
					if (enderecoComplemento.getComplemento() != null && !enderecoComplemento.getComplemento().trim().isEmpty()) {
						builder.append(", ");
						builder.append(enderecoComplemento.getComplemento());
					}
				}
		
				builder.append(" - ");
				builder.append(cep.getBairro().getNome());
				builder.append(" - ");
				builder.append(cep.getBairro().getCidade().getNome());
				builder.append(" - ");
				builder.append(cep.getBairro().getCidade().getEstado().getSigla());
		
				retorno = builder.toString();
			}
			catch (Exception e)
			{
				//e.printStackTrace();
			}
		} 
		
		return retorno;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public List<GrupoProduto> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoProduto> grupos) {
		this.grupos = grupos;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ClienteFornecedor> getClientes() {
		return clientes;
	}

	public void setClientes(List<ClienteFornecedor> clientes) {
		this.clientes = clientes;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}