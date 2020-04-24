package br.com.kotar.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.ClienteFornecedor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.web.repository.ClienteFornecedorRepository;

@Service
public class ClienteFornecedorService extends BaseService<ClienteFornecedor> {

	//@formatter:off
	@Autowired ClienteFornecedorRepository clienteFornecedorRepository;
	//@formatter:on

	@Override
	public BaseRepository<ClienteFornecedor> getRepository() {
		return clienteFornecedorRepository;
	}

	public void saveOrUpdate(Fornecedor fornecedor, List<ClienteFornecedor> representantes) throws Exception {
		List<ClienteFornecedor> representantesDB = clienteFornecedorRepository.findByFornecedor(fornecedor);
		for (ClienteFornecedor clienteFornecedorDB : representantesDB) {
			boolean exists = false;

			for (ClienteFornecedor clienteFornecedor : representantes) {
				if (clienteFornecedorDB.getId() == null || clienteFornecedor.getId().longValue() == 0) {
					continue;
				}

				if (clienteFornecedor.getId().longValue() == clienteFornecedorDB.getId().longValue()) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				clienteFornecedorRepository.delete(clienteFornecedorDB);
			}
		}

		for (ClienteFornecedor representanteFornecedor : representantes) {
			representanteFornecedor.setFornecedor(fornecedor);
			clienteFornecedorRepository.save(representanteFornecedor);
		}
	}

	public void saveOrUpdate(Fornecedor fornecedor, Cliente cliente) throws Exception {
		ClienteFornecedor clienteFornecedor = clienteFornecedorRepository.findByClienteAndFornecedor(cliente, fornecedor);
		if (clienteFornecedor == null) {
			clienteFornecedor = new ClienteFornecedor();
			clienteFornecedor.setCliente(cliente);
			clienteFornecedor.setFornecedor(fornecedor);
			clienteFornecedorRepository.save(clienteFornecedor);
		}
	}

	public void delete(Fornecedor fornecedor, Cliente cliente) throws Exception {
		ClienteFornecedor clienteFornecedor = clienteFornecedorRepository.findByClienteAndFornecedor(cliente, fornecedor);
		if (clienteFornecedor != null) {
			clienteFornecedorRepository.delete(clienteFornecedor);
		}
	}

	public void delete(Cliente cliente) throws Exception {
		List<ClienteFornecedor> list = clienteFornecedorRepository.findByCliente(cliente);
		clienteFornecedorRepository.deleteAll(list);
	}

	public List<ClienteFornecedor> findByFornecedor(Fornecedor fornecedor) throws Exception {
		return clienteFornecedorRepository.findByFornecedor(fornecedor);
	}

}
