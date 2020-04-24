package br.com.kotar.domain.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import br.com.kotar.core.util.StringUtil;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;

public class SituacaoCotacaoTypeDeserializer extends JsonDeserializer<SituacaoCotacaoType> {

    @Override
    public SituacaoCotacaoType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        SituacaoCotacaoType type = null;
        try{

            if (node.fieldNames().hasNext()) {
                String name = node.fieldNames().next();
                if (StringUtil.temValor(name)) {
                    type = SituacaoCotacaoType.fromValue(name);
                    if (type != null) {
                        return type;
                    }
                }
            } else {
            	if (node.asText() != null && !node.asText().trim().isEmpty()){
            		type = SituacaoCotacaoType.fromValue(node.asText());
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
