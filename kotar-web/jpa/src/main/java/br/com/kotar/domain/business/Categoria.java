package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.Base64;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.CrudDomain;

//@formatter:off
@Entity
@Table(name = "categoria", 
	uniqueConstraints = { 
			@UniqueConstraint(columnNames = { "nome" }, name = "uidx_categoria") })

@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "categoria_id")) })
//@formatter:on
public class Categoria extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "categoria_id")
	@JsonProperty("categoria_id")
	private Long id;

	@Override
	@JsonProperty("categoria_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@JsonIgnore
	@Lob
	@Column(name = "IMAGEM")
	private byte[] imagem;

	@JsonProperty("imagem64")
	public void setImagem64(String imagem) {
		if (imagem != null) {
			this.imagem = Base64.getDecoder().decode(imagem);
		}
	}

	@JsonProperty("imagem64")
	public String getImagem64() {
		if (this.imagem != null) {
			return Base64.getEncoder().encodeToString(this.imagem);
		}

		return "";
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}