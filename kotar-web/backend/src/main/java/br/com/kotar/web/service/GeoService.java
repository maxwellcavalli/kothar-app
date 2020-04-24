package br.com.kotar.web.service;

import java.math.BigDecimal;

import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import br.com.kotar.core.helper.map.EnderecoCoordenadaHelper;
import br.com.kotar.core.helper.map.LatLng;
import br.com.kotar.core.util.Constants;
import br.com.kotar.core.util.MapUtil;
import br.com.kotar.core.util.http.HttpUtil;
import br.com.kotar.domain.business.Cep;
import br.com.kotar.domain.business.Cidade;
import br.com.kotar.domain.business.EnderecoComplemento;

@Component
public class GeoService {

	@Autowired
	Environment env;

	public EnderecoComplemento geoReferenciar(Cep cep, EnderecoComplemento enderecoComplemento) throws Exception {
		if (Constants.HAS_PROXY == null) {
			HttpUtil.init(env);
		}

		if (cep != null) {

			String rua = cep.getNome();
			String numero = enderecoComplemento.getNumero();

			Cidade cid = cep.getBairro().getCidade();

			String cidade = cid.getNome();
			String siglaUf = cid.getEstado().getSigla();

			EnderecoCoordenadaHelper resultado = MapUtil.buscarLatitudeLongitude(rua, numero, cidade, siglaUf);

			if (resultado != null) {
				enderecoComplemento.setLatitude(new BigDecimal(resultado.getNrLatitude()));
				enderecoComplemento.setLongitude(new BigDecimal(resultado.getNrLongitude()));
			}
		}
		return enderecoComplemento;
	}
	
	
	public Integer calcularDistancia(LatLng origin, LatLng dest) {
		Integer retorno = null;
		if (Constants.HAS_PROXY == null) {
			HttpUtil.init(env);
		}
		
		if (origin != null && dest != null && origin.latitude != null && origin.longitude != null && dest.latitude != null && dest.longitude != null) {
			retorno = new MapUtil().calcularDistanciaGoogleMaps(origin, dest, 5);	
		}
		
		return retorno;
	}
	
	
	/* Teste de cálculo de distância*/
	public static void main(String[] args) {
		
		HttpHost proxy = new HttpHost("127.0.0.1", Integer.valueOf(3129));
		
		Constants.PROXY = proxy;
		Constants.HAS_PROXY = true;
		
		LatLng origin = new LatLng(new Double(-25.4290405000), new Double(-49.2636146000));
		LatLng dest = new LatLng(new Double(-25.4336365000), new Double(-49.2724942000));
		
		Integer distancia = new MapUtil().calcularDistanciaGoogleMaps(origin, dest, 5);	
		
		System.out.println("DISTANCIA = "+distancia);
	}
	

}
