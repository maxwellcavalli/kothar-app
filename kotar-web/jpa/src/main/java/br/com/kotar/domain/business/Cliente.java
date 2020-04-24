package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import br.com.kotar.core.util.formatter.CpfCnpjFormatter;
import br.com.kotar.domain.security.Usuario;

//@formatter:off
@Entity
@Table(name = "cliente", 
	uniqueConstraints = { 
			@UniqueConstraint(columnNames = { "nome", "cpf" }, name = "uidx_cliente") }, 
	indexes = {
		@Index(columnList = "nome", name = "idx_cliente_nome"), 
		@Index(columnList = "cpf", name = "idx_cliente_cpf") })

@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "cliente_id")) })
//@formatter:on
public class Cliente extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cliente_id")
	@JsonProperty("cliente_id")
	private Long id;

	@Override
	@JsonProperty("cliente_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@Column(name = "cpf", length = 11, nullable = true)
	private String cpf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USUARIO_ID", referencedColumnName = "USUARIO_ID", foreignKey = @ForeignKey(name = "FK_cliente_usuario"), nullable = false)
	@JsonProperty("cliente_usuario")
	private Usuario usuario;

	@Transient
	@JsonProperty("cliente_enderecos")
	private List<ClienteEndereco> listEnderecos;

	@Transient
	@JsonProperty("cpf_format")
	public String getCpfFormat() {
		String cpf = this.cpf;
		if (cpf != null) {
			cpf = CpfCnpjFormatter.format(this.cpf);
		}

		return cpf;
	}
	
	@Transient
	@JsonProperty("cliente_primeiro_nome")
	public String getPrimeiroNome() {
        String retorno = "";

        if (getNome() != null && !getNome().trim().isEmpty()) {
            int espaco = getNome().trim().indexOf(" ");
            if (espaco != -1) {
                retorno = getNome().trim().substring(0, espaco);
            }
            else {
            	retorno = getNome().trim();
            }
        }
        return retorno;
    }

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ClienteEndereco> getListEnderecos() {
		return listEnderecos;
	}

	public void setListEnderecos(List<ClienteEndereco> listEnderecos) {
		this.listEnderecos = listEnderecos;
	}

}