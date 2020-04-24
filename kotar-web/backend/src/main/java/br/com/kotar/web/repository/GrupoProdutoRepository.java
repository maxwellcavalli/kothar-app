package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.GrupoProduto;

public interface GrupoProdutoRepository extends CrudRepository<GrupoProduto>{

	//@formatter:off	
	@Query(value = "select grupoProduto "
			+ "       from GrupoProduto grupoProduto "
			+ "       left join fetch grupoProduto.grupoProdutoParent"
			+ "      where upper(grupoProduto.nome) like upper(:nome) ",
			countQuery="select count(grupoProduto) "
					+ "       from GrupoProduto grupoProduto "
					+ "      where upper(grupoProduto.nome) like upper(:nome) ")
	public Page<GrupoProduto> findByNomeLikeIgnoreCase(@Param("nome") String name, Pageable pageable);

	@Query(value = "select grupoProduto "
			+ "       from GrupoProduto grupoProduto "
			+ "       left join fetch grupoProduto.grupoProdutoParent"
			+ "      where grupoProduto.id = :id ")
	public Optional<GrupoProduto> findById(@Param("id") Long id);
	
	
	@Query(value = "select grupoProduto "
			+ "       from GrupoProduto grupoProduto "
			+ "      where grupoProduto.grupoProdutoParent is null ")
			
	public List<GrupoProduto> findAllParents();
	
	@Query(value = "select grupoProduto "
			+ "       from GrupoProduto grupoProduto "
			+ "      where grupoProduto.grupoProdutoParent = :grupoProdutoParent ")
	public List<GrupoProduto> findAllChidren(@Param("grupoProdutoParent") GrupoProduto grupoProdutoParent);
	
	
	//@formatter:on
	
}