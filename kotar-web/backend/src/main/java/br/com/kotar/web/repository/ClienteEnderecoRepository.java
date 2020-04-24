package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.ClienteEndereco;

public interface ClienteEnderecoRepository extends BaseRepository<ClienteEndereco> {

	//@formatter:off
	@Query(
			"   from ClienteEndereco clienteEndereco "
			+ "inner join fetch clienteEndereco.cep cep "
			+ " inner join fetch cep.bairro bairro "
			+ " inner join fetch bairro.cidade cidade "
			+ " inner join fetch cidade.estado estado "
			+ " inner join fetch clienteEndereco.enderecoComplemento "
			+ " where clienteEndereco.cliente = :cliente")
	public List<ClienteEndereco> findByCliente(@Param("cliente") Cliente cliente);
	
	
	@Query(
			"   from ClienteEndereco clienteEndereco "
			+ "inner join fetch clienteEndereco.cep cep "
			+ " inner join fetch cep.bairro bairro "
			+ " inner join fetch bairro.cidade cidade "
			+ " inner join fetch cidade.estado estado "
			+ " inner join fetch clienteEndereco.enderecoComplemento "
			+ " where clienteEndereco.id = :id ")
	public Optional<ClienteEndereco> findById(@Param("id") Long id);
	
	//@formatter:on
}