package br.com.kotar.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.kotar.core.exception.RecordNotFound;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;
import br.com.kotar.domain.business.type.SituacaoRespostaFornecedorType;
import br.com.kotar.domain.business.type.TipoJurosPagamentoType;
import br.com.kotar.domain.business.type.TipoPagamentoType;
import br.com.kotar.domain.helper.CotacaoRespostaFilter;
import br.com.kotar.domain.helper.SituacaoRespostaFornecedorHelper;
import br.com.kotar.domain.helper.TipoJurosPagamentoHelper;
import br.com.kotar.domain.helper.TipoPagamentoHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.impl.CotacaoDAOImpl;

@Service
public class RespostaCotacaoService extends BaseService<Cotacao> {

	//@formatter:off
	//@Autowired CotacaoRepository cotacaoRepository;
	@Autowired CotacaoDAOImpl cotacaoRepositoryImpl;
	@Autowired CotacaoItemFornecedorService cotacaoItemFornecedorService;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	@Autowired CotacaoService cotacaoService;
	//@formatter:on

	@Override
	public BaseRepository<Cotacao> getRepository() {
		return cotacaoRepositoryImpl;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Cotacao findByResposta(Long id, Fornecedor fornecedor) {
		Optional<Cotacao> cotacaoOptional = findById(id);

		if (!cotacaoOptional.isPresent()){
			throw new RecordNotFound();
		}

		Cotacao cotacao = cotacaoOptional.get();

		String _message = messages.get(cotacao.getSituacaoCotacao().getMessageKey());
		cotacao.setSituacaoStr(_message);

		CotacaoFornecedor cotacaoFornecedor = cotacaoFornecedorService.findByCotacaoAndFornecedor(cotacao, fornecedor);
		cotacaoFornecedor.setFornecedor(fornecedor);
		cotacao.setCotacaoFornecedor(cotacaoFornecedor);

		List<CotacaoItemFornecedor> forncedorItens = cotacaoItemFornecedorService.findByCotacaoAndCotacaoFornecedor(cotacao, cotacaoFornecedor);
		cotacao.setForncedorItens(forncedorItens);

		boolean permiteResposta = !cotacaoFornecedor.isEnviado() && cotacaoFornecedor.getDataRecusa() == null;
		cotacao.setPermiteResposta(true);
		if (cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.ENVIADA)) {
			cotacao.setPermiteResposta(permiteResposta);
		} else {
			cotacao.setPermiteResposta(false);
			permiteResposta = false;
		}

		boolean hasFornecedorValor = false;
		for (CotacaoItemFornecedor _c : forncedorItens) {
			if (!_c.getValores().isEmpty()) {
				hasFornecedorValor = true;
				break;
			}
		}

		cotacao.setPermiteRecusar(!hasFornecedorValor && permiteResposta);

		return cotacao;
	}

	public Page<Cotacao> findCotacaoRespostaByParams(CotacaoRespostaFilter cotacaoFilter, Usuario usuario) throws Exception {
		return cotacaoRepositoryImpl.findCotacaoRespostaByParams(cotacaoFilter, usuario);
	}

	@Override
	public void delete(Cotacao cotacao) throws Exception {
		throw new NotImplementedException();
	}

	public void archiveResposta(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) throws Exception {
		List<CotacaoItemFornecedor> forncedorItens = cotacao.getForncedorItens();
		cotacaoItemFornecedorService.archiveResposta(forncedorItens, cotacaoFornecedor);
	}

	private void validaStatusCotacao(Cotacao cotacao) throws Exception {
		Optional<Cotacao> cotacaoOptional = findById(cotacao.getId());
		if (!cotacaoOptional.isPresent()){
			throw new RecordNotFound();
		}

		Cotacao _cot = cotacaoOptional.get();
		if (_cot.getSituacaoCotacao().equals(SituacaoCotacaoType.ENCERRADA) || _cot.getSituacaoCotacao().equals(SituacaoCotacaoType.FINALIZADA)) {
			throw new Exception(messages.get("cotacao.cancelamento.finalizada"));
		}
	}

	public void enviarResposta(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) throws Exception {
		validaStatusCotacao(cotacao);

		cotacaoFornecedorService.enviarResposta(cotacao, cotacaoFornecedor);
	}

	public void recusarCotacao(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) throws Exception {
		validaStatusCotacao(cotacao);
		cotacaoFornecedorService.recusarCotacao(cotacao, cotacaoFornecedor);
	}

	public List<SituacaoRespostaFornecedorHelper> getSituacoesResposta() {
		List<SituacaoRespostaFornecedorHelper> list = new ArrayList<>();

		for (SituacaoRespostaFornecedorType type : SituacaoRespostaFornecedorType.values()) {
			SituacaoRespostaFornecedorHelper helper = new SituacaoRespostaFornecedorHelper();
			helper.setValue(type);
			helper.setDescription(messages.get(type.getMessageKey()));

			list.add(helper);
		}

		return list;
	}

	public List<TipoPagamentoHelper> getTiposPagamento() {
		List<TipoPagamentoHelper> list = new ArrayList<>();

		for (TipoPagamentoType type : TipoPagamentoType.values()) {
			TipoPagamentoHelper helper = new TipoPagamentoHelper();
			helper.setValue(type);
			helper.setDescription(messages.get(type.getMessageKey()));

			list.add(helper);
		}

		return list;
	}

	public List<TipoJurosPagamentoHelper> getTiposJurosPagamento() {
		List<TipoJurosPagamentoHelper> list = new ArrayList<>();

		for (TipoJurosPagamentoType type : TipoJurosPagamentoType.values()) {
			TipoJurosPagamentoHelper helper = new TipoJurosPagamentoHelper();
			helper.setValue(type);
			helper.setDescription(messages.get(type.getMessageKey()));

			list.add(helper);
		}

		return list;
	}

}
