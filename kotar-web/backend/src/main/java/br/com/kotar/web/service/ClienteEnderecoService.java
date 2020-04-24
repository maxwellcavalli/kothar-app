package br.com.kotar.web.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cep;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.ClienteEndereco;
import br.com.kotar.domain.business.EnderecoComplemento;
import br.com.kotar.web.repository.ClienteEnderecoRepository;

@Service
public class ClienteEnderecoService extends BaseService<ClienteEndereco> {

	//@formatter:off
	@Autowired ClienteEnderecoRepository clienteEnderecoRepository;
	@Autowired GeoService geoService;
	//@formatter:on

	@Override
	public BaseRepository<ClienteEndereco> getRepository() {
		return clienteEnderecoRepository;
	}

	public List<ClienteEndereco> findByCliente(Cliente cliente) {
		return clienteEnderecoRepository.findByCliente(cliente);
	}

	public void persistir(Cliente cliente, List<ClienteEndereco> listEnderecos) throws Exception {
		List<ClienteEndereco> listEnderecosDB = findByCliente(cliente);
		for (ClienteEndereco clienteEnderecoDB : listEnderecosDB) {
			boolean exists = false;

			for (ClienteEndereco clienteEndereco : listEnderecos) {
				if (clienteEndereco.getId() == null || clienteEndereco.getId().longValue() == 0) {
					continue;
				}

				if (clienteEndereco.getId().longValue() == clienteEnderecoDB.getId().longValue()) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				delete(clienteEnderecoDB);
			}
		}

		if (listEnderecos != null) {
			for (ClienteEndereco clienteEndereco : listEnderecos) {

				Cep cep = clienteEndereco.getCep();
				EnderecoComplemento enderecoComplemento = clienteEndereco.getEnderecoComplemento();
				if (enderecoComplemento.getNumero() == null) {
					enderecoComplemento.setNumero("");
				}

				boolean hasChanged = false;
				if (enderecoComplemento.getLatitude() == null || enderecoComplemento.getLongitude() == null || 
						(enderecoComplemento.getLatitude().doubleValue() == 0.0 && enderecoComplemento.getLongitude().doubleValue() == 0.0)) {
					hasChanged = true;
				} else {
					if (clienteEndereco.getId() != null && clienteEndereco.getId().longValue() > 0) {

						Optional<ClienteEndereco> clienteEnderecoOptional = getRepository().findById(clienteEndereco.getId());

						if (clienteEnderecoOptional.isPresent()) {
							ClienteEndereco _clienteEnderecoOld = clienteEnderecoOptional.get();
							Cep _cepOld = _clienteEnderecoOld.getCep();
							EnderecoComplemento _enderecoComplementoOld = _clienteEnderecoOld.getEnderecoComplemento();
							if (_enderecoComplementoOld.getNumero() == null) {
								_enderecoComplementoOld.setNumero("");
							}

							if (_cepOld.getId().longValue() != cep.getId().longValue()
									|| !enderecoComplemento.getNumero().equals(_enderecoComplementoOld.getNumero())) {

								hasChanged = true;
							}
						}
					}
				}

				if (hasChanged) {
					enderecoComplemento = geoService.geoReferenciar(cep, enderecoComplemento);
					clienteEndereco.setEnderecoComplemento(enderecoComplemento);
				}

				clienteEndereco.setCliente(cliente);
				save(clienteEndereco);
			}
		}
	}
}