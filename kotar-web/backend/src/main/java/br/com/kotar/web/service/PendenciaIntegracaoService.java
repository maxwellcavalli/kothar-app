package br.com.kotar.web.service;

import java.util.Calendar;
import java.util.Optional;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.FornecedorProduto;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.domain.business.PendenciaIntegracao;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.type.SituacaoProdutoType;
import br.com.kotar.domain.business.type.TipoPendenciaIntegracaoType;
import br.com.kotar.domain.helper.PendenciaIntegracaoFilter;
import br.com.kotar.domain.helper.PendenciaIntegracaoHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.impl.PendenciaIntegracaoDAOImpl;

@Service
public class PendenciaIntegracaoService extends BaseService<PendenciaIntegracao> {

    //@formatter:off
    @Autowired
    PendenciaIntegracaoDAOImpl pendenciaIntegracaoProdutoDAO;
    @Autowired
    GrupoProdutoService grupoProdutoService;
    @Autowired
    ProdutoService produtoService;
    @Autowired
    FornecedorProdutoService fornecedorProdutoService;
    @Autowired
    FornecedorProdutoPrecoService fornecedorProdutoPrecoService;
    //@formatter:on

    @Override
    public BaseRepository<PendenciaIntegracao> getRepository() {
        return pendenciaIntegracaoProdutoDAO;
    }

    public void criarPendencia(Produto produto, Fornecedor fornecedor, Double precoUnitario, Usuario usuario,
                               TipoPendenciaIntegracaoType tipoPendenciaIntegracao) throws Exception {

        PendenciaIntegracaoHelper _pi = new PendenciaIntegracaoHelper();

        Produto _p = new Produto();
        _p.setId(produto.getId());
        _p.setNome(produto.getNome());
        _p.setDetalhamento(produto.getDetalhamento());
        _p.setImagens(produto.getImagens());

        Fornecedor _f = new Fornecedor();
        _f.setId(fornecedor.getId());
        _f.setNome(fornecedor.getNome());
        _f.setCpfCnpj(fornecedor.getCpfCnpj());

        _pi.setProduto(_p);
        _pi.setFornecedor(_f);
        _pi.setPrecoUnitario(precoUnitario);

        Hibernate5Module module = new Hibernate5Module();
        module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
        module.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        String json = mapper.writeValueAsString(_pi);

        PendenciaIntegracao _pip = new PendenciaIntegracao();
        _pip.setObjectStr(json);
        _pip.setDataCriacao(Calendar.getInstance().getTime());
        _pip.setUsuarioCriacao(usuario);
        _pip.setTipoPendenciaIntegracao(tipoPendenciaIntegracao);

        saveOrUpdate(_pip);
    }

    public Page<PendenciaIntegracao> findPedencias(PendenciaIntegracaoFilter pendenciaIntegracaoProdutoHelper) throws Exception {
        return ((PendenciaIntegracaoDAOImpl) getRepository()).findPedencias(pendenciaIntegracaoProdutoHelper);
    }

    @Override
    public Optional<PendenciaIntegracao> findById(Long id) {
        Optional<PendenciaIntegracao> pendenciaIntegracaoOptional = super.findById(id);

        if (pendenciaIntegracaoOptional.isPresent()) {
            PendenciaIntegracao _o = pendenciaIntegracaoOptional.get();

            _o.setTipoPendenciaStr(messages.get(_o.getTipoPendenciaIntegracao().getMessageKey()));

            return Optional.of(_o);
        } else {
            return Optional.empty();
        }

    }

    public PendenciaIntegracao persistirNovoProduto(PendenciaIntegracao pendenciaIntegracao, Usuario usuario) throws Exception {
        Optional<PendenciaIntegracao> pendenciaIntegracaoOptional = findById(pendenciaIntegracao.getId());
        if (!pendenciaIntegracaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        pendenciaIntegracao = pendenciaIntegracaoOptional.get();

        GrupoProduto grupoProduto = grupoProdutoService.findDefault();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PendenciaIntegracaoHelper _pih = mapper.readValue(pendenciaIntegracao.getObjectStr(), PendenciaIntegracaoHelper.class);

        Produto produto = _pih.getProduto();

        produto.setGrupoProduto(grupoProduto);
        produto.setSituacaoProduto(SituacaoProdutoType.CADASTRADO);

        Produto _pN = produtoService.saveOrUpdate(produto, usuario);

        produto.setId(_pN.getId());

        Fornecedor fornecedor = _pih.getFornecedor();
        Double precoUnitario = _pih.getPrecoUnitario();
        String identificacaoProduto = _pih.getIdentificacaoProduto();

        FornecedorProduto fornecedorProduto = fornecedorProdutoService.persistir(fornecedor, _pN, identificacaoProduto);

        if (precoUnitario != null && precoUnitario.doubleValue() > 0) {
            fornecedorProdutoPrecoService.persistir(fornecedorProduto, precoUnitario);
        }

        _pih.setProduto(produto);
        String json = mapper.writeValueAsString(_pih);

        pendenciaIntegracao.setObjectStr(json);
        pendenciaIntegracao.setDataAlteracao(Calendar.getInstance().getTime());
        pendenciaIntegracao.setUsuarioAlteracao(usuario);
        pendenciaIntegracao.setFinalizado(true);
        return saveOrUpdate(pendenciaIntegracao);
    }

    public PendenciaIntegracao vincularProduto(PendenciaIntegracao pendenciaIntegracao, Usuario usuario) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PendenciaIntegracaoHelper _pih = mapper.readValue(pendenciaIntegracao.getObjectStr(), PendenciaIntegracaoHelper.class);

        Produto produto = _pih.getProduto();
        Optional<Produto> produtoOptional = produtoService.findById(produto.getId());

        if (!produtoOptional.isPresent()){
            throw new RecordNotFound();
        }

        produto = produtoOptional.get();

        Fornecedor fornecedor = _pih.getFornecedor();
        Double precoUnitario = _pih.getPrecoUnitario();
        String identificacaoProduto = _pih.getIdentificacaoProduto();

        FornecedorProduto fornecedorProduto = fornecedorProdutoService.persistir(fornecedor, produto, identificacaoProduto);

        if (precoUnitario != null && precoUnitario.doubleValue() > 0) {
            fornecedorProdutoPrecoService.persistir(fornecedorProduto, precoUnitario);
        }

        Optional<PendenciaIntegracao> pendenciaIntegracaoOptional = findById(pendenciaIntegracao.getId());
        if (!pendenciaIntegracaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        pendenciaIntegracao = pendenciaIntegracaoOptional.get();
        pendenciaIntegracao.setDataAlteracao(Calendar.getInstance().getTime());
        pendenciaIntegracao.setUsuarioAlteracao(usuario);
        pendenciaIntegracao.setFinalizado(true);
        return saveOrUpdate(pendenciaIntegracao);
    }

    public PendenciaIntegracao finalizarPendencia(PendenciaIntegracao pendenciaIntegracao, Usuario usuario) throws Exception {
        Optional<PendenciaIntegracao> pendenciaIntegracaoOptional = findById(pendenciaIntegracao.getId());
        if (!pendenciaIntegracaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        pendenciaIntegracao = pendenciaIntegracaoOptional.get();
        pendenciaIntegracao.setDataAlteracao(Calendar.getInstance().getTime());
        pendenciaIntegracao.setUsuarioAlteracao(usuario);
        pendenciaIntegracao.setFinalizado(true);
        return saveOrUpdate(pendenciaIntegracao);
    }

}
