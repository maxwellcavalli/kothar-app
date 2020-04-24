package br.com.kotar.domain.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import br.com.kotar.core.util.StringUtil;
import br.com.kotar.domain.security.type.SituacaoUsuarioType;

public class SituacaoUsuarioTypeDeserializer extends JsonDeserializer<SituacaoUsuarioType> {

    @Override
    public SituacaoUsuarioType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        SituacaoUsuarioType type = null;
        try{

            if (node.fieldNames().hasNext()) {
                String name = node.fieldNames().next();
                if (StringUtil.temValor(name)) {
                    type = SituacaoUsuarioType.fromValue(name);
                    if (type != null) {
                        return type;
                    }
                }
            } else {
            	if (node.asText() != null && !node.asText().trim().isEmpty()){
            		type = SituacaoUsuarioType.fromValue(node.asText());
                    if (type != null) {
                        return type;
                    }
            	}
            }
        }catch(Exception e){
            type = null;
        }
        return type;
    }
}
