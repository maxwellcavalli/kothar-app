package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Fornecedor;

public interface FornecedorRepository extends CrudRepository<Fornecedor> {

	//@formatter:off	
	
//	@Query(value = "select fornecedor "
//			+ "       from Fornecedor fornecedor "
//			+ "      inner join fetch fornecedor.cep cep "
//			+ "      inner join fetch cep.bairro bairro "
//			+ "      inner join fetch bairro.cidade cidade "
//			+ "      inner join fetch cidade.estado estado "
//			+ "       left join fetch fornecedor.enderecoComplemento "			
//			+ "      where upper(fornecedor.nome) like upper(:nome) "
//			+ "         or upper(fornecedor.cpfCnpj) = (:cpfCnpj) ",			
//			countQuery="select count(fornecedor) "
//					+ "       from Fornecedor fornecedor "
//					+ "      inner join fornecedor.cep cep "
//					+ "      inner join cep.bairro bairro "
//					+ "      inner join bairro.cidade cidade "
//					+ "      inner join cidade.estado estado "
//					+ "       left join fornecedor.enderecoComplemento "			
//					+ "      where upper(fornecedor.nome) like upper(:nome) "
//					+ "         or upper(fornecedor.cpfCnpj) = (:cpfCnpj) "
//			)
//	public Page<Fornecedor> findByNomeOrCpfCnpjLikeIgnoreCase(@Param("nome") String nome, @Param("cpfCnpj") String cpfCnpj, Pageable pageable);

	@Query(value = "select fornecedor "
			+ "       from Fornecedor fornecedor "
			+ "      inner join fetch fornecedor.cep cep "
			+ "      inner join fetch cep.bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado estado "
			+ "       left join fetch fornecedor.enderecoComplemento "	
			+ "      where fornecedor.id = :id ")
	public Optional<Fornecedor> findById(@Param("id") Long id);
	
	
	@Query(value = "select fornecedor "
			+ "       from Fornecedor fornecedor "
			+ "      inner join fetch fornecedor.cep cep "
			+ "      inner join fetch cep.bairro bairro "
			+ "      inner join fetch bairro.cidade cidade "
			+ "      inner join fetch cidade.estado estado "
			+ "       left join fetch fornecedor.enderecoComplemento "	
			+ "      where exists (select 1 "
			+ "                      from ClienteFornecedor clienteFornecedor "
			+ "                     where clienteFornecedor.fornecedor = fornecedor "
			+ "                       and clienteFornecedor.cliente = :cliente ) "
			
			)
	public List<Fornecedor> findByCliente(@Param("cliente") Cliente cliente);
	
	@Query(value = "select fornecedor "
			+ "       from Fornecedor fornecedor "
			+ "      where fornecedor.uuid = :uuid ")
	public Fornecedor findByUuid(@Param("uuid") String uuid);
	//@formatter:on

}