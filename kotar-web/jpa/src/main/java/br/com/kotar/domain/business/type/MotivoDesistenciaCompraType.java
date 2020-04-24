package br.com.kotar.domain.business.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MotivoDesistenciaCompraType  {
	
	NAO_CUMPRIU_PRAZO("desistencia.nao.cumpriu.prazo"),
	NAO_CUMPRIU_PRECO("desistencia.nao.cumpriu.preco"),
	PRODUTO_FORA_DAS_ESPECIFICACOES("desistencia.produto.fora.especificacoes"),
	OUTROS("desistencia.outros");
	
	
	private String messageKey;
	
	private MotivoDesistenciaCompraType(String messageKey) {
		this.messageKey = messageKey;
	}

	@JsonCreator
    public static MotivoDesistenciaCompraType fromValue(final String val){
		return MotivoDesistenciaCompraType.valueOf(val);
    }
	
	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
}