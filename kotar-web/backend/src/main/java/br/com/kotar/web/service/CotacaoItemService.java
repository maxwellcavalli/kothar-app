package br.com.kotar.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemArquivo;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.ProdutoImagem;
import br.com.kotar.web.repository.CotacaoItemRepository;

@Service
public class CotacaoItemService extends BaseService<CotacaoItem> {

	//@formatter:off
	@Autowired CotacaoItemRepository cotacaoItemRepository;
	@Autowired ProdutoService produtoService;
	@Autowired FornecedorService fornecedorService;
	@Autowired CotacaoItemFornecedorService cotacaoItemFornecedorService;
	@Autowired CotacaoItemArquivoService cotacaoItemArquivoService;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	@Autowired ProdutoImagemService produtoImagemService;
	//@formatter:on

	@Override
	public BaseRepository<CotacaoItem> getRepository() {
		return cotacaoItemRepository;
	}

	public List<CotacaoItem> findByCotacaoResposta(Cotacao cotacao, Fornecedor fornecedor) {
		return cotacaoItemRepository.findByCotacaoResposta(cotacao, fornecedor);
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<CotacaoItem> findByCotacao(Cotacao cotacao) {
		return findByCotacao(cotacao, true);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<CotacaoItem> findByCotacao(Cotacao cotacao, boolean loadArquivos) {
		List<CotacaoItem> list = cotacaoItemRepository.findByCotacao(cotacao);
		if (loadArquivos){
			for (CotacaoItem cotacaoItem : list) {
				
				Produto produto = cotacaoItem.getProduto();
				
				if (!cotacaoItem.getProduto().isGenerico()) {
					List<ProdutoImagem> arquivos = produtoImagemService.findByProduto(produto);
					produto.setImagens(arquivos);
					
					for (ProdutoImagem pi : arquivos) {
						if (pi.isPrincipal()) {
							produto.setProdutoImagem(pi);
						}
					}
				}
				
				List<CotacaoItemArquivo> arquivos = cotacaoItemArquivoService.findByCotacaoItem(cotacaoItem);
				cotacaoItem.setArquivos(arquivos);
			}
		}

		return list;
	}

	public void saveOrUpdate(Cotacao cotacao, List<CotacaoItem> itens) throws Exception {

		List<CotacaoItem> listDB = findByCotacao(cotacao);
		for (CotacaoItem cotacaoItemDB : listDB) {

			boolean exists = false;
			for (CotacaoItem cotacaoItem : itens) {
				if (cotacaoItem.getId() == null || cotacaoItem.getId().longValue() == 0) {
					continue;
				}

				if (cotacaoItem.getId().longValue() == cotacaoItemDB.getId().longValue()) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				cotacaoItemArquivoService.delete(cotacaoItemDB);
				cotacaoItemRepository.delete(cotacaoItemDB);
			}
		}

		for (CotacaoItem cotacaoItem : itens) {
			cotacaoItem.setCotacao(cotacao);
			List<CotacaoItemArquivo> arquivos = cotacaoItem.getArquivos();

			cotacaoItem = cotacaoItemRepository.save(cotacaoItem);

			if (arquivos != null) {
				cotacaoItemArquivoService.saveOrUpdate(cotacaoItem, arquivos);
			}
		}
	}

	public void enviarItensCotacao(Cotacao cotacao, List<CotacaoItem> itens) throws Exception {

		// validar se os itens genericos possuem uma descricao detalhada
		for (CotacaoItem cotacaoItem : itens) {
			Produto produto = cotacaoItem.getProduto();
			Optional<Produto> produtoOptional = produtoService.findById(produto.getId());

			if (!produtoOptional.isPresent()){
				throw new RecordNotFound();
			}

			produto = produtoOptional.get();

			if (produto.isGenerico()) {

				if (cotacaoItem.getObservacao() == null || cotacaoItem.getObservacao().trim().isEmpty()
						|| cotacaoItem.getObservacao().trim().length() < 5) {

					throw new Exception(messages.get("cotacao.item.generico.invalido"));
				}
			}
		}

		Map<Fornecedor, CotacaoFornecedor> ctrlForn = new HashMap<>();
		for (CotacaoItem item : itens) {
			GrupoProduto grupoProduto = item.getProduto().getGrupoProduto();
			// pesquisar os fornecedores que atendem os grupos
			List<Fornecedor> fornecedores = fornecedorService.findByGrupoProduto(grupoProduto);

			for (Fornecedor fornecedor : fornecedores) {
				CotacaoFornecedor cotacaoFornecedor = null;
				if (ctrlForn.containsKey(fornecedor)) {
					cotacaoFornecedor = ctrlForn.get(fornecedor);
				} else {
					cotacaoFornecedor = new CotacaoFornecedor();
					cotacaoFornecedor.setCotacao(cotacao);
					cotacaoFornecedor.setFornecedor(fornecedor);

					cotacaoFornecedor = cotacaoFornecedorService.save(cotacaoFornecedor);

					ctrlForn.put(fornecedor, cotacaoFornecedor);
				}

				cotacaoItemFornecedorService.saveOrUpdate(item, cotacaoFornecedor);
			}
		}
	}

	public void delete(Cotacao cotacao) throws Exception {
		List<CotacaoItem> itens = findByCotacao(cotacao);
		for (CotacaoItem cotacaoItem : itens){
			cotacaoItemArquivoService.delete(cotacaoItem);
		}
		
		super.delete(itens);
	}
	
	public void copiarItensCotacao(Cotacao cotacaoOrigem, Cotacao cotacaoDestino) throws Exception {
		List<CotacaoItem> list = findByCotacao(cotacaoOrigem);
		for (CotacaoItem cotacaoItem : list){
			CotacaoItem _new = cotacaoItem.clone();
			_new.setId(null);
			_new.setCotacao(cotacaoDestino);
			
			_new = save(_new);
			
			cotacaoItemArquivoService.copiarArquivos(cotacaoItem, _new);
		}
	}
	
	

}
