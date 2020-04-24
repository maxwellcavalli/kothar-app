package br.com.kotar.web.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.core.util.DataUtil;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;
import br.com.kotar.domain.business.type.TipoAvaliacaoCotacaoType;
import br.com.kotar.domain.business.type.TipoJurosPagamentoType;
import br.com.kotar.domain.business.type.TipoPagamentoType;
import br.com.kotar.domain.helper.CotacaoFornecedorFilter;
import br.com.kotar.web.repository.CotacaoFornecedorRepository;
import br.com.kotar.web.repository.impl.CotacaoFornecedorDAOImpl;
import br.com.kotar.web.util.ServiceUtil;

@Service
public class CotacaoFornecedorService extends BaseService<CotacaoFornecedor> {

	//@formatter:off
	@Autowired CotacaoFornecedorRepository cotacaoFornecedorRepository;
	@Autowired CotacaoItemFornecedorValorService cotacaoItemFornecedorValorService;
	@Autowired CotacaoService cotacaoService;
	@Autowired CotacaoItemService cotacaoItemService;
	@Autowired CotacaoItemFornecedorService cotacaoItemFornecedorService;
	@Autowired GeoService geoService;
	@Autowired CotacaoFornecedorDAOImpl cotacaoFornecedorDAOImpl;
	@Autowired FornecedorService fornecedorService;
	@Autowired AsyncGeoService asyncGeoService;
	//@formatter:on

	@Override 
	public BaseRepository<CotacaoFornecedor> getRepository() {
		return cotacaoFornecedorDAOImpl;
	}

