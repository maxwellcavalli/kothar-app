package br.com.kotar.domain.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.kotar.domain.business.Cep;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoEndereco;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemFornecedor;
import br.com.kotar.domain.business.CotacaoItemFornecedorValor;
import br.com.kotar.domain.business.EnderecoComplemento;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.Produto;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;

public class RetornoConsultaCotacaoHelper {

	@JsonProperty("cotacao")
	private CotacaoHelper cotacao = new CotacaoHelper();
	final DecimalFormat df = new DecimalFormat("0.00");
	final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public CotacaoHelper getCotacao() {
		return cotacao;
	}

	public void setCotacao(CotacaoHelper cotacao) {
		this.cotacao = cotacao;
	}

	public RetornoConsultaCotacaoHelper(Cotacao cotacao, List<CotacaoItem> listCotacaoItem, List<CotacaoFornecedor> fornecedores,
			CotacaoFornecedor fornecedorVencedor) {
		this.getCotacao().id = cotacao.getId();
		this.getCotacao().nome = cotacao.getNome();
		this.getCotacao().dataCadastro = cotacao.getDataCadastro();
		this.getCotacao().dataLimiteRetorno = cotacao.getDataLimiteRetorno();
		this.getCotacao().dataEnvio = cotacao.getDataEnvio();
		this.getCotacao().cotacaoEndereco = cotacao.getCotacaoEndereco();
		this.getCotacao().cotacaoSituacaoStr = cotacao.getSituacaoStr();
		this.getCotacao().podeFinalizar = cotacao.isPodeFinalizar();
		this.getCotacao().finalizada = cotacao.getSituacaoCotacao().equals(SituacaoCotacaoType.FINALIZADA);
		this.getCotacao().nomeCliente = cotacao.getCliente().getNome();
		this.getCotacao().dataLembreteAvaliacao = cotacao.getDataLembreteAvaliacao();
		this.getCotacao().dataAvaliacao = cotacao.getDataAvaliacao();

		fornecedores.forEach(el -> {
			this.getCotacao().fornecedores.add(createCotacaoFornecedorHelper(el));
		});

		if (fornecedorVencedor != null) {
			this.getCotacao().fornecedorVencedor = createCotacaoFornecedorHelper(fornecedorVencedor);
		}

	}

	private CotacaoItemHelper createCotacaoItemHelper(CotacaoItem cotacaoItem, boolean createCotacaoItemFornecedor, boolean createValoresFornecedor) {
		CotacaoItemHelper cotacaoItemHelper = new CotacaoItemHelper();
		cotacaoItemHelper.id = cotacaoItem.getId();
		cotacaoItemHelper.produto = createProdutoHelper(cotacaoItem.getProduto());
		cotacaoItemHelper.quantidade = cotacaoItem.getQuantidade();
		// cotacaoItemHelper.observacao = cotacaoItem.getObservacao();
		cotacaoItemHelper.valoresComPendencias = cotacaoItem.isValoresComPendencias();
		if (createCotacaoItemFornecedor) {
			if (cotacaoItem.getFornecedorVencedor() != null) {
				cotacaoItemHelper.fornecedorVencedor = createCotacaoItemFornecedorHelper(cotacaoItem.getFornecedorVencedor(), true,
						createValoresFornecedor);
			}
		}

		// if (cotacaoItem.getListCotacaoFornecedor() != null) {
		// Collections.sort(cotacaoItem.getListCotacaoFornecedor(), new
		// Comparator<CotacaoItemFornecedor>() {
		// @Override
		// public int compare(CotacaoItemFornecedor o1, CotacaoItemFornecedor
		// o2) {
		// Integer colocacao1 = o1.getCotacaoFornecedor().getColocacaoGlobal();
		// Integer colocacao2 = o2.getCotacaoFornecedor().getColocacaoGlobal();
		//
		// colocacao1 = colocacao1 == null ? 0 : colocacao1;
		// colocacao2 = colocacao2 == null ? 0 : colocacao2;
		//
		// return colocacao1.compareTo(colocacao2);
		// }
		// });
		//
		// if (createCotacaoItemFornecedor){
		// cotacaoItem.getListCotacaoFornecedor().forEach(el -> {
		// cotacaoItemHelper.listCotacaoFornecedor.add(createCotacaoItemFornecedorHelper(el,
		// true, createValoresFornecedor));
		// });
		// }
		// }

		return cotacaoItemHelper;
	}

	private ProdutoHelper createProdutoHelper(Produto produto) {
		ProdutoHelper produtoHelper = new ProdutoHelper();
		produtoHelper.id = produto.getId();
		produtoHelper.nome = produto.getNome();

		return produtoHelper;
	}

