package br.com.kotar.web.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.core.util.StringUtil;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.type.TipoAvaliacaoCotacaoType;
import br.com.kotar.domain.helper.ConsultaRespostaFilter;
import br.com.kotar.domain.helper.RetornoConsultaCotacaoHelper;
import br.com.kotar.web.repository.impl.CotacaoDAOImpl;

@Service
public class ConsultaRespostaService extends BaseService<Cotacao> {

	//@formatter:off
	@Autowired CotacaoDAOImpl cotacaoDAOImpl;
	@Autowired CotacaoService cotacaoService;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	@Autowired CotacaoItemService cotacaoItemService;
	@Autowired CotacaoItemFornecedorService cotacaoItemFornecedorService;
	@Autowired FornecedorService fornecedorService;
	@Autowired CotacaoItemFornecedorValorService cotacaoItemFornecedorValorService;
	//@Autowired GeoService geoService;
	//@formatter:on

	@Override
	public BaseRepository<Cotacao> getRepository() {
		return cotacaoDAOImpl;
	}

	public Page<Cotacao> findConsultaRespostaByParams(ConsultaRespostaFilter consultaRespostaFilter, Cliente cliente)
			throws Exception {
		return ((CotacaoDAOImpl) getRepository()).findConsultaRespostaByParams(consultaRespostaFilter, cliente);
	}

