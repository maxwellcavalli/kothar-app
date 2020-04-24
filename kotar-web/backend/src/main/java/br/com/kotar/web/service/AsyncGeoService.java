package br.com.kotar.web.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.com.kotar.core.helper.map.LatLng;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.Fornecedor;

@Service
public class AsyncGeoService {
	
	//@formatter:off
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	@Autowired GeoService geoService;
	@Autowired FornecedorService fornecedorService;
	//@formatter:on
	
	@Async
	public CompletableFuture<Boolean> geoReferenciar(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) throws InterruptedException {
		Optional<CotacaoFornecedor> cotacaoFornecedorOptional = cotacaoFornecedorService.findById(cotacaoFornecedor.getId());

		if (!cotacaoFornecedorOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacaoFornecedor = cotacaoFornecedorOptional.get();
		Fornecedor fornecedor = cotacaoFornecedor.getFornecedor();

		Optional<Fornecedor> fornecedorOptional = fornecedorService.findById(fornecedor.getId());
		if (!fornecedorOptional.isPresent()){
			throw new RecordNotFound();
		}
		fornecedor = fornecedorOptional.get();
		
		
		BigDecimal latitudeOrigem = cotacao.getCotacaoEndereco().getEnderecoComplemento().getLatitude();		
		BigDecimal longitudeOrigem = cotacao.getCotacaoEndereco().getEnderecoComplemento().getLongitude();		
		LatLng latLngOrigem = new LatLng(latitudeOrigem.doubleValue(), longitudeOrigem.doubleValue());
		BigDecimal latitudeDestino = fornecedor.getEnderecoComplemento().getLatitude();		
		BigDecimal longitudeDestino = fornecedor.getEnderecoComplemento().getLongitude();		
		LatLng latLngDestino = new LatLng(latitudeDestino.doubleValue(), longitudeDestino.doubleValue());
		Integer distancia = geoService.calcularDistancia(latLngOrigem, latLngDestino);
		cotacaoFornecedor.setDistancia(distancia);
		
		cotacaoFornecedorService.save(cotacaoFornecedor);
		
		return CompletableFuture.completedFuture(Boolean.TRUE);
	}

}