	private CotacaoItemFornecedorHelper createCotacaoItemFornecedorHelper(CotacaoItemFornecedor cotacaoItemFornecedor,
			boolean createCotacaoFornecedor, boolean createValoresFornecedor) {

		CotacaoItemFornecedorHelper cotacaoItemFornecedorHelper = new CotacaoItemFornecedorHelper();
		cotacaoItemFornecedorHelper.id = cotacaoItemFornecedor.getId();
		cotacaoItemFornecedorHelper.cotacaoItemHelper = createCotacaoItemHelper(cotacaoItemFornecedor.getCotacaoItem(), createCotacaoFornecedor,
				createValoresFornecedor);
		cotacaoItemFornecedorHelper.hasPendencias = cotacaoItemFornecedor.isHasPendencias();

		// if (createCotacaoFornecedor) {
		// cotacaoItemFornecedorHelper.cotacaoFornecedor =
		// createCotacaoFornecedorHelper(cotacaoItemFornecedor.getCotacaoFornecedor());
		// }
		//
		if (createValoresFornecedor) {
			cotacaoItemFornecedor.getValores().forEach(el -> {
				cotacaoItemFornecedorHelper.valores.add(createCotacaoItemFornecedorValorHelper(el));
			});
		}

		if (cotacaoItemFornecedor.getCotacaoItemFornecedorValor() != null) {
			cotacaoItemFornecedorHelper.cotacaoItemFornecedorValor = createCotacaoItemFornecedorValorHelper(
					cotacaoItemFornecedor.getCotacaoItemFornecedorValor());

			BigDecimal unitario = cotacaoItemFornecedor.getCotacaoItemFornecedorValor().getUnitario();
			BigDecimal valorTotal = cotacaoItemFornecedor.getCotacaoItem().getQuantidade().multiply(unitario);
			cotacaoItemFornecedorHelper.valorTotal = df.format(valorTotal);
		} else {
			if (cotacaoItemFornecedorHelper.valores.size() > 0) {
				CotacaoItemFornecedorValorHelper _min = Collections.min(cotacaoItemFornecedorHelper.valores, Comparator.comparing(c -> c.unitario));
				CotacaoItemFornecedorValorHelper _max = Collections.max(cotacaoItemFornecedorHelper.valores, Comparator.comparing(c -> c.unitario));

				cotacaoItemFornecedorHelper.variacaoValor = _min.getUnitarioFmt() + " ~ " + _max.getUnitarioFmt();
			}
		}

		return cotacaoItemFornecedorHelper;
	}

	private CotacaoItemFornecedorValorHelper createCotacaoItemFornecedorValorHelper(CotacaoItemFornecedorValor cotacaoItemFornecedorValor) {
		CotacaoItemFornecedorValorHelper cotacaoItemFornecedorValorHelper = new CotacaoItemFornecedorValorHelper();
		cotacaoItemFornecedorValorHelper.id = cotacaoItemFornecedorValor.getId();
		cotacaoItemFornecedorValorHelper.unitario = cotacaoItemFornecedorValor.getUnitario();
		cotacaoItemFornecedorValorHelper.marcaModelo = cotacaoItemFornecedorValor.getMarcaModelo();
		cotacaoItemFornecedorValorHelper.selecionado = cotacaoItemFornecedorValor.isSelecionado();

		return cotacaoItemFornecedorValorHelper;
	}

	private CondicoesPagamentoHelper createCondicoesPagamentoHelper(CotacaoFornecedor cotacaoFornecedor) {
		CondicoesPagamentoHelper condicoesPagamentoHelper = new CondicoesPagamentoHelper();
		condicoesPagamentoHelper.tipo = cotacaoFornecedor.getTipoPagamentoStr();
		if (cotacaoFornecedor.getNumeroParcelas() != null) {
			condicoesPagamentoHelper.numeroParcelas = cotacaoFornecedor.getNumeroParcelas().toString();
		}

		if (cotacaoFornecedor.getJurosPacelamento() != null) {
			condicoesPagamentoHelper.taxaJuros = df.format(cotacaoFornecedor.getJurosPacelamento());
		}

		return condicoesPagamentoHelper;
	}

