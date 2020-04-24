package br.com.kotar.web.repository.impl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.Cacheable;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.repository.TransformData;
import br.com.kotar.core.repository.impl.BaseCrudRepositoryImpl;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.ProdutoImagem;
import br.com.kotar.domain.business.type.SituacaoProdutoType;
import br.com.kotar.domain.helper.ProdutoFilter;
import br.com.kotar.web.repository.ProdutoRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ProdutoDAOImpl extends BaseCrudRepositoryImpl<Produto> implements ProdutoRepository {

	//@formatter:off
	@Autowired ProdutoRepository produtoRepository;	
	//@formatter:on

	@Override
	public CrudRepository<Produto> getRepository() {
		return produtoRepository;
	}
	
	public List<Produto> findAll(){
		StringBuilder hql = new StringBuilder();
		hql.append(" select new br.com.kotar.domain.business.Produto ( "); 
		hql.append(" 				p.id, p.nome ) ");
		hql.append("   from Produto p ");
		hql.append("  order by p.nome ");
		
		TypedQuery<Produto> query = em.createQuery(hql.toString(), Produto.class);
		return query.getResultList();
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Cacheable("produtos")
	public Page<Produto> findByParams(ProdutoFilter produtoFilter) throws Exception {

		StringBuilder sql_fields = new StringBuilder();
		sql_fields.append(" select p.produto_id as produto_id, ");
		sql_fields.append(" 		p.nome as produto_nome, ");
		sql_fields.append(" 		p.generico as produto_generico, ");
		sql_fields.append(" 		p.situacao_produto as produto_situacao, ");
		sql_fields.append(" 		g.grupo_produto_id as grupo_produto_id, ");
		sql_fields.append(" 		g.nome as grupo_produto_nome, ");
		sql_fields.append(" 		tbl1.produto_imagem_id as produto_imagem_id, ");
		sql_fields.append(" 		tbl1.nome as produto_imagem_nome ");

		StringBuilder sql_common = new StringBuilder();
		sql_common.append("   from produto p ");
		sql_common.append("  inner join grupo_produto g on g.grupo_produto_id = p.grupo_produto_id ");
		sql_common.append("   left join (select pimg.produto_imagem_id, pimg.nome, pimg.produto_id ");
		sql_common.append("                from produto_imagem pimg ");
		sql_common.append("               where pimg.principal = 1 ");
		sql_common.append("                 and pimg.ativo = 1 ) tbl1 on tbl1.produto_id = p.produto_id ");
		sql_common.append("  where 1 = 1 ");

		Map<String, Object> parameters = new HashMap<>();
		if (produtoFilter != null) {
			if (produtoFilter.getNome() != null && !produtoFilter.getNome().trim().isEmpty()) {
				sql_common.append(" and UPPER(p.nome) LIKE :nome ");
				parameters.put("nome", "%" + produtoFilter.getNome().toUpperCase() + "%");
			}
		}

		StringBuilder sql_order = new StringBuilder();
		sql_order.append(" order by p.nome asc ");

		Pageable pageable = produtoFilter.getPageable();

		Page<Produto> paged = searchNativeQuery(sql_fields, sql_common, sql_order, pageable, parameters, Produto.class, new TransformData<Produto>() {

			@Override
			public Produto transform(Object[] row) throws Exception {
				BigInteger produto_id = (BigInteger) row[0];
				String produto_nome = (String) row[1];
				Boolean produto_generico = (Boolean) row[2];
				SituacaoProdutoType produto_situacao = SituacaoProdutoType.get((Integer) row[3]);
				BigInteger grupo_produto_id = (BigInteger) row[4];
				String grupo_produto_nome = (String) row[5];
				BigInteger produto_imagem_id = (BigInteger) row[6];
				String produto_imagem_nome = (String) row[7];

				Produto _p = new Produto();
				_p.setId(produto_id.longValue());
				_p.setNome(produto_nome);
				_p.setGenerico(produto_generico);
				_p.setSituacaoProduto(produto_situacao);

				GrupoProduto _g = new GrupoProduto();
				_g.setId(grupo_produto_id.longValue());
				_g.setNome(grupo_produto_nome);

				_p.setGrupoProduto(_g);

				if (produto_imagem_id != null) {
					ProdutoImagem _pi = new ProdutoImagem();
					_pi.setId(produto_imagem_id.longValue());
					_pi.setNome(produto_imagem_nome);

					_p.setProdutoImagem(_pi);
				}

				return _p;
			}

		});

		return paged;
	}

}
