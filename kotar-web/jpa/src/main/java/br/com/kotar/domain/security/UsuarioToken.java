package br.com.kotar.domain.security;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

//@formatter:off
@Entity
@Table(name = "usuario_token", 
	uniqueConstraints = { 
			@UniqueConstraint(columnNames = { "KOTHAR_TOKEN" }, name = "uidx_KTOKEN"),
			@UniqueConstraint(columnNames = { "PUSH_TOKEN_ID" }, name = "uidx_PTOKEN")})
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "usuario_token_id")) })
//@formatter:on
public class UsuarioToken extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "usuario_token_id")
	@JsonProperty("usuario_token_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USUARIO_ID", referencedColumnName = "USUARIO_ID", foreignKey = @ForeignKey(name = "FK_usuario_token_usuario"), nullable = false)
	@JsonProperty("usuario_token_usuario")
	private Usuario usuario;	

	@Column(name = "KOTHAR_TOKEN", length = 512, unique = true, nullable = false)
	@JsonProperty("kothar_token")
	private String kotharToken;
	
	@Column(name = "PUSH_TOKEN_ID", length = 512, unique = true, nullable = false)
	@JsonProperty("push_token_id")
	private String pushTokenId;
	
	@Column(name = "ORIGEM", length = 512, nullable = true)
	@JsonProperty("origem")
	private String origem;	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPushTokenId() {
		return pushTokenId;
	}

	public void setPushTokenId(String pushTokenId) {
		this.pushTokenId = pushTokenId;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getKotharToken() {
		return kotharToken;
	}

	public void setKotharToken(String kotharToken) {
		this.kotharToken = kotharToken;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
}