package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.CotacaoEndereco;
import br.com.kotar.domain.business.EnderecoComplemento;
import br.com.kotar.web.repository.CotacaoEnderecoRepository;

@Service
public class CotacaoEnderecoService extends BaseService<CotacaoEndereco> {

	//@formatter:off
	
	@Autowired CotacaoEnderecoRepository cotacaoEnderecoRepository;
	@Autowired EnderecoComplementoService enderecoComplementoService;
	//@formatter:on

	@Override
	public BaseRepository<CotacaoEndereco> getRepository() {
		return cotacaoEnderecoRepository;
	}
	
	public CotacaoEndereco persistir(CotacaoEndereco cotacaoEndereco) throws Exception {
		EnderecoComplemento enderecoComplemento = cotacaoEndereco.getEnderecoComplemento();
		EnderecoComplemento _new = enderecoComplemento.clone();
		_new.setId(null);
		
		_new = enderecoComplementoService.save(_new);
		
		//verificar se o endereco esta geo-referenciado
		if (_new.getLatitude() == null){
			
			
		}
		
		cotacaoEndereco.setEnderecoComplemento(_new);
		
		return save(cotacaoEndereco);
	}
	
}