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
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.CrudDomain;

//@formatter:off
@Entity
@Table(name = "cidade",
	uniqueConstraints={
			@UniqueConstraint(columnNames={"estado_id", "nome"}, name="uidx_cidade")
	},
	indexes={
			@Index(columnList="nome", name="idx_cidade_nome")
	}
)

@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "cidade_id")) })
//@formatter:on
public class Cidade extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "cidade_id")
	@JsonProperty("cidade_id")
	private Long id;

	@Override
	@JsonProperty("cidade_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESTADO_ID", referencedColumnName = "ESTADO_ID", foreignKey = @ForeignKey(name = "FK_cidade_estado"), nullable = false)
	@JsonProperty("cidade_estado")
	private Estado estado;

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}