	private CotacaoFornecedorHelper createCotacaoFornecedorHelper(CotacaoFornecedor cotacaoFornecedor) {
		CotacaoFornecedorHelper cotacaoFornecedorHelper = new CotacaoFornecedorHelper();
		cotacaoFornecedorHelper.id = cotacaoFornecedor.getId();

		cotacaoFornecedorHelper.condicoesPagamento = createCondicoesPagamentoHelper(cotacaoFornecedor);
		cotacaoFornecedorHelper.valorGlobal = cotacaoFornecedor.getValorGlobal();
		cotacaoFornecedorHelper.colocacaoGlobal = cotacaoFornecedor.getColocacaoGlobal();
		cotacaoFornecedorHelper.fornecedor = createFornecedorHelper(cotacaoFornecedor.getFornecedor());
		cotacaoFornecedorHelper.fornecedorComPendencias = cotacaoFornecedor.isFornecedorComPendencias();
		cotacaoFornecedorHelper.prazoEntrega = cotacaoFornecedor.getEntrega();
		cotacaoFornecedorHelper.codigoAprovacaoVencedor = cotacaoFornecedor.getCodigoAprovacaoVencedor();
		
		if (cotacaoFornecedor.getDataValidade() != null) {
			cotacaoFornecedorHelper.dataValidadeFmt = sdf.format(cotacaoFornecedor.getDataValidade());
		}

		if (cotacaoFornecedor.getVariacaoMin() != null && cotacaoFornecedor.getVariacaoMax() != null) {
			cotacaoFornecedorHelper.variacaoPrecoMin = df.format(cotacaoFornecedor.getVariacaoMin());
			cotacaoFornecedorHelper.variacaoPrecoMax = df.format(cotacaoFornecedor.getVariacaoMax());
		}

		if (cotacaoFornecedor.getItensFornecedor() != null) {
			cotacaoFornecedor.getItensFornecedor().forEach(el -> {
				cotacaoFornecedorHelper.itensFornecedor.add(createCotacaoItemFornecedorHelper(el, false, true));
			});
		}

		if (cotacaoFornecedor.getDistancia() != null) {
			cotacaoFornecedorHelper.distancia = cotacaoFornecedor.getDistancia();
			cotacaoFornecedorHelper.distanciaFmt = cotacaoFornecedor.getDistanciaFmt();
		} 
		
		return cotacaoFornecedorHelper;
	}

	private FornecedorHelper createFornecedorHelper(Fornecedor fornecedor) {
		FornecedorHelper fornecedorHelper = new FornecedorHelper();
		fornecedorHelper.id = fornecedor.getId();
		fornecedorHelper.nome = fornecedor.getNome();
		fornecedorHelper.cpfCnpj = fornecedor.getCpfCnpj();
		fornecedorHelper.cep = fornecedor.getCep();
		fornecedorHelper.enderecoComplemento = fornecedor.getEnderecoComplemento();
		fornecedorHelper.enderecoFormatado = fornecedor.getEnderecoFmt();

		return fornecedorHelper;
	}

	public class FornecedorHelper {
		@JsonProperty("fornecedor_id")
		public Long id;

		@JsonProperty("fornecedor_nome")
		public String nome;

		@JsonProperty("fornecedor_cpf_cnpj")
		public String cpfCnpj;

		@JsonProperty("fornecedor_cep")
		public Cep cep;

		@JsonProperty("fornecedor_endereco")
		public EnderecoComplemento enderecoComplemento;

		@JsonProperty("fornecedor_endereco_formatado")
		public String enderecoFormatado;

	}

	public class CondicoesPagamentoHelper {

		@JsonProperty("condicoes_pagamento_tipo")
		public String tipo;

		@JsonProperty("condicoes_pagamento_numero_parcelas")
		public String numeroParcelas;

		@JsonProperty("condicoes_pagamento_juros")
		public String taxaJuros;

		@JsonProperty("condicoes_pagamento_texto_prazo")
		public String getTextoPrazo() {
			if (numeroParcelas != null && !numeroParcelas.trim().isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(numeroParcelas);
				builder.append("x");

				if (taxaJuros != null && !taxaJuros.trim().isEmpty()) {
					builder.append(" (" + taxaJuros + ")");
				}

				return builder.toString();
			}
			return "";
		}
	}

	public class CotacaoFornecedorHelper {

		@JsonProperty("cotacao_fornecedor_id")
		public Long id;

		@JsonProperty("cotacao_fornecedor_fornecedor")
		public FornecedorHelper fornecedor;

		@JsonProperty("cotacao_fornecedor_valor_global")
		public BigDecimal valorGlobal;

		@JsonProperty("cotacao_fornecedor_colocacao")
		public Integer colocacaoGlobal;

		@JsonProperty("cotacao_fornecedor_pendencia")
		public boolean fornecedorComPendencias;

		@JsonProperty("cotacao_fornecedor_prazo_entrega")
		public String prazoEntrega;

		@JsonProperty("cotacao_fornecedor_itens")
		public List<CotacaoItemFornecedorHelper> itensFornecedor = new ArrayList<>();

		@JsonProperty("cotacao_fornecedor_variacao_preco_min")
		public String variacaoPrecoMin;

		@JsonProperty("cotacao_fornecedor_variacao_preco_max")
		public String variacaoPrecoMax;

		@JsonProperty("cotacao_fornecedor_condicoes_pagamento")
		private CondicoesPagamentoHelper condicoesPagamento;

