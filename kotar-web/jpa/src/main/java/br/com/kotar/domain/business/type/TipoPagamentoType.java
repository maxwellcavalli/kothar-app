package br.com.kotar.domain.business.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoPagamentoType  {
	
	AVISTA("tipo.pagamento.avista"),
	APRAZO("tipo.pagamento.aprazo");
	
	private String messageKey;
	
	private TipoPagamentoType(String messageKey) {
		this.messageKey = messageKey;
	}

	@JsonCreator
    public static TipoPagamentoType fromValue(final String val){
		return TipoPagamentoType.valueOf(val);
    }
	
	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	

}
