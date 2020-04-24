package br.com.kotar.web.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.ProdutoImagem;

public interface ProdutoImagemRepository extends BaseRepository<ProdutoImagem>{

	//@formatter:off
	@Query(value = "select new br.com.kotar.domain.business.ProdutoImagem( "
			+ "				produtoImagem.id, produtoImagem.nome, produtoImagem.principal ) "
			+ "       from ProdutoImagem produtoImagem "
			+ "      where produtoImagem.produto = :produto "
			+ "        and produtoImagem.ativo = 1 ")
	public List<ProdutoImagem> findByProduto(@Param("produto") Produto produto);

	@Query(value = "select new br.com.kotar.domain.business.ProdutoImagem( "
			+ "				produtoImagem.id, produtoImagem.nome, produtoImagem.principal ) "
			+ "       from ProdutoImagem produtoImagem "
			+ "      where produtoImagem.produto = :produto ")
	public List<ProdutoImagem> findAllByProduto(@Param("produto") Produto produto);
	
	@Query(value = "select produtoImagem "
			+ "       from ProdutoImagem produtoImagem "
			+ "      where produtoImagem.id = :id")
	public Optional<ProdutoImagem> findById(@Param("id") Long id);

	@Query(value = "select new br.com.kotar.domain.business.ProdutoImagem( "
			+ "				produtoImagem.id, produtoImagem.nome, produtoImagem.thumb, produtoImagem.produto, produtoImagem.principal) "
			+ "       from ProdutoImagem produtoImagem "
			+ "      where produtoImagem.id = :id")
	public ProdutoImagem findThumbsById(@Param("id") Long id);
	//@formatter:on
	
}