		@JsonProperty("cotacao_fornecedor_valor_global_fmt")
		public String getValorGlobalFmt() {
			if (valorGlobal == null) {
				return null;
			} else {
				return df.format(valorGlobal);
			}
		}

		@JsonProperty("codigo_aprovacao_vencedor")
		public String codigoAprovacaoVencedor;

		@JsonProperty("cotacao_fornecedor_data_validade")
		public String dataValidadeFmt;
		
		@JsonProperty("distancia_metros")
		public Integer distancia;

		@JsonProperty("distancia_formatada")
		public String distanciaFmt;
	}

	public class CotacaoItemFornecedorValorHelper {

		@JsonProperty("cotacao_item_fornecedor_valor_id")
		public Long id;

		@JsonProperty("cotacao_item_fornecedor_valor_unitario")
		public BigDecimal unitario;

		@JsonProperty("cotacao_item_fornecedor_valor_selecionado")
		public boolean selecionado;

		@JsonProperty("cotacao_item_fornecedor_valor_marca_modelo")
		public String marcaModelo;

		@JsonProperty("cotacao_item_fornecedor_valor_unitario_fmt")
		public String getUnitarioFmt() {
			if (unitario == null) {
				return null;
			} else {
				return df.format(unitario);
			}
		}
	}

	public class CotacaoItemFornecedorHelper {

		@JsonProperty("cotacao_item_fornecedor_id")
		public Long id;

		@JsonProperty("cotacao_item_fornecedor_fornecedor")
		public CotacaoFornecedorHelper cotacaoFornecedor;

		@JsonProperty("cotacao_item_fornecedor_valores")
		public List<CotacaoItemFornecedorValorHelper> valores = new ArrayList<>();

		@JsonProperty("cotacao_item_fornecedor_melhor_valor")
		public CotacaoItemFornecedorValorHelper cotacaoItemFornecedorValor;

		@JsonProperty("cotacao_item_fornecedor_valor_total")
		public String valorTotal;

		@JsonProperty("cotacao_item_fornecedor_item")
		private CotacaoItemHelper cotacaoItemHelper;

		@JsonProperty("cotacao_item_fornecedor_possui_pendencias")
		public boolean hasPendencias;

		@JsonProperty("cotacao_item_fornecedor_variacao_valor")
		public String variacaoValor;

	}

	public class ProdutoHelper {

		@JsonProperty("produto_id")
		public Long id;

		@JsonProperty("produto_nome")
		public String nome;

	}

	public class CotacaoItemHelper {

		@JsonProperty("cotacao_item_id")
		public Long id;

		@JsonProperty("cotacao_item_produto")
		public ProdutoHelper produto;

		@JsonProperty("cotacao_item_quantidade")
		public BigDecimal quantidade;

		@JsonProperty("cotacao_item_fornecedor")
		public List<CotacaoItemFornecedorHelper> listCotacaoFornecedor = new ArrayList<>();

		@JsonProperty("cotacao_item_pendencias_valores")
		public boolean valoresComPendencias;

		@JsonProperty("cotacao_item_fornecedor_vencedor")
		public CotacaoItemFornecedorHelper fornecedorVencedor;
	}

	public class CotacaoHelper {

		@JsonProperty("cotacao_id")
		public Long id;

		@JsonProperty("cotacao_nome")
		public String nome;

		@JsonProperty("cotacao_data_cadastro")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
		public Date dataCadastro;

		@JsonProperty("cotacao_data_limite_retorno")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
		public Date dataLimiteRetorno;

		@JsonProperty("cotacao_data_envio")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
		public Date dataEnvio;

		@JsonProperty("cotacao_endereco")
		public CotacaoEndereco cotacaoEndereco;

		@JsonProperty("cotacao_itens")
		public List<CotacaoItemHelper> itens = new ArrayList<>();

		@JsonProperty("cotacao_situacao_str")
		public String cotacaoSituacaoStr;

		@JsonProperty("cotacao_fornecedores")
		public List<CotacaoFornecedorHelper> fornecedores = new ArrayList<>();

		@JsonProperty("cotacao_pode_finalizar")
		public boolean podeFinalizar;

		@JsonProperty("cotacao_finalizada")
		public boolean finalizada;

		@JsonProperty("cotacao_fornecedor_vencedor")
		public CotacaoFornecedorHelper fornecedorVencedor;
		
		@JsonProperty("cotacao_cliente_nome")
		public String nomeCliente;
		
		@JsonProperty("cotacao_data_lembrete_avaliacao")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
		public Date dataLembreteAvaliacao;
		
		@JsonProperty("cotacao_data_avaliacao")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", timezone = "GMT-3")
		public Date dataAvaliacao;
		
		
	}

}
