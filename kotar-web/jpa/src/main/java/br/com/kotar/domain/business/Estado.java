package br.com.kotar.domain.business;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.CrudDomain;

//@formatter:off
@Entity
@Table(name = "estado", 
	uniqueConstraints = { @UniqueConstraint(columnNames = { "sigla" }, name = "uidx_estado") }, 
	indexes = {
		@Index(columnList = "nome", name = "idx_estado_nome") })
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "estado_id")) })
//@formatter:on
public class Estado extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "estado_id")
	@JsonProperty("estado_id")
	private Long id;

	@Override
	@JsonProperty("estado_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@Column(name = "sigla", nullable = false, unique = true, length = 3)
	private String sigla;

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		if (sigla != null) {
			sigla = sigla.toUpperCase();
		}
		this.sigla = sigla;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}