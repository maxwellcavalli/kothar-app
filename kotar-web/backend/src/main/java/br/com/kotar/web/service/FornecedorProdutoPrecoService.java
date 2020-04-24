package br.com.kotar.web.service;

import java.math.BigDecimal;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.FornecedorProduto;
import br.com.kotar.domain.business.FornecedorProdutoPreco;
import br.com.kotar.web.repository.FornecedorProdutoPrecoRepository;

@Service
public class FornecedorProdutoPrecoService extends BaseService<FornecedorProdutoPreco> {

	//@formatter:off
	@Autowired FornecedorProdutoPrecoRepository fornecedorProdutoPrecoRepository;
	//@formatter:on

	@Override
	public BaseRepository<FornecedorProdutoPreco> getRepository() {
		return fornecedorProdutoPrecoRepository;
	}

	public FornecedorProdutoPreco findLastUpdate(FornecedorProduto fornecedorProduto) {
		return ((FornecedorProdutoPrecoRepository) getRepository()).findLastUpdate(fornecedorProduto);
	}

	public void persistir(FornecedorProduto fornecedorProduto, Double preco) throws Exception {
		FornecedorProdutoPreco _fpp = findLastUpdate(fornecedorProduto);
		if (_fpp != null) {
			if (_fpp.getUnitario().doubleValue() != preco) {
				_fpp = null;
			}
		}

		if (_fpp == null) {
			_fpp = new FornecedorProdutoPreco();
			_fpp.setFornecedorProduto(fornecedorProduto);
			_fpp.setDataAtualizacao(Calendar.getInstance().getTime());
			_fpp.setUnitario(new BigDecimal(preco));

			_fpp = saveOrUpdate(_fpp);
		}
	}
}
