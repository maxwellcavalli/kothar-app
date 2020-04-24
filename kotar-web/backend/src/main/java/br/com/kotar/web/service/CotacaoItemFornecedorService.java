package br.com.kotar.web.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemArquivo;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.web.repository.CotacaoItemFornecedorRepository;

@Service
public class CotacaoItemFornecedorService extends BaseService<CotacaoItemFornecedor> {

	//@formatter:off
	@Autowired CotacaoItemFornecedorRepository cotacaoItemFornecedorRepository;
	@Autowired CotacaoItemFornecedorValorService cotacaoItemFornecedorValorService;
	@Autowired CotacaoItemArquivoService cotacaoItemArquivoService;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	//@formatter:on

	@Override
	public BaseRepository<CotacaoItemFornecedor> getRepository() {
		return cotacaoItemFornecedorRepository;
	}

	public void saveOrUpdate(CotacaoItem cotacaoItem, CotacaoFornecedor cotacaoFornecedor) throws Exception {
		CotacaoItemFornecedor cotacaoItemFornecedor = new CotacaoItemFornecedor();
		cotacaoItemFornecedor.setCotacaoItem(cotacaoItem);
		cotacaoItemFornecedor.setCotacaoFornecedor(cotacaoFornecedor);
		cotacaoItemFornecedor.setValores(null);

		cotacaoItemFornecedorRepository.save(cotacaoItemFornecedor);
	}

	public List<CotacaoItemFornecedor> findByCotacaoAndCotacaoFornecedor(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) {
		List<CotacaoItemFornecedor> list = cotacaoItemFornecedorRepository.findByCotacaoAndCotacaoFornecedor(cotacao, cotacaoFornecedor);
		for (CotacaoItemFornecedor cotacaoItemFornecedor : list) {
			List<CotacaoItemFornecedorValor> valores = cotacaoItemFornecedorValorService.findByCotacaoItemFornecedor(cotacaoItemFornecedor);
			cotacaoItemFornecedor.setValores(valores);

			CotacaoItem cotacaoItem = cotacaoItemFornecedor.getCotacaoItem();
			if (!valores.isEmpty()) {
				Produto produto = cotacaoItem.getProduto();

				if (!produto.isGenerico()) {
					cotacaoItemFornecedor.setValorUnitario(valores.get(0).getUnitario());
				}
			}

			List<CotacaoItemArquivo> arquivos = cotacaoItemArquivoService.findByCotacaoItem(cotacaoItem);
			cotacaoItem.setArquivos(arquivos);
		}

		return list;
	}

	public void archiveResposta(List<CotacaoItemFornecedor> forncedorItens, CotacaoFornecedor cotacaoFornecedor) throws Exception {

		cotacaoFornecedor = cotacaoFornecedorService.archiveResposta(cotacaoFornecedor);

		for (CotacaoItemFornecedor cotacaoItemFornecedor : forncedorItens) {
			List<CotacaoItemFornecedorValor> valores = cotacaoItemFornecedor.getValores();
			BigDecimal valorUnitario = cotacaoItemFornecedor.getValorUnitario();

			cotacaoItemFornecedor.setCotacaoFornecedor(cotacaoFornecedor);
			cotacaoItemFornecedor = cotacaoItemFornecedorRepository.save(cotacaoItemFornecedor);
			cotacaoItemFornecedor.setValorUnitario(valorUnitario);

			if (!cotacaoItemFornecedor.isIgnorarItem()) {
				cotacaoItemFornecedorValorService.saveOrUpdate(cotacaoItemFornecedor, valores);
			} else {
				cotacaoItemFornecedorValorService.delete(cotacaoItemFornecedor);
			}
		}
	}

	public CotacaoItemFornecedor findByCotacaoAndCotacaoItemFornecedorWithValues(CotacaoItem cotacaoItem, CotacaoFornecedor cotacaoFornecedor) {
		CotacaoItemFornecedor cotacaoItemFornecedor = cotacaoItemFornecedorRepository.findByCotacaoAndCotacaoItemFornecedorWithValues(cotacaoItem,
				cotacaoFornecedor);
		List<CotacaoItemFornecedorValor> valores = cotacaoItemFornecedorValorService.findByCotacaoItemFornecedor(cotacaoItemFornecedor);
		cotacaoItemFornecedor.setValores(valores);

		return cotacaoItemFornecedor;
	}
	
	public List<CotacaoItemFornecedor> findByCotacaoFornecedor(@Param("cotacaoFornecedor") CotacaoFornecedor cotacaoFornecedor) {
		List<CotacaoItemFornecedor> list = cotacaoItemFornecedorRepository.findByCotacaoFornecedor(cotacaoFornecedor);
		list.forEach(el -> {
			List<CotacaoItemFornecedorValor> valores = cotacaoItemFornecedorValorService.findByCotacaoItemFornecedor(el);
			el.setValores(valores);
		});
		
		return list;
	}
	
	
}
