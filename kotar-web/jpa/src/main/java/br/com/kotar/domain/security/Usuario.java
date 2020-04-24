package br.com.kotar.domain.security;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.com.kotar.core.domain.CrudDomain;
import br.com.kotar.domain.jackson.SituacaoUsuarioTypeDeserializer;
import br.com.kotar.domain.security.type.SituacaoUsuarioType;

//@formatter:off
@Entity
@Table(name = "usuario", 
	uniqueConstraints = { 
			@UniqueConstraint(columnNames = { "username" }, name = "uidx_usuario") }, 
	indexes = {
		@Index(columnList = "nome", name = "idx_usuario_nome") })
@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "usuario_id")) })
//@formatter:on
public class Usuario extends CrudDomain implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "usuario_id")
	@JsonProperty("usuario_id")
	private Long id;

	@Override
	@JsonProperty("usuario_nome")
	public void setNome(String nome) {
		super.setNome(nome);
	}

	@Column(name = "USERNAME", length = 48, unique = true, nullable = false)
	private String username;

	@JsonIgnore
	@Column(name = "PASSWORD", length = 256, unique = false, nullable = false)
	private String password;

	@Column(name = "email", length = 100, unique = false, nullable = true)
	private String email;

	@Column(name = "telefone", length = 50, unique = false, nullable = true)
	private String telefone;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED", unique = false, nullable = true)
	private Date updated;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_LOGIN", unique = false, nullable = true)
	private Date lastLogin;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "usuario", cascade = CascadeType.ALL)
	private Set<LogUsuario> logAcesso;

	@JsonProperty("usuario_situacao")
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "SITUACAO_USUARIO", nullable = false)
	@JsonDeserialize(using = SituacaoUsuarioTypeDeserializer.class)
	private SituacaoUsuarioType situacao = SituacaoUsuarioType.ATIVO;

	@Column(name = "RESET_TOKEN", length = 50, unique = false, nullable = true)
	private String resetToken;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATE_RESET_TOKEN", unique = false, nullable = true)
	private Date dateResetToken;

	@Column(name = "ID_FACEBOOK", length = 50, unique = true, nullable = true)
	private String idFacebook;

	@Transient
	private String tokenFacebook;

	@Column(name = "ID_GOOGLE", length = 50, unique = true, nullable = true)
	private String idGoogle;
	
	@Column(name = "LANGUAGE", length = 5)
	private String language;
	
	@Column(name = "ADMIN")
	private boolean admin;	

	@Transient
	private String tokenGoogle;

	@Transient
	private String passwordLogin;

	@Transient
	private String token;

	@Transient
	private boolean loginCliente;

	@Transient
	@JsonProperty("usuario_situacao_str")
	public String getSituacaoStr() {
		return this.situacao.getDescription();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Set<LogUsuario> getLogAcesso() {
		return logAcesso;
	}

	public void setLogAcesso(Set<LogUsuario> logAcesso) {
		this.logAcesso = logAcesso;
	}

	public String getPasswordLogin() {
		return passwordLogin;
	}

	public void setPasswordLogin(String passwordLogin) {
		this.passwordLogin = passwordLogin;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public SituacaoUsuarioType getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoUsuarioType situacao) {
		this.situacao = situacao;
	}

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public Date getDateResetToken() {
		return dateResetToken;
	}

	public void setDateResetToken(Date dateResetToken) {
		this.dateResetToken = dateResetToken;
	}

	public String getIdFacebook() {
		return idFacebook;
	}

	public void setIdFacebook(String idFacebook) {
		this.idFacebook = idFacebook;
	}

	public String getTokenFacebook() {
		return tokenFacebook;
	}

	public void setTokenFacebook(String tokenFacebook) {
		this.tokenFacebook = tokenFacebook;
	}

	public String getIdGoogle() {
		return idGoogle;
	}

	public void setIdGoogle(String idGoogle) {
		this.idGoogle = idGoogle;
	}

	public String getTokenGoogle() {
		return tokenGoogle;
	}

	public void setTokenGoogle(String tokenGoogle) {
		this.tokenGoogle = tokenGoogle;
	}

	public boolean isLoginCliente() {
		return loginCliente;
	}

	public void setLoginCliente(boolean loginCliente) {
		this.loginCliente = loginCliente;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
