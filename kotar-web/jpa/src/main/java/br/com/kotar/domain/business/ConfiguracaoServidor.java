package br.com.kotar.domain.business;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.core.domain.BaseDomain;

//@formatter:off
@Entity
@Table(name = "configuracao_servidor")

@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "configuracao_servidor_id")) })
//@formatter:on
public class ConfiguracaoServidor extends BaseDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "configuracao_servidor_id")
	@JsonProperty("configuracao_servidor_id")
	private Long id;
	
	@Column(name = "push_server_key", length = 512, unique = true, nullable = true)
	@JsonProperty("push_server_key")
	private String pushServerKey;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPushServerKey() {
		return pushServerKey;
	}

	public void setPushServerKey(String pushServerKey) {
		this.pushServerKey = pushServerKey;
	}
}