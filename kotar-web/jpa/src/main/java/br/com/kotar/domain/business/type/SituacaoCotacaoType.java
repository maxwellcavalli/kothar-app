package br.com.kotar.domain.business.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SituacaoCotacaoType  {
	
	PENDENTE("cotacao.situacao.pendente"),
	ENVIADA("cotacao.situacao.enviada"),
	ENCERRADA("cotacao.situacao.encerrada"), //prazo
	FINALIZADA("cotacao.situacao.finalizada"), //aceita
	CANCELADA("cotacao.situacao.cancelada");
	
	private String messageKey;
	
	private SituacaoCotacaoType(String messageKey) {
		this.messageKey = messageKey;
	}

	@JsonCreator
    public static SituacaoCotacaoType fromValue(final String val){
		return SituacaoCotacaoType.valueOf(val);
    }
	
	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	

}