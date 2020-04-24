package br.com.kotar.domain.security.type;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.kotar.core.enumeration.BaseEnum;

public enum SituacaoUsuarioType implements BaseEnum {

	ATIVO("Ativo"), 
	BLOQUEADO("Bloqueado"), 
	INATIVO("Inativo");

	private String description;

	private SituacaoUsuarioType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JsonCreator
    public static SituacaoUsuarioType fromValue(final String val){
		if (val != null){
			return SituacaoUsuarioType.valueOf(val);
		} else {
			return null;
		}
    }
	
	@JsonValue
	public Object getJsonValue() throws JsonParseException, JsonMappingException, IOException {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"");
		builder.append(this.name());
		builder.append("\"");
		builder.append(":");
		if (this instanceof BaseEnum) {
			builder.append("\"");
			builder.append(((BaseEnum) this).getDescription());
			builder.append("\"");
		} else {
			builder.append("\"");
			builder.append(this.name());
			builder.append("\"");
		}
		builder.append("}");

		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(builder.toString(), Object.class);	
	}

}
