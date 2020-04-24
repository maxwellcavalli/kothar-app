package br.com.kotar.domain.business;

import java.io.Serializable;

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

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.CrudDomain;

//@formatter:off
@Entity
@Table(name = "cep", indexes = { 
		@Index(columnList = "nome", name = "idx_cep_nome"), 
		@Index(columnList = "codigo_postal", name = "idx_cep_postal") 
})

@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "cep_id")) })
//@formatter:on
public class Cep extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cep_id")
	@JsonProperty("cep_id")
	private Long id;

	@Override
	@JsonProperty("cep_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@Column(name = "CODIGO_POSTAL", length = 10, nullable = false)
	private String codigoPostal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BAIRRO_ID", referencedColumnName = "BAIRRO_ID", foreignKey = @ForeignKey(name = "FK_cep_bairro"), nullable = false)
	@JsonProperty("cep_bairro")
	private Bairro bairro;

	public String getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public Bairro getBairro() {
		return bairro;
	}

	public void setBairro(Bairro bairro) {
		this.bairro = bairro;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}