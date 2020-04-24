package br.com.kotar.web.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.kotar.core.helper.SimilarityObject;
import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.service.BaseCrudService;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorProduto;
import br.com.kotar.domain.business.LogSituacaoProduto;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.ProdutoImagem;
import br.com.kotar.domain.business.type.SituacaoProdutoType;
import br.com.kotar.domain.business.type.TipoPendenciaIntegracaoType;
import br.com.kotar.domain.helper.ProdutoFilter;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.ProdutoRepository;
import br.com.kotar.web.repository.impl.ProdutoDAOImpl;
import info.debatty.java.stringsimilarity.JaroWinkler;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

@Service
public class ProdutoService extends BaseCrudService<Produto> {

    //@formatter:off
    @Autowired
    ProdutoRepository produtoRepository;
    @Autowired
    ProdutoDAOImpl produtoDAO;

    @Autowired
    ProdutoImagemService produtoImagemService;
    @Autowired
    PendenciaIntegracaoService pendenciaIntegracaoProdutoService;
    @Autowired
    GrupoProdutoService grupoProdutoService;
    @Autowired
    FornecedorProdutoService fornecedorProdutoService;
    @Autowired
    FornecedorProdutoPrecoService fornecedorProdutoPrecoService;

    //@formatter:on

    @Override
    public CrudRepository<Produto> getRepository() {
        return produtoDAO;
    }

    @Override
    public Optional<Produto> findById(Long id) {
        Optional<Produto> produtoOptional = super.findById(id);
        if (!produtoOptional.isPresent()) {
            return Optional.empty();
        }

        Produto p = produtoOptional.get();
        List<ProdutoImagem> arquivos = produtoImagemService.findByProduto(p);
        p.setImagens(arquivos);

        return Optional.of(p);
    }

    public Produto findCompleteById(Long id) {
        Optional<Produto> produtoOptional = super.findById(id);
        if (produtoOptional.isPresent()) {
            Produto p = produtoOptional.get();
            List<ProdutoImagem> arquivos = produtoImagemService.findAllByProduto(p);
            p.setImagens(arquivos);

            return p;
        }

        return null;
    }

    public Produto saveOrUpdate(Produto produto, Usuario usuario) throws Exception {

        List<ProdutoImagem> arquivos = produto.getImagens();

        if (produto.getLogSituacao() == null) {
            produto.setLogSituacao(new HashSet<>());
        }

        produto.setSituacaoProduto(SituacaoProdutoType.ANALISE);

        LogSituacaoProduto logSituacaoProduto = new LogSituacaoProduto();
        logSituacaoProduto.setProduto(produto);
        logSituacaoProduto.setUsuario(usuario);

        produto.getLogSituacao().add(logSituacaoProduto);
        produto = produtoRepository.save(produto);

        produtoImagemService.saveOrUpdate(produto, arquivos);

        return produto;
    }

    public Page<Produto> findByParams(ProdutoFilter produtoFilter) throws Exception {
        Page<Produto> paged = ((ProdutoDAOImpl) getRepository()).findByParams(produtoFilter);

        return paged;
    }

    public boolean persistirProdutoSoap(Produto produto, Fornecedor fornecedor, Double precoUnitario, Usuario usuario, String identificacaoProduto)
            throws Exception {

        List<SimilarityObject<Produto>> _similarities = findSimilarity(produto.getNome(), .9999);
        if (_similarities.size() != 1) {
            pendenciaIntegracaoProdutoService.criarPendencia(produto, fornecedor, precoUnitario, usuario, TipoPendenciaIntegracaoType.NOVO_PRODUTO);
            return true;
        } else {
            Produto _p = null;

            SimilarityObject<Produto> _so = _similarities.get(0);
            _p = _so.getData();

            Optional<Produto> produtoOptional = findById(_p.getId());

            if (!produtoOptional.isPresent()){
                throw new RecordNotFound();
            }

            final Produto _tmp = produtoOptional.get();
            List<ProdutoImagem> _li = new ArrayList<>();
            _tmp.getImagens().forEach(el -> {
                Optional<ProdutoImagem> produtoImagemOptional = produtoImagemService.findById(el.getId());

                if (!produtoImagemOptional.isPresent()){
                    throw new RecordNotFound();
                }

                ProdutoImagem _pi = produtoImagemOptional.get();
                _li.add(_pi);
            });

            _tmp.setImagens(_li);

            _p = _tmp;

            if (produto.getImagens() != null && produto.getImagens().size() > 0) {
                // verificar se as imagens sao diferentes

                int _i = 0;
                while (_i < produto.getImagens().size()) {
                    ProdutoImagem el = produto.getImagens().get(_i);

                    String _n = el.getNome();
                    int _s = el.getArquivo().length;

                    long _q = _tmp.getImagens().stream().filter(elf -> elf.getNome().equals(_n) && elf.getArquivo().length == _s).count();

                    if (_q > 0) {
                        produto.getImagens().remove(_i);
                        _i = -1;
                    }
                    _i++;
                }

                // existem novas imagens, atualizar o cadastro de produtos
                if (produto.getImagens().size() > 0) {
                    _p.getImagens().addAll(produto.getImagens());
                    produto.setNome(_p.getNome());
                    produto.setId(_p.getId());

                    pendenciaIntegracaoProdutoService.criarPendencia(produto, fornecedor, precoUnitario, usuario,
                            TipoPendenciaIntegracaoType.ADICIONAR_FOTOS);
                    _p = saveOrUpdate(_p, usuario);
                }
            }

            FornecedorProduto _fp = fornecedorProdutoService.persistir(fornecedor, _p, identificacaoProduto);
            if (precoUnitario != null && precoUnitario.doubleValue() > 0) {
                fornecedorProdutoPrecoService.persistir(_fp, precoUnitario);
            }

            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<SimilarityObject<Produto>> findSimilarity(String name, double minDistance) {
        List<SimilarityObject<Produto>> _similarities = new ArrayList<>();

        Cache cache = cacheManager.getCache("produtoCache");
        Element element = cache.get("all");

        List<Produto> list = null;
        if (element == null) {
            list = ((ProdutoDAOImpl) getRepository()).findAll();

            element = new Element("all", list);
            cache.put(element);
        } else {
            list = (List<Produto>) element.getObjectValue();
        }

        JaroWinkler jw = new JaroWinkler();
        list.forEach(el -> {

            String s1 = name.toLowerCase();
            String s2 = el.getNome().toLowerCase();

            s1 = s1.trim();
            s2 = s2.trim();

            Double distance = jw.similarity(s1, s2);

            if (distance > minDistance) {
                SimilarityObject<Produto> _s = new SimilarityObject<Produto>();
                _s.setData(el);
                _s.setDistance(distance);

                _similarities.add(_s);
            }
        });

        return _similarities.stream().sorted((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()) * -1).collect(Collectors.toList());
    }

    @Override
    public void delete(Produto entity) throws Exception {
        produtoImagemService.delete(entity);
        super.delete(entity);
    }

}
