package br.com.kotar.web.service;

import java.util.*;

import br.com.kotar.core.exception.RecordNotFound;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.core.util.DataUtil;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoEndereco;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemArquivo;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;
import br.com.kotar.domain.helper.CotacaoFilter;
import br.com.kotar.domain.helper.EstatisticasCotacaoClienteHelper;
import br.com.kotar.domain.helper.EstatisticasCotacaoFornecedorHelper;
import br.com.kotar.domain.helper.MinhasCotacoesVencedorasFornecedorHelper;
import br.com.kotar.domain.helper.SituacaoCotacaoHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.impl.CotacaoDAOImpl;

@Service
public class CotacaoService extends BaseService<Cotacao> {

    //@formatter:off
    //@Autowired CotacaoRepository CotacaoRepository;
    @Autowired
    ClienteService clienteService;
    @Autowired
    CotacaoItemService cotacaoItemService;
    @Autowired
    CotacaoFornecedorService cotacaoFornecedorService;
    @Autowired
    CotacaoDAOImpl cotacaoDAOImpl;
    @Autowired
    CotacaoEnderecoService cotacaoEnderecoService;
    //@Autowired CotacaoEnderecoRepository cotacaoEnderecoRepository;
    @Autowired
    CepService cepService;
    @Autowired
    EnderecoComplementoService enderecoComplementoService;
    //@formatter:on

    @Override
    public BaseRepository<Cotacao> getRepository() {
        return cotacaoDAOImpl;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Optional<Cotacao> findById(Long id) {
        return findById(id, true, true);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Optional<Cotacao> findById(Long id, boolean loadQtdRespostas, boolean loadArquivos) {
        Optional<Cotacao> cotacaoOptional = getRepository().findById(id);
        if (cotacaoOptional.isPresent()) {
            Cotacao cotacao = cotacaoOptional.get();
            String _message = messages.get(cotacao.getSituacaoCotacao().getMessageKey());
            cotacao.setSituacaoStr(_message);

            SituacaoCotacaoHelper situacaoCotacaoHelper = new SituacaoCotacaoHelper();
            situacaoCotacaoHelper.setValue(cotacao.getSituacaoCotacao());
            situacaoCotacaoHelper.setDescription(messages.get(cotacao.getSituacaoCotacao().getMessageKey()));
            cotacao.setSituacaoCotacaoHelper(situacaoCotacaoHelper);

            List<CotacaoItem> itens = cotacaoItemService.findByCotacao(cotacao, loadArquivos);
            cotacao.setItens(itens);

            // retornar o numero de respostas quando enviada
            Long qtdRespostas = 0l;
            if (loadQtdRespostas) {
                if (!cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.PENDENTE)
                        && !cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.CANCELADA)) {

                    qtdRespostas = cotacaoFornecedorService.countByCotacaoRespondida(cotacao);
                }
            }

            cotacao.setCotacaoFornecedorVencedor(cotacaoFornecedorService.findVencedorByCotacao(cotacao));

            cotacao.setQuantidadeRespostas(qtdRespostas);
            cotacao.setPermiteCancelar(qtdRespostas.intValue() == 0 && cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.ENVIADA));

            return Optional.of(cotacao);
        }

