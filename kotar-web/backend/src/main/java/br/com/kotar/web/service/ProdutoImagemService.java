package br.com.kotar.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.core.util.ImageUtil;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.ProdutoImagem;
import br.com.kotar.web.repository.ProdutoImagemRepository;

@Service
public class ProdutoImagemService extends BaseService<ProdutoImagem> {

	//@formatter:off
	@Autowired ProdutoImagemRepository produtoImagemRepository;
	//@formatter:on

	@Override
	public BaseRepository<ProdutoImagem> getRepository() {
		return produtoImagemRepository;
	}
	
	public List<ProdutoImagem> findByProduto(Produto produto){
		return produtoImagemRepository.findByProduto(produto);
	}
	
	public List<ProdutoImagem> findAllByProduto(Produto produto){
		return produtoImagemRepository.findAllByProduto(produto);
	}
	
	public Optional<ProdutoImagem> findById(Long id){
		return produtoImagemRepository.findById(id);
	}

	public ProdutoImagem findThumbsById(Long id){
		return produtoImagemRepository.findThumbsById(id);
	}
	
	public void saveOrUpdate(Produto produto, List<ProdutoImagem> arquivos) throws Exception {
		if (arquivos == null){
			arquivos = new ArrayList<>();
		}
		
		if (produto.isGenerico()){
			arquivos.clear();
		}
		
		List<ProdutoImagem> listDB = findAllByProduto(produto);
		
		for (ProdutoImagem produtoImagemDB : listDB){
			boolean exists = false;
			
			for (ProdutoImagem produtoImagem : arquivos){
				
				if (produtoImagem.getId() == null || produtoImagem.getId().longValue() == 0){
					continue;
				}
				
				if (produtoImagem.getId().longValue() == produtoImagemDB.getId().longValue()){
					exists = true;
					break;
				}				
			}
			
			if (!exists){
				produtoImagemRepository.delete(produtoImagemDB);
			}
		}
		
		boolean temPrincipal = false;
		for (ProdutoImagem produtoImagem : arquivos){			
			if (produtoImagem.isPrincipal()) {
				temPrincipal = true;
				break;
			}			
		}
		
		if (!temPrincipal) {
			if (arquivos != null && arquivos.size() > 0) {
				arquivos.get(0).setPrincipal(true);				
			}
		} 
		
		for (ProdutoImagem produtoImagem : arquivos){
			if (produtoImagem.getArquivo() == null || produtoImagem.getArquivo().length == 0){
				continue;
			}
			
			if (produtoImagem.isPrincipal()) {
				byte[] thumb = ImageUtil.gerarThumb(produtoImagem.getArquivo(), 70, 70);
				produtoImagem.setThumb(thumb);
			}
			
			produtoImagem.setProduto(produto);
			produtoImagemRepository.save(produtoImagem);			
		}
	}
	
	public void delete(Produto produto) throws Exception {
		List<ProdutoImagem> listDB = findAllByProduto(produto);
		produtoImagemRepository.deleteAll(listDB);
	}
	
	public ProdutoImagem createThumbIfNullAndPrincipal(ProdutoImagem produtoImagem) {
		
		Produto produto = produtoImagem.getProduto();
		Optional<ProdutoImagem> produtoImagemOptional = findById(produtoImagem.getId());
		if (!produtoImagemOptional.isPresent()){
			throw new RecordNotFound();
		}

		produtoImagem = produtoImagemOptional.get();
		
		if (produtoImagem.isPrincipal() && produtoImagem.getThumb() == null) {
			byte[] thumb = ImageUtil.gerarThumb(produtoImagem.getArquivo(), 70, 70);
			produtoImagem.setThumb(thumb);
			
			produtoImagem.setProduto(produto);
			produtoImagem = produtoImagemRepository.save(produtoImagem);
		}
		
		return produtoImagem;
	}
}
