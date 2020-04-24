package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Cep;
import br.com.kotar.web.repository.CepRepository;

@Service
public class CepService extends BaseCrudService<Cep> {

	//@formatter:off
	@Autowired CepRepository cepRepository;
	//@formatter:on

	@Override
	public CrudRepository<Cep> getRepository() {
		return cepRepository;
	}

	public Page<Cep> findByCepAndBairroAndCidadeLikeIgnoreCase(String cep, String bairro, String cidade, Pageable pageable) {
		return cepRepository.findByCepAndBairroAndCidadeLikeIgnoreCase(cep, bairro, cidade, pageable);
	}

	public Cep findByCodigoPostal(String codigoPostal) {
		return cepRepository.findByCodigoPostal(codigoPostal);
	}

}