	public RetornoConsultaCotacaoHelper findByCotacaoWithFornecedores(Long id) throws Exception {
		CotacaoFornecedor vencedor = null;
		Optional<Cotacao> cotacaoOptional = cotacaoService.findById(id, false, false);

		if (!cotacaoOptional.isPresent()){
			throw new RecordNotFound();
		}

		Cotacao cotacao = cotacaoOptional.get();
		TipoAvaliacaoCotacaoType tipoAvaliacaoCotacao = TipoAvaliacaoCotacaoType.GLOBAL;

		List<CotacaoItem> listCotacaoItem = cotacao.getItens();// cotacaoItemService.findByCotacao(cotacao);

		// validar os fornecedores que enviaram valores conforme o tipo de
		// avaliacao
		List<CotacaoFornecedor> cotacaoFornecedores = cotacaoFornecedorService
				.findByCotacaoFornecedorWithValues(cotacao, tipoAvaliacaoCotacao);

		for (CotacaoItem cotacaoItem : listCotacaoItem) {
			cotacaoItem.setListCotacaoFornecedor(new ArrayList<>());

			for (CotacaoFornecedor cotacaoFornecedor : cotacaoFornecedores) {
				CotacaoItemFornecedor cotacaoItemFornecedor = cotacaoItemFornecedorService
						.findByCotacaoAndCotacaoItemFornecedorWithValues(cotacaoItem, cotacaoFornecedor);

				if (cotacaoFornecedor.getItensFornecedor() == null) {
					cotacaoFornecedor.setItensFornecedor(new ArrayList<>());
				}

				cotacaoItem.getListCotacaoFornecedor().add(cotacaoItemFornecedor);
			}
		}

		List<CotacaoFornecedor> fornecedores = new ArrayList<>();
		if (tipoAvaliacaoCotacao.equals(TipoAvaliacaoCotacaoType.GLOBAL)) {
			// analisar os itens
			for (CotacaoItem cotacaoItem : listCotacaoItem) {
				Produto produto = cotacaoItem.getProduto();
				// cotacaoItem.setListCotacaoFornecedor(new ArrayList<>());

				boolean hasPendencia = false;
				List<CotacaoItemFornecedor> listAgrupa = cotacaoItem.getListCotacaoFornecedor();
				for (CotacaoItemFornecedor cotacaoItemFornecedor : listAgrupa) {
					cotacaoItemFornecedor.setCotacaoItem(cotacaoItem);
					boolean hasFornecedorPendencia = false;

					List<CotacaoItemFornecedorValor> valores = cotacaoItemFornecedor.getValores();
					CotacaoFornecedor cotacaoFornecedor = cotacaoItemFornecedor.getCotacaoFornecedor();

					if (produto.isGenerico()) {
						if (valores.size() == 1) {
							cotacaoItemFornecedor.setCotacaoItemFornecedorValor(valores.get(0));
						} else {
							long qtdSelecionados = valores.stream().filter(el -> el.isSelecionado() == true).count();
							if (qtdSelecionados > 0) {
								CotacaoItemFornecedorValor cotacaoItemFornecedorValor = valores.stream()
										.filter(el -> el.isSelecionado() == true).collect(Collectors.toList()).get(0);

								cotacaoItemFornecedor.setCotacaoItemFornecedorValor(cotacaoItemFornecedorValor);
							} else {
								hasPendencia = true;
								hasFornecedorPendencia = true;

								cotacaoItemFornecedor.setHasPendencias(true);
							}
						}
					} else {
						if (valores.size() == 1) {
							cotacaoItemFornecedor.setCotacaoItemFornecedorValor(valores.get(0));
						}
					}

					int index = -1;
					for (int i = 0; i < fornecedores.size(); i++) {
						if (fornecedores.get(i).getId().longValue() == cotacaoFornecedor.getId().longValue()) {
							index = i;
							break;
						}
					}

					if (cotacaoFornecedor.getItensFornecedor() == null) {
						cotacaoFornecedor.setItensFornecedor(new ArrayList<>());
					}

					if (!cotacaoFornecedor.isFornecedorComPendencias()) {
						if (hasFornecedorPendencia) {
							cotacaoFornecedor.setFornecedorComPendencias(true);
						}
					}

					if (cotacaoItemFornecedor.getValores().size() == 1) {
						cotacaoItemFornecedor.getValores().get(0).setSelecionado(true);
					}

					cotacaoFornecedor.getItensFornecedor().add(cotacaoItemFornecedor);

					if (index == -1) {
						fornecedores.add(cotacaoFornecedor);
					} else {
						fornecedores.set(index, cotacaoFornecedor);
					}
				}

				cotacaoItem.setValoresComPendencias(hasPendencia);
			}

			// verificar se existe alguma pendencias
			long qtdPendencias = listCotacaoItem.stream().filter(el -> el.isValoresComPendencias() == true).count();

			// agrupar todos os valores do fornecedor
			Map<CotacaoFornecedor, List<CotacaoItemFornecedor>> map = new HashMap<>();
			for (CotacaoItem cotacaoItem : listCotacaoItem) {

				cotacaoItem.getListCotacaoFornecedor().forEach(el -> {
					// if (!el.isHasPendencias()){
					List<CotacaoItemFornecedor> list = map.get(el.getCotacaoFornecedor());
					if (list == null) {
						list = new ArrayList<>();
					}

					list.add(el);
					map.put(el.getCotacaoFornecedor(), list);
					// }
				});
			}

			Function<CotacaoItemFornecedor, BigDecimal> totalMapper = el -> el.getCotacaoItemFornecedorValor() == null
					|| el.isHasPendencias() ? BigDecimal.ZERO
							: el.getCotacaoItemFornecedorValor().getUnitario()
									.multiply(el.getCotacaoItem().getQuantidade());

			// somar o valor total do fornecedor
			for (CotacaoFornecedor cotacaoFornecedor : map.keySet()) {
				CotacaoFornecedor _cf = fornecedores.stream()
						.filter(el -> el.getId().longValue() == cotacaoFornecedor.getId().longValue()).findFirst()
						.get();
				if (_cf == null) {
					_cf = cotacaoFornecedor;
				}

				List<CotacaoItemFornecedor> list = map.get(cotacaoFornecedor);
				long qtdPendenciasFornecedor = list.stream().filter(el -> el.isHasPendencias()).count();

				BigDecimal _min = BigDecimal.ZERO;
				BigDecimal _max = BigDecimal.ZERO;

				if (qtdPendenciasFornecedor > 0) {
					for (CotacaoItemFornecedor cotacaoItemFornecedor : list) {

						BigDecimal qtd = cotacaoItemFornecedor.getCotacaoItem().getQuantidade();
						if (cotacaoItemFornecedor.getValores().size() == 1) {
							_min = _min.add(cotacaoItemFornecedor.getValores().get(0).getUnitario().multiply(qtd));
							_max = _max.add(cotacaoItemFornecedor.getValores().get(0).getUnitario().multiply(qtd));
						} else {

							CotacaoItemFornecedorValor _cMin = Collections.min(cotacaoItemFornecedor.getValores(),
									Comparator.comparing(c -> c.getUnitario()));
							CotacaoItemFornecedorValor _cMax = Collections.max(cotacaoItemFornecedor.getValores(),
									Comparator.comparing(c -> c.getUnitario()));

							_min = _min.add(_cMin.getUnitario().multiply(qtd));
							_max = _max.add(_cMax.getUnitario().multiply(qtd));
						}
					}
					;
				}

				BigDecimal valorGlobal = list.stream().map(totalMapper).reduce(BigDecimal.ZERO, BigDecimal::add);
				_cf.setValorGlobal(valorGlobal);

				cotacaoFornecedor.setVariacaoMin(_min);
				cotacaoFornecedor.setVariacaoMax(_max);

				_cf.setFornecedorComPendencias(qtdPendenciasFornecedor > 0);

				// fornecedores

				int index = fornecedores.indexOf(_cf);
				if (index == -1) {
					fornecedores.add(_cf);
				} else {
					fornecedores.set(index, _cf);
				}
			}

			// avaliar o fornecedor com o melhor valor
			Collections.sort(fornecedores, new Comparator<CotacaoFornecedor>() {

				@Override
				public int compare(CotacaoFornecedor o1, CotacaoFornecedor o2) {
					int compareVencedorByUser = o1.getVencedorByUser().compareTo(o2.getVencedorByUser());
					if (compareVencedorByUser == 0) {
						Boolean pendencia1 = o1.isFornecedorComPendencias();
						Boolean pendencia2 = o2.isFornecedorComPendencias();

						int comparePendencia = pendencia1.compareTo(pendencia2);
						if (comparePendencia == 0) {
							int compareValor = o1.getValorGlobal().compareTo(o2.getValorGlobal());
							if (compareValor == 0) {
								return o1.getDataResposta().compareTo(o2.getDataResposta());
							} else {
								return compareValor;
							}
						} else {
							return comparePendencia;
						}
					} else {
						return compareVencedorByUser * -1;
					}
				}
			});

			int colocacao = 1;
			for (CotacaoFornecedor cotacaoFornecedor : fornecedores) {
				cotacaoFornecedor.setColocacaoGlobal(colocacao);
				colocacao++;
			}

			// carregar os enderecos
			fornecedores.forEach(el -> {
				Fornecedor f = el.getFornecedor();
				Optional<Fornecedor> forncedorOptional = fornecedorService.findById(f.getId());

				if (!forncedorOptional.isPresent()){
					throw new RecordNotFound();
				}

				f = forncedorOptional.get();
				el.setFornecedor(f);

				if (el.getTipoPagamento() != null) {
					el.setTipoPagamentoStr(messages.get(el.getTipoPagamento().getMessageKey()));
				}

				if (el.getTipoJurosPagamento() != null) {
					el.setTipoJurosPagamentoStr(messages.get(el.getTipoJurosPagamento().getMessageKey()));
				}
			});

			// pegar o fornecedor que esta em primeiro lugar
			CotacaoFornecedor cotacaoFornecedor = fornecedores.get(0);
			for (CotacaoItem cotacaoItem : listCotacaoItem) {
				CotacaoItemFornecedor cotacaoItemFornecedor = cotacaoItem.getListCotacaoFornecedor().stream().filter(
						el -> el.getCotacaoFornecedor().getId().longValue() == cotacaoFornecedor.getId().longValue())
						.collect(Collectors.toList()).get(0);

				cotacaoItem.setFornecedorVencedor(cotacaoItemFornecedor);
			}
			// }

			boolean hasSituacaoNaoPermite = cotacao.isSituacaoPendenteCanceladaFinalizada();
			cotacao.setPodeFinalizar(qtdPendencias == 0 && !hasSituacaoNaoPermite);

			// setar a cotação fornecedor vencedora
			if (fornecedores != null && fornecedores.size() > 0) {
				vencedor = fornecedores.get(0);
			}
		}

		return new RetornoConsultaCotacaoHelper(cotacao, listCotacaoItem, fornecedores, vencedor);
	}

