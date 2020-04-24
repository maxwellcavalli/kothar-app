package br.com.kotar.web.repository.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.repository.impl.BaseCrudRepositoryImpl;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.web.repository.GrupoProdutoRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class GrupoProdutoDAOImpl extends BaseCrudRepositoryImpl<GrupoProduto> implements GrupoProdutoRepository {

	//@formatter:off
	@Autowired GrupoProdutoRepository grupoProdutoRepository;
	//@formatter:on

	@Override
	public CrudRepository<GrupoProduto> getRepository() {
		return grupoProdutoRepository;
	}

	/**
	 * validar se o grupo parent informado nao ira se auto relacionar com o
	 * grupo informado
	 * 
	 * @param grupoProduto
	 * @param parent
	 */
	public boolean validateAutoRelation(GrupoProduto grupoProduto, GrupoProduto parent) {
		StringBuilder hql = new StringBuilder();
		hql.append(" select grupoProduto ");
		hql.append("   from GrupoProduto grupoProduto ");
		hql.append("  inner join fetch grupoProduto.grupoProdutoParent ");
		hql.append("  where grupoProduto = :grupoProdutoParent ");

		TypedQuery<GrupoProduto> query = em.createQuery(hql.toString(), GrupoProduto.class);
		query.setParameter("grupoProdutoParent", parent);
		List<GrupoProduto> list = query.getResultList();

		Boolean retorno = true;
		for (GrupoProduto grupoProduto2 : list) {
			if (grupoProduto2.getGrupoProdutoParent().getId().longValue() == grupoProduto.getId().longValue()) {
				retorno = false;
				return retorno;
			} else {
				return validateAutoRelation(grupoProduto, grupoProduto2.getGrupoProdutoParent());
			}
		}

		return retorno;
	}

	@Override
	public List<GrupoProduto> findAllParents() {
		return grupoProdutoRepository.findAllParents();
	}

	@Override
	public List<GrupoProduto> findAllChidren(GrupoProduto grupoProdutoParent) {
		return grupoProdutoRepository.findAllChidren(grupoProdutoParent);
	}

}
