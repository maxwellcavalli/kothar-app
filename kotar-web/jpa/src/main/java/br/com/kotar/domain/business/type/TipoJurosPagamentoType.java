package br.com.kotar.domain.business.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoJurosPagamentoType  {
	
	SEMJUROS("tipo.juros.pagamento.sem"),
	COMJUROS("tipo.juros.pagamento.com");
	
	private String messageKey;
	
	private TipoJurosPagamentoType(String messageKey) {
		this.messageKey = messageKey;
	}

	@JsonCreator
    public static TipoJurosPagamentoType fromValue(final String val){
		return TipoJurosPagamentoType.valueOf(val);
    }
	
	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	

}
