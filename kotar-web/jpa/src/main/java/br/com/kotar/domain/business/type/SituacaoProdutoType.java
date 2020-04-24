package br.com.kotar.domain.business.type;

import br.com.kotar.core.enumeration.BaseEnum;

public enum SituacaoProdutoType implements BaseEnum{

	CADASTRADO("Cadastrado"), 
	ANALISE("Em Analise"), 
	BLOQUEADO("Bloqueado");

	private String description;
	
	private SituacaoProdutoType(String description) {
		this.description = description;
	}
	
	public static SituacaoProdutoType get(int key) {
		for (SituacaoProdutoType type : SituacaoProdutoType.values()){
			if (type.ordinal() == key){
				return type;
			}
		}
		
		return null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