	public void finalizar(Cotacao cotacao, CotacaoFornecedor cotacaoFornecedor) throws Exception {
		Optional<Cotacao> cotacaoOptional = cotacaoService.findById(cotacao.getId());
		if (!cotacaoOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacao = cotacaoOptional.get();
		if (!cotacao.isSituacaoPendenteCanceladaFinalizada()) {
			forceFornecedorVencedor(cotacaoFornecedor);

			String entregaDias = cotacaoFornecedor.getEntrega();
			if (entregaDias != null) {
				Integer diasEntregaLembreteAvaliacao = new Integer(entregaDias);
				
				Date dataAtual = Calendar.getInstance().getTime();
				
				Calendar dataLembreteCalendar = Calendar.getInstance();
				dataLembreteCalendar.setTime(dataAtual);
				dataLembreteCalendar.add(Calendar.DAY_OF_MONTH, diasEntregaLembreteAvaliacao);
				
				cotacao.setDataLembreteAvaliacao(dataLembreteCalendar.getTime());
			}
			
			cotacaoService.finalizar(cotacao);

			String codigoVencedor = StringUtil.generateRandString(8, "A-NP-Z1-9");
			cotacaoFornecedor.setCodigoAprovacaoVencedor(codigoVencedor);

			RetornoConsultaCotacaoHelper domain = findByCotacaoWithFornecedores(cotacao.getId());
			BigDecimal valorTotalAprovado = domain.getCotacao().fornecedorVencedor.valorGlobal;
			cotacaoFornecedor.setValorTotalAprovado(valorTotalAprovado);
			cotacaoFornecedorService.save(cotacaoFornecedor);
		} else {
			throw new Exception(messages.get("consulta.resposta.cotacao.finish"));
		}
	}

	public void forceFornecedorVencedor(CotacaoFornecedor cotacaoFornecedor) throws Exception {
		Cotacao cotacao = cotacaoFornecedor.getCotacao();

		Optional<Cotacao> cotacaoOptional = cotacaoService.findById(cotacao.getId());
		if (!cotacaoOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacao = cotacaoOptional.get();

		// validar se existem itens com pendencias

		if (!cotacao.isSituacaoPendenteCanceladaFinalizada()) {
			cotacaoFornecedorService.forceFornecedorVencedor(cotacaoFornecedor);
		} else {
			throw new Exception(messages.get("consulta.resposta.cotacao.change.winner"));
		}
	}

	public void alterarValorSelecionado(CotacaoItemFornecedorValor cotacaoItemFornecedorValor) throws Exception {
		CotacaoItemFornecedor cotacaoItemFornecedor = cotacaoItemFornecedorValor.getCotacaoItemFornecedor();

		List<CotacaoItemFornecedorValor> list = cotacaoItemFornecedorValorService
				.findByCotacaoItemFornecedor(cotacaoItemFornecedor);
		for (CotacaoItemFornecedorValor cifv : list) {
			if (cifv.getId().longValue() != cotacaoItemFornecedorValor.getId().longValue()) {
				cifv.setSelecionado(false);
			}
		}

		cotacaoItemFornecedorValorService.save(list);
		cotacaoItemFornecedorValorService.save(cotacaoItemFornecedorValor);
	}

}