        return Optional.empty();

    }

    public Cotacao saveOrUpdate(Cotacao cotacao, Usuario usuario) throws Exception {
        boolean isNew = cotacao.getId() == null || cotacao.getId().longValue() == 0;
        if (isNew) {
            Calendar cal = Calendar.getInstance();
            cotacao.setDataCadastro(cal.getTime());

            if (cotacao.getDataLimiteRetorno() == null) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cotacao.setDataLimiteRetorno(cal.getTime());
            }
            cotacao.setSituacaoCotacao(SituacaoCotacaoType.PENDENTE);
        } else {
            Optional<Cotacao> cotacaoOptional = findById(cotacao.getId());

            if (!cotacaoOptional.isPresent()){
                throw new RecordNotFound();
            }

            Cotacao _cot = cotacaoOptional.get();
            if (!_cot.getSituacaoCotacao().equals(SituacaoCotacaoType.PENDENTE)) {
                throw new Exception(messages.get("cotacao.alteracao"));
            }
        }

        CotacaoEndereco cotacaoEndereco = cotacao.getCotacaoEndereco();
        if (cotacaoEndereco == null ||
                cotacaoEndereco.getCep() == null ||
                cotacaoEndereco.getCep().getId() == null ||
                cotacaoEndereco.getCep().getId().longValue() == 0) {
            cotacao.setCotacaoEndereco(null);
        } else {
            cotacaoEndereco = cotacaoEnderecoService.persistir(cotacaoEndereco);
            cotacao.setCotacaoEndereco(cotacaoEndereco);
        }

        Cliente cliente = clienteService.findByUsuario(usuario);
        cotacao.setCliente(cliente);

        List<CotacaoItem> itens = cotacao.getItens();

        for (CotacaoItem i : itens) {
            i.setCotacao(cotacao);
            for (CotacaoItemArquivo a : i.getArquivos()) {
                a.setCotacaoItem(i);
            }
        }

        cotacao = getRepository().save(cotacao);
        cotacaoItemService.saveOrUpdate(cotacao, itens);
        return cotacao;
    }

    public Page<Cotacao> findCotacaoByParams(CotacaoFilter cotacaoFilter, Cliente cliente) throws Exception {
        return ((CotacaoDAOImpl) getRepository()).findCotacaoByParams(cotacaoFilter, cliente);
    }

    public Page<Cotacao> pesquisarCotacoesFinalizadasFornecedor(Fornecedor fornecedor, CotacaoFilter cotacaoFilter) throws Exception {
        return ((CotacaoDAOImpl) getRepository()).pesquisarCotacoesFinalizadasFornecedor(fornecedor, cotacaoFilter);
    }

    public void delete(Cotacao cotacao) throws Exception {
        Optional<Cotacao> cotacaoOptional = findById(cotacao.getId());
        if (!cotacaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        cotacao = cotacaoOptional.get();
        if (!cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.PENDENTE)) {
            throw new Exception(messages.get("cotacao.exclusao"));
        }

        cotacaoItemService.delete(cotacao);

        super.delete(cotacao);
    }

    public Cotacao enviarCotacao(Cotacao cotacao) throws Exception {
        Date dLimite = null;
        if (cotacao.getDataLimiteRetorno() != null) {
            dLimite = cotacao.getDataLimiteRetorno();
        }

        CotacaoEndereco cotacaoEndereco = cotacao.getCotacaoEndereco();
        if (cotacaoEndereco == null) {
            throw new Exception(messages.get("cotacao.escolha.endereco"));
        }
        if (cotacaoEndereco.getAlias() == null || cotacaoEndereco.getAlias().trim().compareTo("") == 0) {
            throw new Exception(messages.get("cotacao.informe.alias.endereco"));
        }

        cotacaoEndereco = cotacaoEnderecoService.persistir(cotacaoEndereco);
        Optional<Cotacao> cotacaoOptional = findById(cotacao.getId());

        if (!cotacaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        cotacao = cotacaoOptional.get();
        cotacao.setCotacaoEndereco(cotacaoEndereco);

        if (dLimite != null) {
            cotacao.setDataLimiteRetorno(dLimite);
        }

        if (!cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.PENDENTE)) {
            throw new Exception(messages.get("cotacao.ja.enviada"));
        }

        Date hoje = Calendar.getInstance().getTime();

        if (cotacao.getDataLimiteRetorno().before(hoje)) {
            throw new Exception(messages.get("cotacao.data.limite.invalida"));
        } else {
            // verificar se a data limite possui pelo menos 1h da data atual
            Long hours = DataUtil.diferrenceBetweenTwoDatesInHours(hoje, cotacao.getDataLimiteRetorno());
            if (hours < 1) {
                throw new Exception(messages.get("cotacao.data.limite.range"));
            }
        }

        cotacaoItemService.enviarItensCotacao(cotacao, cotacao.getItens());

        cotacao.setDataEnvio(Calendar.getInstance().getTime());
        cotacao.setSituacaoCotacao(SituacaoCotacaoType.ENVIADA);

        return getRepository().save(cotacao);
    }

    public Cotacao cancelarCotacao(Cotacao cotacao) throws Exception {
        Optional<Cotacao> cotacaoOptional = findById(cotacao.getId());
        if (!cotacaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        cotacao = cotacaoOptional.get();
        if (cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.PENDENTE)) {
            delete(cotacao);

            return null;
        } else {
            if (cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.CANCELADA)) {
                throw new Exception(messages.get("cotacao.cancelamento.existente"));
            }

            if (cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.FINALIZADA)) {
                throw new Exception(messages.get("cotacao.cancelamento.finalizada"));
            }

            if (cotacao.getQuantidadeRespostas().longValue() > 0) {
                throw new Exception(messages.get("cotacao.cancelamento.possui.respostas"));
            }

            cotacao.setSituacaoCotacao(SituacaoCotacaoType.CANCELADA);
            return save(cotacao);
        }
    }

    public List<Cotacao> findCotacaoExpirada() {
        List<SituacaoCotacaoType> situacaoCotacao = Arrays.asList(SituacaoCotacaoType.ENVIADA);


        return ((CotacaoDAOImpl) getRepository()).findCotacaoExpirada(situacaoCotacao);
    }

    public Cotacao copiarCotacao(Cotacao cotacao) throws Exception {
        String nome = cotacao.getNome();

        Optional<Cotacao> cotacaoOptional = findById(cotacao.getId());
        if (!cotacaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        cotacao = cotacaoOptional.get();
        if (nome == null || nome.trim().isEmpty()) {
            nome = cotacao.getNome();
        }

        Cotacao _new = (Cotacao) BeanUtils.cloneBean(cotacao);
        _new.setId(null);
        _new.setSituacaoCotacao(SituacaoCotacaoType.PENDENTE);
        _new.setNome(nome);
        _new.setCotacaoEndereco(null);
        _new.setDataEnvio(null);

        Calendar cal = Calendar.getInstance();
        _new.setDataCadastro(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, 1);
        _new.setDataLimiteRetorno(cal.getTime());

        _new = save(_new);

        cotacaoItemService.copiarItensCotacao(cotacao, _new);

        return _new;
    }

    public List<SituacaoCotacaoHelper> getSituacoes() {
        List<SituacaoCotacaoHelper> list = new ArrayList<>();

        for (SituacaoCotacaoType type : SituacaoCotacaoType.values()) {
            SituacaoCotacaoHelper helper = new SituacaoCotacaoHelper();
            helper.setValue(type);
            helper.setDescription(messages.get(type.getMessageKey()));

            list.add(helper);
        }

        return list;
    }

    public void finalizar(Cotacao cotacao) throws Exception {
        Optional<Cotacao> cotacaoOptional = findById(cotacao.getId());

        if (!cotacaoOptional.isPresent()){
            throw new RecordNotFound();
        }

        cotacao = cotacaoOptional.get();
        cotacao.setDataFinalizado(Calendar.getInstance().getTime());
        cotacao.setSituacaoCotacao(SituacaoCotacaoType.FINALIZADA);
        save(cotacao);
    }

    public List<MinhasCotacoesVencedorasFornecedorHelper> findMinhasCotacoesVencedorasFornecedorPorMes(Fornecedor fornecedor) throws Exception {
        if (fornecedor == null) {
            throw new Exception(messages.get("cotacao.fornecedor.invalido"));
        }

        return ((CotacaoDAOImpl) getRepository()).findMinhasCotacoesVencedorasFornecedorPorMes(fornecedor);
    }

    public List<EstatisticasCotacaoFornecedorHelper> findEstatisticasCotacaoFornecedor(Fornecedor fornecedor) throws Exception {
        if (fornecedor == null) {
            throw new Exception(messages.get("cotacao.fornecedor.invalido"));
        }

        return ((CotacaoDAOImpl) getRepository()).findEstatisticasCotacaoFornecedor(fornecedor);
    }

    public List<EstatisticasCotacaoClienteHelper> findEstatisticasCotacaoCliente(Cliente cliente) throws Exception {
        if (cliente == null) {
            throw new Exception(messages.get("cliente.not.found"));
        }

        return ((CotacaoDAOImpl) getRepository()).findEstatisticasCotacaoCliente(cliente);
    }

}
