package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Bairro;
import br.com.kotar.web.repository.BairroRepository;

@Service
public class BairroService extends BaseCrudService<Bairro>{

	//@formatter:off
	@Autowired BairroRepository bairroRepository;
	//@formatter:on

	@Override
	public CrudRepository<Bairro> getRepository() {
		return bairroRepository;
	}
	
	public Page<Bairro> findByBairroAndCidadeLikeIgnoreCase(String bairro, String cidade, Pageable pageable){
		return bairroRepository.findByBairroAndCidadeLikeIgnoreCase(bairro, cidade, pageable);
	}
	
	public Page<Bairro> findByBairroIdCidadeLikeIgnoreCase(String bairro, 
			Long cidade, Pageable pageable){
		
		return bairroRepository.findByBairroIdCidadeLikeIgnoreCase(bairro, cidade, pageable);
	}

}
