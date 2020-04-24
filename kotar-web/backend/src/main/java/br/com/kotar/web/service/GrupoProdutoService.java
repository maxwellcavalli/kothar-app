package br.com.kotar.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.component.Messages;
import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.web.repository.impl.GrupoProdutoDAOImpl;

@Service
public class GrupoProdutoService extends BaseCrudService<GrupoProduto> {

	final long DEFAULT = 9l;
	
	//@formatter:off
	//@Autowired GrupoProdutoRepository grupoProdutoRepository;
	@Autowired GrupoProdutoDAOImpl grupoProdutoDAO;
	//@formatter:on

	@Override
	public CrudRepository<GrupoProduto> getRepository() {
		return grupoProdutoDAO;
	}

	@Autowired
	private Messages messages;

	public GrupoProduto saveOrUpdate(GrupoProduto grupoProduto) throws Exception {
		if (grupoProduto.getGrupoProdutoParent() != null) {
			if (grupoProduto.getId() != null && grupoProduto.getId().longValue() > 0) {
				if (grupoProduto.getGrupoProdutoParent().getId().longValue() == grupoProduto.getId().longValue()) {

					throw new Exception(messages.get("grupo.produto.parent"));
				}

				boolean autoRelation = ((GrupoProdutoDAOImpl)getRepository()).validateAutoRelation(grupoProduto, grupoProduto.getGrupoProdutoParent());
				if (autoRelation == false) {
					throw new Exception(messages.get("grupo.produto.auto.relation"));
				}
			}
		}

		grupoProduto = getRepository().save(grupoProduto);
		return grupoProduto;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<GrupoProduto> findGrouped() throws Exception {
		List<GrupoProduto> parents = ((GrupoProdutoDAOImpl)getRepository()).findAllParents();
		List<GrupoProduto> retorno = new ArrayList<>();

		for (GrupoProduto g : parents) {
			GrupoProduto gn = g.clone();
			
			gn.setGrupoProdutoParent(null);
			gn.setChildren(group(gn));
			
			retorno.add(gn);
		}

		return retorno;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	private List<GrupoProduto> group(GrupoProduto g) throws Exception {
		List<GrupoProduto> children = ((GrupoProdutoDAOImpl)getRepository()).findAllChidren(g);
		List<GrupoProduto> ret = new ArrayList<>();

		for (GrupoProduto gc : children) {
			GrupoProduto gcn = gc.clone();
			
			gcn.setGrupoProdutoParent(null);
			gcn.setChildren(group(gcn));
			ret.add(gcn);
		}

		return ret;
	}
	
	public GrupoProduto findDefault(){
		Optional<GrupoProduto> grupoProdutoOptional = findById(DEFAULT);
		if (grupoProdutoOptional.isPresent()){
			return grupoProdutoOptional.get();
		}

		return  null;
	}

}
