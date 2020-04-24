package br.com.kotar.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.domain.business.Produto;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface ProdutoRepository extends CrudRepository<Produto> {

	//@formatter:off
	@Query(value = "select produto "
			+ "       from Produto produto "
			+ "      inner join fetch produto.grupoProduto "
			//+ "      left join fetch produto.categorias "
			+ "      where upper(produto.nome) like upper(:nome) ",
			countQuery="select count(produto) "
					+ "       from Produto produto "
					+ "      where upper(produto.nome) like upper(:nome) ")
	public Page<Produto> findByNomeLikeIgnoreCase(@Param("nome") String name, Pageable pageable);

	@Query(value = "select produto "
			+ "       from Produto produto "
			+ "      inner join fetch produto.grupoProduto "
			//+ "      left join fetch produto.categorias "
			+ "      where produto.id = :id ")
	public Optional<Produto> findById(@Param("id") Long id);
	
	
	

	//@formatter:on

}