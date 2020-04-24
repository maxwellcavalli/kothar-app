package br.com.kotar.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorGrupoProduto;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.web.repository.FornecedorGrupoProdutoRepository;

@Service
public class FornecedorGrupoProdutoService extends BaseService<FornecedorGrupoProduto>{

	//@formatter:off
	@Autowired FornecedorGrupoProdutoRepository fornecedorGrupoProdutoRepository;
	//@formatter:on
	
	@Override
	public BaseRepository<FornecedorGrupoProduto> getRepository() {
		return fornecedorGrupoProdutoRepository;
	}

	public void saveOrUpate(Fornecedor fornecedor, List<GrupoProduto> grupos) throws Exception {
		List<FornecedorGrupoProduto> listFornecedorGrupo = fornecedorGrupoProdutoRepository.findByFornecedor(fornecedor);
		Map<Long, Long> map = new HashMap<>();

		for (FornecedorGrupoProduto fornecedorGrupoProduto : listFornecedorGrupo) {
			boolean exists = false;
			for (GrupoProduto grupoProduto : grupos) {

				if (fornecedorGrupoProduto.getGrupoProduto().getId().longValue() == grupoProduto.getId().longValue()) {
					exists = true;
					map.put(grupoProduto.getId().longValue(), grupoProduto.getId().longValue());
					break;
				}
			}

			if (!exists) {
				fornecedorGrupoProdutoRepository.delete(fornecedorGrupoProduto);
			}
		}

		for (GrupoProduto grupoProduto : grupos) {
			if (!map.containsKey(grupoProduto.getId().longValue())) {
				FornecedorGrupoProduto fornecedorGrupoProduto = new FornecedorGrupoProduto();
				fornecedorGrupoProduto.setFornecedor(fornecedor);
				fornecedorGrupoProduto.setGrupoProduto(grupoProduto);
				fornecedorGrupoProdutoRepository.save(fornecedorGrupoProduto);
			}
		}
	}

	public List<FornecedorGrupoProduto> findByFornecedor(Fornecedor fornecedor) throws Exception {
		return fornecedorGrupoProdutoRepository.findByFornecedor(fornecedor);
	}

	public void delete(Fornecedor fornecedor) throws Exception {
		List<FornecedorGrupoProduto> list = fornecedorGrupoProdutoRepository.findByFornecedor(fornecedor);
		fornecedorGrupoProdutoRepository.deleteAll(list);
	}


}
