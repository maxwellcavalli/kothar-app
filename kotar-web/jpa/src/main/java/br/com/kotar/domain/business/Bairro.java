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
@Table(name = "bairro",
	uniqueConstraints={
			@UniqueConstraint(columnNames={"bairro_id", "nome"}, name="uidx_bairro")
	},
	indexes={
			@Index(columnList="nome", name="idx_bairro_nome")
	}
)

@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "bairro_id")) })
//@formatter:on
public class Bairro extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bairro_id")
	@JsonProperty("bairro_id")
	private Long id;

	@Override
	@JsonProperty("bairro_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CIDADE_ID", referencedColumnName = "CIDADE_ID", foreignKey = @ForeignKey(name = "FK_bairro_cidade"), nullable = false)
	@JsonProperty("bairro_cidade")
	private Cidade cidade;

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}