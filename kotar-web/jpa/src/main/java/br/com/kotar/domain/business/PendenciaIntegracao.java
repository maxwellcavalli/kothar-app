package br.com.kotar.domain.business;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;
import br.com.kotar.domain.business.type.TipoPendenciaIntegracaoType;
import br.com.kotar.domain.security.Usuario;

//@formatter:off
@Entity
@Table(name = "pendencia_integracao")

//@formatter:on
public class PendenciaIntegracao extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pendencia_integracao_id")
	@JsonProperty("pendencia_integracao_id")
	private Long id;

	@Lob
	@Column(name = "objeto")
	private byte[] objeto;
	
	@JsonProperty("data_criacao")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_criacao")
	private Date dataCriacao;
	
	@JsonProperty("data_alteracao")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao")
	private Date dataAlteracao;
	
	@JsonProperty("usuario_criacao")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USUARIO_CRIACAO_ID", referencedColumnName = "USUARIO_ID", foreignKey = @ForeignKey(name = "FK_pendite_usucri"), nullable = false)
	private Usuario usuarioCriacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USUARIO_ALTERACAO_ID", referencedColumnName = "USUARIO_ID", foreignKey = @ForeignKey(name = "FK_pendite_usualt"), nullable = true)
	private Usuario usuarioAlteracao;
	
	@Column(name = "finalizado")
	private boolean finalizado;	
	
	@JsonProperty("tipo_pendencia")
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "tipo_pendencia", length=256)
	private TipoPendenciaIntegracaoType tipoPendenciaIntegracao;
	
	@JsonProperty("tipo_pendencia_str")
	@javax.persistence.Transient
	private String tipoPendenciaStr;

	@JsonProperty("objectStr")
	public void setObjectStr(String value) {
		if (value != null) {
			this.objeto = value.getBytes();
		}
	}
	
	@JsonProperty("objectStr")
	@javax.persistence.Transient
	public String getObjectStr(){
		if (objeto != null && objeto.length > 0){
			return new String(objeto);
		} else {
			return null;
		}
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getObjeto() {
		return objeto;
	}

	public void setObjeto(byte[] objeto) {
		this.objeto = objeto;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public Usuario getUsuarioCriacao() {
		return usuarioCriacao;
	}

	public void setUsuarioCriacao(Usuario usuarioCriacao) {
		this.usuarioCriacao = usuarioCriacao;
	}

	public Usuario getUsuarioAlteracao() {
		return usuarioAlteracao;
	}

	public void setUsuarioAlteracao(Usuario usuarioAlteracao) {
		this.usuarioAlteracao = usuarioAlteracao;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public TipoPendenciaIntegracaoType getTipoPendenciaIntegracao() {
		return tipoPendenciaIntegracao;
	}

	public void setTipoPendenciaIntegracao(TipoPendenciaIntegracaoType tipoPendenciaIntegracao) {
		this.tipoPendenciaIntegracao = tipoPendenciaIntegracao;
	}

	public String getTipoPendenciaStr() {
		return tipoPendenciaStr;
	}

	public void setTipoPendenciaStr(String tipoPendenciaStr) {
		this.tipoPendenciaStr = tipoPendenciaStr;
	}



}