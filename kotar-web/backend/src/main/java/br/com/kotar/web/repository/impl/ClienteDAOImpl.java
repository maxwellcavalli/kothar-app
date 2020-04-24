package br.com.kotar.web.repository.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.repository.impl.BaseCrudRepositoryImpl;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.ClienteRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ClienteDAOImpl extends BaseCrudRepositoryImpl<Cliente> implements ClienteRepository {
	//@formatter:off
	@Autowired ClienteRepository clienteRepository;
	//@formatter:on

	@Override
	public CrudRepository<Cliente> getRepository() {
		return clienteRepository;
	}

	public Page<Cliente> findByNomeOrCpfLikeIgnoreCase(String nome, String cpf, Usuario usuario, Pageable pageable) throws Exception {

		StringBuilder hql = new StringBuilder();
		hql.append(" select cliente ");
		hql.append("   from Cliente cliente ");
		hql.append("   left join cliente.usuario ");
		hql.append("  where 1 = 1 ");

		Map<String, Object> parameters = new HashMap<>();
		if (nome != null && !nome.trim().isEmpty()) {
			hql.append("    and upper(cliente.nome) like upper(:nome) ");
			parameters.put("nome", nome);
		}

		if (cpf != null && !cpf.trim().isEmpty()) {
			hql.append("   and upper(cliente.cpf) = (:cpf) ");
			parameters.put("cpf", cpf);
		}

		if (!usuario.isAdmin()) {
			Cliente cli = clienteRepository.findByUsuario(usuario);

			hql.append(" and cliente.id = :id ");
			parameters.put("id", cli.getId());
		}

		return searchPaginated(hql.toString(), pageable, parameters);
	}

	@Override
	public Cliente findByUsuario(Usuario usuario) {
		return clienteRepository.findByUsuario(usuario);
	}

}
