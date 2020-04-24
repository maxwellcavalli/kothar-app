package br.com.kotar.domain.business.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoPendenciaIntegracaoType  {
	
	NOVO_PRODUTO("pendencia.integracao.tipo.novo.produto"),
	ADICIONAR_FOTOS("pendencia.integracao.tipo.add.photos");	
	
	private String messageKey;
	
	private TipoPendenciaIntegracaoType(String messageKey) {
		this.messageKey = messageKey;
	}

	@JsonCreator
    public static TipoPendenciaIntegracaoType fromValue(final String val){
		return TipoPendenciaIntegracaoType.valueOf(val);
    }
	
	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	

}
