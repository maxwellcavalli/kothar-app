package br.com.kotar.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorProduto;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.web.repository.FornecedorProdutoRepository;

@Service
public class FornecedorProdutoService extends BaseService<FornecedorProduto> {

	//@formatter:off
	@Autowired FornecedorProdutoRepository fornecedorProdutoRepository;
	//@formatter:on

	@Override
	public BaseRepository<FornecedorProduto> getRepository() {
		return fornecedorProdutoRepository;
	}

	public FornecedorProduto findByFornecedorAndProduto(Fornecedor fornecedor, Produto produto) {
		return ((FornecedorProdutoRepository) getRepository()).findByFornecedorAndProduto(fornecedor, produto);
	}

	public FornecedorProduto findByFornecedorAndIdentificacao(Fornecedor fornecedor, String identificacao) {
		return ((FornecedorProdutoRepository) getRepository()).findByFornecedorAndIdentificacao(fornecedor, identificacao);
	}

	public FornecedorProduto persistir(Fornecedor fornecedor, Produto produto, String identificacaoProduto) throws Exception {
		FornecedorProduto _f = findByFornecedorAndProduto(fornecedor, produto);
		if (_f == null) {
			_f = new FornecedorProduto();
			_f.setFornecedor(fornecedor);
			_f.setProduto(produto);
		}

		if (identificacaoProduto != null && !identificacaoProduto.trim().isEmpty()) {
			_f.setIdentificacao(identificacaoProduto);
		}
		_f = saveOrUpdate(_f);

		return _f;
	}

}
