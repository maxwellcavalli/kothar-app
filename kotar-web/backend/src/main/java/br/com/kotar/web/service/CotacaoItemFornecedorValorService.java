package br.com.kotar.web.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.web.repository.CotacaoItemFornecedorValorRepository;

@Service
public class CotacaoItemFornecedorValorService extends BaseService<CotacaoItemFornecedorValor> {

	//@formatter:off
	@Autowired CotacaoItemFornecedorValorRepository cotacaoItemFornecedorValorRepository;
	//@formatter:on

	@Override
	public BaseRepository<CotacaoItemFornecedorValor> getRepository() {
		return cotacaoItemFornecedorValorRepository;
	}

	public List<CotacaoItemFornecedorValor> findByCotacaoItemFornecedor(CotacaoItemFornecedor cotacaoItemFornecedor) {
		return cotacaoItemFornecedorValorRepository.findByCotacaoItemFornecedor(cotacaoItemFornecedor);
		// return null;
	}

	public void saveOrUpdate(CotacaoItemFornecedor cotacaoItemFornecedor, List<CotacaoItemFornecedorValor> valores) {
		CotacaoItem cotacaoItem = cotacaoItemFornecedor.getCotacaoItem();
		Produto produto = cotacaoItem.getProduto();
		if (!produto.isGenerico()) {
			if (valores == null) {
				valores = new ArrayList<CotacaoItemFornecedorValor>();
			}

			if (valores.isEmpty()) {
				CotacaoItemFornecedorValor cotacaoItemFornecedorValor = new CotacaoItemFornecedorValor();
				cotacaoItemFornecedorValor.setMarcaModelo(null);
				cotacaoItemFornecedorValor.setUnitario(cotacaoItemFornecedor.getValorUnitario());

				valores.add(cotacaoItemFornecedorValor);
			} else {
				CotacaoItemFornecedorValor cotacaoItemFornecedorValor = valores.get(0);
				cotacaoItemFornecedorValor.setMarcaModelo(null);
				cotacaoItemFornecedorValor.setUnitario(cotacaoItemFornecedor.getValorUnitario());
				valores.set(0, cotacaoItemFornecedorValor);
			}
		}

		List<CotacaoItemFornecedorValor> listDB = findByCotacaoItemFornecedor(cotacaoItemFornecedor);
		for (CotacaoItemFornecedorValor cotacaoItemFornecedorValorDB : listDB) {
			boolean exists = false;

			for (CotacaoItemFornecedorValor cotacaoItemFornecedorValor : valores) {
				if (cotacaoItemFornecedorValor.getId() == null || cotacaoItemFornecedorValor.getId().longValue() == 0) {
					continue;
				}

				if (cotacaoItemFornecedorValor.getId().longValue() == cotacaoItemFornecedorValorDB.getId().longValue()) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				cotacaoItemFornecedorValorRepository.delete(cotacaoItemFornecedorValorDB);
			}
		}

		for (CotacaoItemFornecedorValor cotacaoItemFornecedorValor : valores) {
			if (produto.isGenerico()) {
				if (cotacaoItemFornecedorValor.getMarcaModelo() == null || cotacaoItemFornecedorValor.getMarcaModelo().isEmpty()
						|| cotacaoItemFornecedorValor.getUnitario() == null || cotacaoItemFornecedorValor.getUnitario().doubleValue() <= 0) {
					continue;
				}
			}

			cotacaoItemFornecedorValor.setCotacaoItemFornecedor(cotacaoItemFornecedor);
			cotacaoItemFornecedorValor.setDataAtualizacao(Calendar.getInstance().getTime());

			cotacaoItemFornecedorValorRepository.save(cotacaoItemFornecedorValor);
		}
	}

	public void delete(CotacaoItemFornecedor cotacaoItemFornecedor) throws Exception {
		List<CotacaoItemFornecedorValor> listDB = findByCotacaoItemFornecedor(cotacaoItemFornecedor);
		cotacaoItemFornecedorValorRepository.deleteAll(listDB);
	}

	public List<CotacaoItemFornecedorValor> findByCotacaoFornecedor(CotacaoFornecedor cotacaoFornecedor) {
		return cotacaoItemFornecedorValorRepository.findByCotacaoFornecedor(cotacaoFornecedor);
	}

}