	public CotacaoFornecedor archiveResposta(CotacaoFornecedor cotacaoFornecedor) throws Exception {
		Date dataValidade = cotacaoFornecedor.getDataValidade();	
		TipoPagamentoType tipoPagamento = cotacaoFornecedor.getTipoPagamento();
		Integer numeroParcelas = cotacaoFornecedor.getNumeroParcelas();
		BigDecimal jurosPacelamento = cotacaoFornecedor.getJurosPacelamento();
		TipoJurosPagamentoType tipoJurosPagamento = cotacaoFornecedor.getTipoJurosPagamento();
		
		if (tipoPagamento.equals(TipoPagamentoType.AVISTA)) {
			numeroParcelas = null;
			jurosPacelamento = null;
			tipoJurosPagamento = null;
		}
		
		String entrega = cotacaoFornecedor.getEntrega();

		Optional<CotacaoFornecedor> cotacaoFornecedorOptional = findById(cotacaoFornecedor.getId());
		if (!cotacaoFornecedorOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacaoFornecedor = cotacaoFornecedorOptional.get();
		cotacaoFornecedor.setEntrega(entrega);
		cotacaoFornecedor.setDataValidade(dataValidade);	
		cotacaoFornecedor.setTipoPagamento(tipoPagamento);
		cotacaoFornecedor.setNumeroParcelas(numeroParcelas);
		cotacaoFornecedor.setJurosPacelamento(jurosPacelamento);
		cotacaoFornecedor.setTipoJurosPagamento(tipoJurosPagamento);
		
		cotacaoFornecedor.setEnviado(false);
		cotacaoFornecedorRepository.save(cotacaoFornecedor);
		return cotacaoFornecedor;
	}
	

	

	public void enviarResposta(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) throws Exception {
		Date dataValidade = cotacaoFornecedor.getDataValidade();
		Optional<CotacaoFornecedor> cotacaoFornecedorOptional = findById(cotacaoFornecedor.getId());

		if (!cotacaoFornecedorOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacaoFornecedor = cotacaoFornecedorOptional.get();
		cotacaoFornecedor.setDataValidade(dataValidade);
		
		Date dAtual = DataUtil.dataAtual();
		if (dataValidade.before(dAtual)){
			throw new Exception(messages.get("cotacao.resposta.data.validade"));
		}

		cotacaoFornecedor.setDataResposta(Calendar.getInstance().getTime());
		cotacaoFornecedor.setEnviado(true);
		cotacaoFornecedorRepository.save(cotacaoFornecedor); 

		// verificar se todos os fornecedores responderam ou recusaram, caso
		// positivo, finalizar a cotacao
		List<CotacaoFornecedor> list = cotacaoFornecedorRepository.findByCotacao(cotacao);
		int qtdRecusado = 0;
		int qtdEnviado = 0;
		for (CotacaoFornecedor cf : list) {
			if (cf.getDataRecusa() != null) {
				qtdRecusado++;
			} else if (cf.isEnviado()) {
				qtdEnviado++;
			}
		}

		// todos os fornecedores recusaram a cotacao, encerrar a mesma
		if ((qtdRecusado + qtdEnviado) == list.size()) {
			cotacao.setSituacaoCotacao(SituacaoCotacaoType.ENCERRADA);
			cotacaoService.save(cotacao);
		}

		System.out.println("Inicio Georefenciamento");
		CompletableFuture<Boolean> geo = asyncGeoService.geoReferenciar(cotacao, cotacaoFornecedor);		
		CompletableFuture.allOf(geo);
		System.out.println("Fim Georefenciamento");
		
	}

	public void recusarCotacao(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) throws Exception {
		Optional<CotacaoFornecedor> cotacaoFornecedorOptional = findById(cotacaoFornecedor.getId());
		if (!cotacaoFornecedorOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacaoFornecedor = cotacaoFornecedorOptional.get();

		cotacaoFornecedor.setEnviado(true);
		cotacaoFornecedor.setDataRecusa(Calendar.getInstance().getTime());
		cotacaoFornecedorRepository.save(cotacaoFornecedor);

		// verificar se existem mais fornecedores pendentes de resposta, caso
		// nao tenha, mudar o status da cotacao para encerrada
		List<CotacaoFornecedor> list = cotacaoFornecedorRepository.findByCotacao(cotacao);
		int qtdRecusado = 0;
		int qtdEnviado = 0;
		for (CotacaoFornecedor cf : list) {
			if (cf.getDataRecusa() != null) {
				qtdRecusado++;
			} else if (cf.isEnviado()) {
				qtdEnviado++;
			}
		}

		if (qtdEnviado == 0) {
			// todos os fornecedores recusaram a cotacao, encerrar a mesma
			if (qtdRecusado == list.size()) {
				cotacao.setSituacaoCotacao(SituacaoCotacaoType.FINALIZADA);
				cotacaoService.save(cotacao);
			}
		} else {
			if (qtdEnviado + qtdRecusado == list.size()){
				cotacao.setSituacaoCotacao(SituacaoCotacaoType.ENCERRADA);
				cotacaoService.save(cotacao);
			}
		}
	}

	public CotacaoFornecedor findByCotacaoAndFornecedor(Cotacao cotacao, Fornecedor fornecedor) {
		return cotacaoFornecedorRepository.findByCotacaoAndFornecedor(cotacao, fornecedor);
	}

	public long countByCotacaoRespondida(Cotacao cotacao) {
		return cotacaoFornecedorRepository.countByCotacaoRespondida(cotacao);
	}

	public CotacaoFornecedor findVencedorByCotacao(Cotacao cotacao) {
		
		CotacaoFornecedor cotacaoFornecedor = cotacaoFornecedorRepository.findVencedorByCotacao(cotacao); 

		if (cotacaoFornecedor != null) {
			Optional<Fornecedor> fornecedorOptional = fornecedorService.findById(cotacaoFornecedor.getFornecedor().getId());
			if (!fornecedorOptional.isPresent()){
				throw new RecordNotFound();
			}

			Fornecedor fornecedor = fornecedorOptional.get();
			cotacaoFornecedor.setFornecedor(fornecedor);
			
			ServiceUtil.popularTextoPrazo(cotacaoFornecedor, messages);
		}
		
		return cotacaoFornecedor;
	} 
	
	public List<CotacaoFornecedor> findByCotacao(Cotacao cotacao) throws Exception {
		return cotacaoFornecedorRepository.findByCotacao(cotacao);
	}

	public List<CotacaoFornecedor> findVencedorByCliente(Cliente cliente) {
		return cotacaoFornecedorRepository.findVencedorByCliente(cliente);
	} 
	
	public List<CotacaoFornecedor> findByCotacaoFornecedorWithValues(Cotacao cotacao, TipoAvaliacaoCotacaoType tipoAvaliacaoCotacao)
			throws Exception {
		return cotacaoFornecedorRepository.findByCotacaoGlobal(cotacao);
		
		//List<CotacaoFornecedor> listCotacaoFornecedor = findByCotacao(cotacao);
//		List<CotacaoItem> listCotacaoItem = cotacaoItemService.findByCotacao(cotacao);
//
//		List<CotacaoFornecedor> retorno = new ArrayList<>();
//
//		for (CotacaoFornecedor cf : listCotacaoFornecedor) {
//			if (!cf.isEnviado() || cf.getDataRecusa() != null) {
//				continue;
//			}
//
//			List<CotacaoItemFornecedor> listCotacaoItemFornecedor = cotacaoItemFornecedorService.findByCotacaoAndCotacaoFornecedor(cotacao, cf);
//			// verificar se todos os itens possuem valores informados
//			int qtdItensComValor = 0;
//			for (CotacaoItemFornecedor cotacaoItemFornecedor : listCotacaoItemFornecedor) {
//				List<CotacaoItemFornecedorValor> listCotacaoItemFornecedorValor = cotacaoItemFornecedorValorService
//						.findByCotacaoItemFornecedor(cotacaoItemFornecedor);
//				if (listCotacaoItemFornecedorValor.size() > 0) {
//					qtdItensComValor++;
//				}
//			}
//
//			// verificar se o fornecedor atende a todos os itens
//			if (tipoAvaliacaoCotacao.equals(TipoAvaliacaoCotacaoType.GLOBAL)) {
//				if (listCotacaoItemFornecedor.size() != listCotacaoItem.size()) {
//					continue;
//				}
//
//				if (listCotacaoItemFornecedor.size() != qtdItensComValor) {
//					continue;
//				}
//			} else {
//				// verificar se ao menos 1 item possui valor
//				if (qtdItensComValor == 0) {
//					continue;
//				}
//			}
//
//			retorno.add(cf);
//		}
//
//		return retorno;
	}

	public void forceFornecedorVencedor(CotacaoFornecedor cotacaoFornecedor) throws Exception {
		if (hasPendenciasFornecedor(cotacaoFornecedor)){
			throw new Exception (messages.get("consulta.resposta.cotacao.finish"));
		}
		
		Cotacao _cot = cotacaoFornecedor.getCotacao();
		// resetar todas as alteracoes de "forcar vencedor"
		List<CotacaoFornecedor> list = findByCotacao(_cot);
		for (CotacaoFornecedor cf : list) {
			if (cf.getVencedorByUser().booleanValue() == true) {
				cf.setVencedorByUser(false);
				save(cf);
			}
		}

		cotacaoFornecedor.setVencedorByUser(true);
		save(cotacaoFornecedor);
	}
	
	private boolean hasPendenciasFornecedor(CotacaoFornecedor cotacaoFornecedor) throws Exception {
		List<CotacaoItemFornecedor> list = cotacaoItemFornecedorService.findByCotacaoFornecedor(cotacaoFornecedor);
		int qtdPendencias = 0;
		for (CotacaoItemFornecedor cotacaoItemFornecedor : list){
			boolean hasSelected = false;
			if (cotacaoItemFornecedor.getValores().size() > 1){
				for (CotacaoItemFornecedorValor cotacaoItemFornecedorValor: cotacaoItemFornecedor.getValores()){
					if (cotacaoItemFornecedorValor.isSelecionado()){
						hasSelected = true;
						break;
					}
				}
				
				if (!hasSelected){
					qtdPendencias++;
				}
			}
		}
		
		return qtdPendencias > 0; 
	}
	
	public Page<CotacaoFornecedor> findCotacaoFornecedorVencedorByCliente(Cliente cliente, CotacaoFornecedorFilter cotacaoFornecedorFilter) throws Exception {
		return ((CotacaoFornecedorDAOImpl) getRepository()).findCotacaoFornecedorVencedorByCliente(cliente, cotacaoFornecedorFilter);
	}
	
	public List<CotacaoFornecedor> findRespostasEnviadasParaPush() {
		return cotacaoFornecedorRepository.findRespostasEnviadasParaPush();
	}

}
