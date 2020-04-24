package br.com.kotar.domain.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import br.com.kotar.core.util.StringUtil;
import br.com.kotar.domain.business.type.TipoJurosPagamentoType;

public class TipoJurosPagamentoTypeDeserializer extends JsonDeserializer<TipoJurosPagamentoType> {

    @Override
    public TipoJurosPagamentoType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        TipoJurosPagamentoType type = null;
        try{

            if (node.fieldNames().hasNext()) {
                String name = node.fieldNames().next();
                if (StringUtil.temValor(name)) {
                    type = TipoJurosPagamentoType.fromValue(name);
                    if (type != null) {
                        return type;
                    }
                }
            } else {
            	if (node.asText() != null && !node.asText().trim().isEmpty()){
            		type = TipoJurosPagamentoType.fromValue(node.asText());
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
