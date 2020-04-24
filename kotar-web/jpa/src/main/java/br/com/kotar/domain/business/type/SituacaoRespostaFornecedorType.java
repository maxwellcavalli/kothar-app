package br.com.kotar.domain.business.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SituacaoRespostaFornecedorType {
	
	RECUSADA(1, "cotacao.resposta.fornecedor.recusada"),
	RESPONDIDA(2, "cotacao.resposta.fornecedor.respondida"),
	RESPONDENDO(3, "cotacao.resposta.fornecedor.respondendo"),
	AGUARDANDO(4, "cotacao.resposta.fornecedor.aguardando"),
	NAO_RSPONDIDA(5, "cotacao.resposta.fornecedor.nao.respondida");
	
	private int code;	
	private String messageKey;
	

	private SituacaoRespostaFornecedorType(int code, String messageKey) {
		this.code = code;
		this.messageKey = messageKey;
	}
	
	public static SituacaoRespostaFornecedorType get(int code){
		for (SituacaoRespostaFornecedorType type : values()){
			if (type.getCode() == code){
				return type;
			}
		}
		
		return null;
	}
	
	@JsonCreator
    public static SituacaoRespostaFornecedorType fromValue(final String val){
		return SituacaoRespostaFornecedorType.valueOf(val);
    }

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

}
