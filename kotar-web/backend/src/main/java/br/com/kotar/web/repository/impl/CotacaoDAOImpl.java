package br.com.kotar.web.repository.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.repository.impl.BaseCrudRepositoryImpl;
import br.com.kotar.core.util.DataUtil;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;
import br.com.kotar.domain.business.type.SituacaoRespostaFornecedorType;
import br.com.kotar.domain.helper.ConsultaRespostaFilter;
import br.com.kotar.domain.helper.CotacaoFilter;
import br.com.kotar.domain.helper.CotacaoRespostaFilter;
import br.com.kotar.domain.helper.EstatisticasCotacaoClienteHelper;
import br.com.kotar.domain.helper.EstatisticasCotacaoFornecedorHelper;
import br.com.kotar.domain.helper.MinhasCotacoesVencedorasFornecedorHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.CotacaoRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CotacaoDAOImpl extends BaseCrudRepositoryImpl<Cotacao> implements CotacaoRepository {
	//@formatter:off
	@Autowired CotacaoRepository cotacaoRepository;
	//@formatter:on

	@Override
	public CrudRepository<Cotacao> getRepository() {
		return cotacaoRepository;
	}

	public Page<Cotacao> pesquisarCotacoesFinalizadasFornecedor(Fornecedor fornecedor, CotacaoFilter cotacaoFilter) throws Exception {
		StringBuilder hql = new StringBuilder();
		hql.append(" select cotacao ");
		hql.append("   from Cotacao cotacao ");
		hql.append("  inner join fetch cotacao.cliente ");
		hql.append("  inner join fetch cotacao.cotacaoEndereco cotacaoEndereco ");
		hql.append("  inner join fetch cotacaoEndereco.cep cep ");
		hql.append("  inner join fetch cep.bairro bairro ");
		hql.append("  inner join fetch bairro.cidade cidade ");
		hql.append("  inner join fetch cidade.estado ");
		hql.append("  inner join fetch cotacaoEndereco.enderecoComplemento ");

		hql.append("  where exists (select 1 ");
		hql.append("                  from CotacaoFornecedor cotacaoFornecedor ");
		hql.append("                 where cotacaoFornecedor.cotacao = cotacao ");
		hql.append("                   and cotacaoFornecedor.fornecedor = :fornecedor ");
		hql.append("                   and cotacaoFornecedor.vencedorByUser = 1 )");
		hql.append("    and cotacao.situacaoCotacao = :situacaoCotacao ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("situacaoCotacao", SituacaoCotacaoType.FINALIZADA);
		parameters.put("fornecedor", fornecedor);

		if (cotacaoFilter.getNome() != null && !cotacaoFilter.getNome().isEmpty()) {
			hql.append(" and upper(cotacao.nome) like :nome ");
			parameters.put("nome", "%" + cotacaoFilter.getNome().toUpperCase() + "%");
		}

		if (cotacaoFilter.getPeriodoCadastro() != null) {
			// cadastro
			if (cotacaoFilter.getPeriodoCadastro().getInicio() != null) {
				hql.append(" and cotacao.dataCadastro >= :cadastro_inicial ");
				Date d = cotacaoFilter.getPeriodoCadastro().getInicio();
				d = DataUtil.adicionaHoraData(d, 0, 0, 0);
				parameters.put("cadastro_inicial", d);
			}

			if (cotacaoFilter.getPeriodoCadastro().getFim() != null) {
				hql.append(" and cotacao.dataCadastro <= :cadastro_final ");
				Date d = cotacaoFilter.getPeriodoCadastro().getFim();
				d = DataUtil.adicionaHoraData(d, 23, 59, 59);
				parameters.put("cadastro_final", d);
			}
		}

		if (cotacaoFilter.getPeriodoEnvio() != null) {
			// envio
			if (cotacaoFilter.getPeriodoEnvio().getInicio() != null) {
				hql.append(" and cotacao.dataEnvio >= :envio_inicial ");
				Date d = cotacaoFilter.getPeriodoEnvio().getInicio();
				d = DataUtil.adicionaHoraData(d, 0, 0, 0);
				parameters.put("envio_inicial", d);
			}

			if (cotacaoFilter.getPeriodoEnvio().getFim() != null) {
				hql.append(" and cotacao.dataEnvio <= :envio_final ");
				Date d = cotacaoFilter.getPeriodoEnvio().getFim();
				d = DataUtil.adicionaHoraData(d, 23, 59, 59);
				parameters.put("envio_final", d);
			}
		}

		if (cotacaoFilter.getPeriodoRetorno() != null) {

			// limite retorno
			if (cotacaoFilter.getPeriodoRetorno().getInicio() != null) {
				hql.append(" and cotacao.dataLimiteRetorno >= :retorno_inicial ");
				Date d = cotacaoFilter.getPeriodoRetorno().getInicio();
				d = DataUtil.adicionaHoraData(d, 0, 0, 0);
				parameters.put("retorno_inicial", d);
			}

			if (cotacaoFilter.getPeriodoRetorno().getFim() != null) {
				hql.append(" and cotacao.dataLimiteRetorno <= :retorno_final ");
				Date d = cotacaoFilter.getPeriodoRetorno().getFim();
				d = DataUtil.adicionaHoraData(d, 23, 59, 59);
				parameters.put("retorno_final", d);
			}
		}

		Pageable pageable = cotacaoFilter.getPageable();
		if (pageable.getSort() == null) {
			hql.append("  order by cotacao.id desc ");
		} else {
			StringBuilder builder = new StringBuilder();
			String orderBy = "";

			pageable.getSort().forEach(order -> {
				builder.append(" cotacao." + order.getProperty());
				builder.append(" " + order.getDirection().name());
				builder.append(",");
			});

			orderBy = builder.toString();

			if (!orderBy.trim().isEmpty()) {
				orderBy = orderBy.substring(0, orderBy.length() - 1);
				orderBy = " order by " + orderBy;
			}

			hql.append(orderBy);
		}

		Page<Cotacao> retorno = searchPaginated(hql.toString(), pageable, parameters);
		return retorno;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Page<Cotacao> findCotacaoRespostaByParams(CotacaoRespostaFilter cotacaoRespostaFilter, Usuario usuario) throws Exception {
		// Cliente cliente = null;

		if (cotacaoRespostaFilter.getFornecedor() == null) {
			return new PageImpl<>(new ArrayList<>());
		}

		if (usuario.isAdmin()) {
			throw new Exception(messages.get("cotacao.admin.resposta"));
		}

		Pageable pageable = cotacaoRespostaFilter.getPageable();
		StringBuilder hql = new StringBuilder();

		hql.append(" select new br.com.kotar.domain.business.Cotacao( ");
		hql.append(" 		cotacao.id, cotacao.nome, cotacao.dataCadastro, cotacao.dataLimiteRetorno, ");
		hql.append(" 		cotacao.dataEnvio, cotacao.situacaoCotacao, ");
		hql.append(" 		(select case when cotacaoFornecedor.dataRecusa is not null then 1 ");
		hql.append(" 					 when cotacaoFornecedor.enviado = true then 2 ");
		hql.append(" 					 when cotacaoFornecedor.enviado = false and (select count(1) ");
		hql.append("                                                                   from CotacaoItemFornecedorValor cotacaoItemFornecedorValor ");
		hql.append(
				"                                                                  inner join cotacaoItemFornecedorValor.cotacaoItemFornecedor cotacaoItemFornecedor ");
		hql.append(
				" 				    		                                      where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor) > 0 then 3 ");
		hql.append(" 				     else ");
		hql.append("                        case when cotacao.dataLimiteRetorno > current_timestamp() then 4 else 5 end ");
		hql.append(" 				end ");
		hql.append(" 		   from CotacaoFornecedor cotacaoFornecedor ");
		hql.append(" 		  where cotacaoFornecedor.cotacao = cotacao ");
		hql.append(" 		    and cotacaoFornecedor.fornecedor = :fornecedor )");
		hql.append(" 		) ");

		// hql.append(" select cotacao ");
		hql.append("   from Cotacao cotacao ");
		// hql.append(" inner join cotacao.cliente ");
		hql.append("  where 1 = 1 ");
		hql.append("    and cotacao.situacaoCotacao != :situacaoCancelada ");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("situacaoCancelada", SituacaoCotacaoType.CANCELADA);

		// hql.append(" and cotacao.situacaoCotacao = :situacaoCotacao ");
		// parameters.put("situacaoCotacao", SituacaoCotacaoType.ENVIADA);

		if (cotacaoRespostaFilter != null) {

			hql.append("  and exists (select 1 ");
			hql.append("                from CotacaoFornecedor cotacaoFornecedor ");
			hql.append("               where cotacaoFornecedor.cotacao = cotacao ");
			hql.append("                 and cotacaoFornecedor.fornecedor = :fornecedor ) ");
			parameters.put("fornecedor", cotacaoRespostaFilter.getFornecedor());
			// hql.append(" and cotacao.dataLimiteRetorno > current_timestamp()
			// ");

			if (cotacaoRespostaFilter.getNome() != null && !cotacaoRespostaFilter.getNome().isEmpty()) {
				hql.append(" and upper(cotacao.nome) like :nome ");
				parameters.put("nome", "%" + cotacaoRespostaFilter.getNome().toUpperCase() + "%");
			}

			// if (cliente != null) {
			// hql.append(" and cotacao.cliente = :cliente ");
			// parameters.put("cliente", cliente);
			// }

			if (cotacaoRespostaFilter.getPeriodoCadastro() != null) {
				// cadastro
				if (cotacaoRespostaFilter.getPeriodoCadastro().getInicio() != null) {
					hql.append(" and cotacao.dataCadastro >= :cadastro_inicial ");
					Date d = cotacaoRespostaFilter.getPeriodoCadastro().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("cadastro_inicial", d);
				}

				if (cotacaoRespostaFilter.getPeriodoCadastro().getFim() != null) {
					hql.append(" and cotacao.dataCadastro <= :cadastro_final ");
					Date d = cotacaoRespostaFilter.getPeriodoCadastro().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("cadastro_final", d);
				}
			}

			if (cotacaoRespostaFilter.getPeriodoEnvio() != null) {
				// envio
				if (cotacaoRespostaFilter.getPeriodoEnvio().getInicio() != null) {
					hql.append(" and cotacao.dataEnvio >= :envio_inicial ");
					Date d = cotacaoRespostaFilter.getPeriodoEnvio().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("envio_inicial", d);
				}

				if (cotacaoRespostaFilter.getPeriodoEnvio().getFim() != null) {
					hql.append(" and cotacao.dataEnvio <= :envio_final ");
					Date d = cotacaoRespostaFilter.getPeriodoEnvio().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("envio_final", d);
				}
			}

			if (cotacaoRespostaFilter.getPeriodoRetorno() != null) {

				// limite retorno
				if (cotacaoRespostaFilter.getPeriodoRetorno().getInicio() != null) {
					hql.append(" and cotacao.dataLimiteRetorno >= :retorno_inicial ");
					Date d = cotacaoRespostaFilter.getPeriodoRetorno().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("retorno_inicial", d);
				}

				if (cotacaoRespostaFilter.getPeriodoRetorno().getFim() != null) {
					hql.append(" and cotacao.dataLimiteRetorno <= :retorno_final ");
					Date d = cotacaoRespostaFilter.getPeriodoRetorno().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("retorno_final", d);
				}
			}

			if (cotacaoRespostaFilter.getSituacaoCotacao() != null) {

				if (cotacaoRespostaFilter.getSituacaoCotacao() != null) {
					hql.append(" and cotacao.situacaoCotacao = :situacaoCotacao ");
					parameters.put("situacaoCotacao", cotacaoRespostaFilter.getSituacaoCotacao());
				}
			}

			if (cotacaoRespostaFilter.getSituacaoRespostaFornecedor() != null) {

				if (cotacaoRespostaFilter.getSituacaoRespostaFornecedor().equals(SituacaoRespostaFornecedorType.RECUSADA)) {
					hql.append("  and exists (select 1 ");
					hql.append("                from CotacaoFornecedor cotacaoFornecedor ");
					hql.append("               where cotacaoFornecedor.cotacao = cotacao ");
					hql.append("                 and cotacaoFornecedor.fornecedor = :fornecedor ");
					hql.append("                 and cotacaoFornecedor.dataRecusa is not null) ");
				} else if (cotacaoRespostaFilter.getSituacaoRespostaFornecedor().equals(SituacaoRespostaFornecedorType.RESPONDIDA)) {

					hql.append("  and exists (select 1 ");
					hql.append("                from CotacaoFornecedor cotacaoFornecedor ");
					hql.append("               where cotacaoFornecedor.cotacao = cotacao ");
					hql.append("                 and cotacaoFornecedor.fornecedor = :fornecedor ");
					hql.append("                 and cotacaoFornecedor.enviado = true ");
					hql.append("                 and cotacaoFornecedor.dataRecusa is null ) ");
				} else if (cotacaoRespostaFilter.getSituacaoRespostaFornecedor().equals(SituacaoRespostaFornecedorType.RESPONDENDO)) {
					hql.append("  and cotacao.situacaoCotacao = :situacaoCotacaoRespondendo ");
					parameters.put("situacaoCotacaoRespondendo", SituacaoCotacaoType.ENVIADA);

					hql.append("  and not exists (select 1 ");
					hql.append("                    from CotacaoFornecedor cotacaoFornecedor ");
					hql.append("                   where cotacaoFornecedor.cotacao = cotacao ");
					hql.append("                     and cotacaoFornecedor.fornecedor = :fornecedor ");
					hql.append("                     and cotacaoFornecedor.enviado = true) ");

					hql.append("  and exists (select 1 ");
					hql.append("                from CotacaoItemFornecedorValor cotacaoItemFornecedorValor ");
					hql.append("               inner join cotacaoItemFornecedorValor.cotacaoItemFornecedor cotacaoItemFornecedor ");
					hql.append("               inner join cotacaoItemFornecedor.cotacaoFornecedor cotacaoFornecedor ");
					hql.append("               where cotacaoFornecedor.cotacao = cotacao ) ");
					hql.append("  and cotacao.dataLimiteRetorno > current_timestamp() ");
				} else if (cotacaoRespostaFilter.getSituacaoRespostaFornecedor().equals(SituacaoRespostaFornecedorType.AGUARDANDO)) {
					hql.append("  and cotacao.situacaoCotacao = :situacaoCotacaoAguardando");
					parameters.put("situacaoCotacaoAguardando", SituacaoCotacaoType.ENVIADA);

					hql.append("  and not exists (select 1 ");
					hql.append("                from CotacaoFornecedor cotacaoFornecedor ");
					hql.append("               where cotacaoFornecedor.cotacao = cotacao ");
					hql.append("                 and cotacaoFornecedor.fornecedor = :fornecedor ");
					hql.append("                 and cotacaoFornecedor.dataRecusa is not null) ");

					hql.append("  and not exists (select 1 ");
					hql.append("                    from CotacaoFornecedor cotacaoFornecedor ");
					hql.append("                   where cotacaoFornecedor.cotacao = cotacao ");
					hql.append("                     and cotacaoFornecedor.fornecedor = :fornecedor ");
					hql.append("                     and cotacaoFornecedor.enviado = true) ");

					hql.append("  and not exists (select 1 ");
					hql.append("                    from CotacaoItemFornecedorValor cotacaoItemFornecedorValor ");
					hql.append("                   inner join cotacaoItemFornecedorValor.cotacaoItemFornecedor cotacaoItemFornecedor ");
					hql.append("                   inner join cotacaoItemFornecedor.cotacaoFornecedor cotacaoFornecedor ");
					hql.append("                   where cotacaoFornecedor.cotacao = cotacao ) ");

					hql.append("  and cotacao.dataLimiteRetorno > current_timestamp() ");
				} else if (cotacaoRespostaFilter.getSituacaoRespostaFornecedor().equals(SituacaoRespostaFornecedorType.NAO_RSPONDIDA)) {
					hql.append("  and cotacao.dataLimiteRetorno < current_timestamp() ");
					hql.append("  and not exists (select 1 ");
					hql.append("                    from CotacaoItemFornecedorValor cotacaoItemFornecedorValor ");
					hql.append("                   inner join cotacaoItemFornecedorValor.cotacaoItemFornecedor cotacaoItemFornecedor ");
					hql.append("                   inner join cotacaoItemFornecedor.cotacaoFornecedor cotacaoFornecedor ");
					hql.append("                   where cotacaoFornecedor.cotacao = cotacao ) ");

					hql.append("  and not exists (select 1 ");
					hql.append("                from CotacaoFornecedor cotacaoFornecedor ");
					hql.append("               where cotacaoFornecedor.cotacao = cotacao ");
					hql.append("                 and cotacaoFornecedor.fornecedor = :fornecedor ");
					hql.append("                 and cotacaoFornecedor.dataRecusa is not null) ");
				}
			}
		}

		if (pageable.getSort() == null) {
			hql.append("  order by cotacao.id desc ");
		} else {
			StringBuilder builder = new StringBuilder();
			String orderBy = "";

			pageable.getSort().forEach(order -> {
				builder.append(" cotacao." + order.getProperty());
				builder.append(" " + order.getDirection().name());
				builder.append(",");
			});

			orderBy = builder.toString();

			if (!orderBy.trim().isEmpty()) {
				orderBy = orderBy.substring(0, orderBy.length() - 1);
				orderBy = " order by " + orderBy;
			}

			hql.append(orderBy);
		}

		Page<Cotacao> retorno = searchPaginated(hql.toString(), pageable, parameters);

		retorno.forEach(_cot -> {
			String _sit = getSituacaoRespostaFornecedor(_cot.getCodeSituacaoResposta());
			_cot.setSituacaoRespostaFornecedor(_sit);
		});

		return retorno;
	}

	private String getSituacaoRespostaFornecedor(Integer codeSituacaoResposta) {
		if (codeSituacaoResposta != null) {
			SituacaoRespostaFornecedorType situacaoRespostaFornecedorType = SituacaoRespostaFornecedorType.get(codeSituacaoResposta);
			if (situacaoRespostaFornecedorType != null) {
				return messages.get(situacaoRespostaFornecedorType.getMessageKey());
			}
		}

		return null;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Page<Cotacao> findCotacaoByParams(CotacaoFilter cotacaoFilter, Cliente cliente) throws Exception {

		Pageable pageable = cotacaoFilter.getPageable();
		StringBuilder hql = new StringBuilder();

		hql.append(" select new br.com.kotar.domain.business.Cotacao( ");
		hql.append(" 			cotacao.id, cotacao.nome, cotacao.dataCadastro, cotacao.dataLimiteRetorno, cotacao.dataEnvio, ");
		hql.append("			cotacao.situacaoCotacao,  ");
		hql.append("			( select count(cotacaoFornecedor.id) ");
		hql.append("			    from CotacaoFornecedor cotacaoFornecedor ");
		hql.append("			   where cotacaoFornecedor.cotacao = cotacao ");
		hql.append("			     and cotacaoFornecedor.enviado = 1 ");
		hql.append("			     and cotacaoFornecedor.dataRecusa is null  ");

		hql.append("			     and (select count(1) ");
		hql.append("			            from CotacaoItemFornecedor cotacaoItemFornecedor ");
		hql.append("			           where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor) = (select count(1)  ");
		hql.append(
				"                                                                                               from CotacaoItemFornecedor cotacaoItemFornecedor ");
		hql.append(
				"                                                                                              where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor ");
		hql.append("                                                                                                and exists (select 1 ");
		hql.append(
				"                                                                                                              from CotacaoItemFornecedorValor cotacaoItemFornecedorValor ");
		hql.append( 
				"                                                                                                             where cotacaoItemFornecedorValor.cotacaoItemFornecedor = cotacaoItemFornecedor ) ) ");
		hql.append(" 		   ), ");

		hql.append(" 		   ( select cotacaoFornecedor ");
		hql.append("			    from CotacaoFornecedor cotacaoFornecedor ");
		hql.append(" 		     where cotacaoFornecedor.codigoAprovacaoVencedor is not null ");
		hql.append(" 		        and cotacaoFornecedor.vencedorByUser = 1 ");
		hql.append(" 		        and cotacaoFornecedor.cotacao = cotacao ");
		hql.append(" 		   ) ");
		
		if (cotacaoFilter.getAvaliacoes() != null && cotacaoFilter.getAvaliacoes() == true) {
			hql.append(" 		   ,( select avaliacao ");
			hql.append("			    from Avaliacao avaliacao, CotacaoFornecedor cotacaoFornecedor, Fornecedor fornecedor ");
			hql.append(" 		     where avaliacao.cotacaoFornecedor = cotacaoFornecedor ");
			hql.append(" 		        and cotacaoFornecedor.fornecedor = fornecedor ");
			hql.append(" 		        and cotacaoFornecedor.cotacao = cotacao ");		
			hql.append(" 		   ) "); 
			
			hql.append(" 		   ,( select fornecedor ");
			hql.append("			    from CotacaoFornecedor cotacaoFornecedor, Fornecedor fornecedor ");
			hql.append(" 		     where cotacaoFornecedor.codigoAprovacaoVencedor is not null ");
			hql.append(" 		        and cotacaoFornecedor.fornecedor = fornecedor ");
			hql.append(" 		        and cotacaoFornecedor.vencedorByUser = 1 ");
			hql.append(" 		        and cotacaoFornecedor.cotacao = cotacao ");		
			hql.append(" 		   ) "); 
		}

		hql.append(" ) ");
		hql.append("   from Cotacao cotacao ");
		hql.append("  where 1 = 1 ");

		// hql.append(" select cotacao ");
		// hql.append(" from Cotacao cotacao ");
		// hql.append(" where 1 = 1 ");

		Map<String, Object> parameters = new HashMap<>();
		if (cotacaoFilter != null) {
			if (cotacaoFilter.getNome() != null && !cotacaoFilter.getNome().isEmpty()) {
				hql.append(" and upper(cotacao.nome) like :nome ");
				parameters.put("nome", "%" + cotacaoFilter.getNome().toUpperCase() + "%");
			}

			if (cliente != null) {
				hql.append(" and cotacao.cliente.id = :cliente ");
				parameters.put("cliente", cliente.getId());
			}

			if (cotacaoFilter.getPeriodoCadastro() != null) {

				// cadastro
				if (cotacaoFilter.getPeriodoCadastro().getInicio() != null) {
					hql.append(" and cotacao.dataCadastro >= :cadastro_inicial ");
					Date d = cotacaoFilter.getPeriodoCadastro().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("cadastro_inicial", d);
				}

				if (cotacaoFilter.getPeriodoCadastro().getFim() != null) {
					hql.append(" and cotacao.dataCadastro <= :cadastro_final ");
					Date d = cotacaoFilter.getPeriodoCadastro().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("cadastro_final", d);
				}

				// envio
				if (cotacaoFilter.getPeriodoEnvio().getInicio() != null) {
					hql.append(" and cotacao.dataEnvio >= :envio_inicial ");
					Date d = cotacaoFilter.getPeriodoEnvio().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("envio_inicial", d);
				}

				if (cotacaoFilter.getPeriodoEnvio().getFim() != null) {
					hql.append(" and cotacao.dataEnvio <= :envio_final ");
					Date d = cotacaoFilter.getPeriodoEnvio().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("envio_final", d);
				}

				// limite retorno
				if (cotacaoFilter.getPeriodoRetorno().getInicio() != null) {
					hql.append(" and cotacao.dataLimiteRetorno >= :retorno_inicial ");
					Date d = cotacaoFilter.getPeriodoRetorno().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("retorno_inicial", d);
				}

				if (cotacaoFilter.getPeriodoRetorno().getFim() != null) {
					hql.append(" and cotacao.dataLimiteRetorno <= :retorno_final ");
					Date d = cotacaoFilter.getPeriodoRetorno().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("retorno_final", d);
				}

				if (cotacaoFilter.getSituacaoCotacao() != null) {
					hql.append(" and cotacao.situacaoCotacao = :situacaoCotacao ");
					parameters.put("situacaoCotacao", cotacaoFilter.getSituacaoCotacao());
				}
			}

			if (cotacaoFilter.getNegociosFechados() != null && cotacaoFilter.getNegociosFechados() == true) {
				hql.append(" 	and	   ( select cotacaoFornecedor ");
				hql.append("			    from CotacaoFornecedor cotacaoFornecedor ");
				hql.append(" 		     where cotacaoFornecedor.codigoAprovacaoVencedor is not null ");
				hql.append(" 		        and cotacaoFornecedor.vencedorByUser = 1 ");
				hql.append(" 		        and cotacaoFornecedor.cotacao = cotacao ");
				hql.append(" 		   ) is not null ");
			}
			
			if (cotacaoFilter.getSomentePendenteDeAvaliacao() != null && cotacaoFilter.getSomentePendenteDeAvaliacao() == true) {
				hql.append("	and	   ( select avaliacao ");
				hql.append("			   from Avaliacao avaliacao, CotacaoFornecedor cotacaoFornecedor ");
				hql.append(" 		     where avaliacao.cotacaoFornecedor = cotacaoFornecedor ");
				hql.append(" 		       and cotacaoFornecedor.cotacao = cotacao ");		
				hql.append(" 		   ) is null ");			
				hql.append(" 		   and  (cotacao.dataLembreteAvaliacao is null or cotacao.dataLembreteAvaliacao < current_timestamp() + 2*86400) ");
			}
			else if (cotacaoFilter.getPendenciasDeAvaliacao() != null && cotacaoFilter.getPendenciasDeAvaliacao() == true) {
				hql.append(" 		and  ( ");
				hql.append("			   ( select avaliacao ");
				hql.append("			       from Avaliacao avaliacao, CotacaoFornecedor cotacaoFornecedor ");
				hql.append(" 		          where avaliacao.cotacaoFornecedor = cotacaoFornecedor ");
				hql.append(" 		          and cotacaoFornecedor.cotacao = cotacao ");		
				hql.append(" 		        ) is not null ");
				hql.append("				or	( select avaliacao ");
				hql.append("			           from Avaliacao avaliacao, CotacaoFornecedor cotacaoFornecedor ");
				hql.append(" 		              where avaliacao.cotacaoFornecedor = cotacaoFornecedor ");
				hql.append(" 		               and cotacaoFornecedor.cotacao = cotacao ");		
				hql.append(" 		            ) is null ");			
				hql.append(" 		            and  (cotacao.dataLembreteAvaliacao is null or cotacao.dataLembreteAvaliacao < current_timestamp() + 2*86400) ");
				hql.append(" 		     ) ");
			}
		}

		if (pageable.getSort() == null) {
			hql.append("  order by cotacao.id desc ");
		} else {
			StringBuilder builder = new StringBuilder();
			String orderBy = "";

			pageable.getSort().forEach(order -> {
				builder.append(" cotacao." + order.getProperty());
				builder.append(" " + order.getDirection().name());
				builder.append(",");
			});

			orderBy = builder.toString();

			if (!orderBy.trim().isEmpty()) {
				orderBy = orderBy.substring(0, orderBy.length() - 1);
				orderBy = " order by " + orderBy;
			}

			hql.append(orderBy);
		}

		Page<Cotacao> retorno = searchPaginated(hql.toString(), pageable, parameters);

		retorno.forEach(_cot -> {

			String _message = messages.get(_cot.getSituacaoCotacao().getMessageKey());
			_cot.setSituacaoStr(_message);

		});

		return retorno;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Page<Cotacao> findConsultaRespostaByParams(ConsultaRespostaFilter consultaRespostaFilter, Cliente cliente) throws Exception {

		Pageable pageable = consultaRespostaFilter.getPageable();
		StringBuilder hql = new StringBuilder();

		hql.append(" select new br.com.kotar.domain.business.Cotacao( ");
		hql.append(" 			cotacao.id, cotacao.nome, cotacao.dataCadastro, cotacao.dataLimiteRetorno, cotacao.dataEnvio, ");
		hql.append("			cotacao.situacaoCotacao,  ");
		hql.append("			( select count(cotacaoFornecedor.id) ");
		hql.append("			    from CotacaoFornecedor cotacaoFornecedor ");
		hql.append("			   where cotacaoFornecedor.cotacao = cotacao ");
		hql.append("			     and cotacaoFornecedor.enviado = 1 ");
		hql.append("			     and cotacaoFornecedor.dataRecusa is null  ");

		hql.append("			     and (select count(1) ");
		hql.append("			            from CotacaoItemFornecedor cotacaoItemFornecedor ");
		hql.append("			           where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor) = (select count(1)  ");
		hql.append("                                                                                               from CotacaoItemFornecedor cotacaoItemFornecedor ");
		hql.append("                                                                                              where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor ");
		hql.append("                                                                                                and exists (select 1 ");
		hql.append("                                                                                                              from CotacaoItemFornecedorValor cotacaoItemFornecedorValor ");
		hql.append("                                                                                                             where cotacaoItemFornecedorValor.cotacaoItemFornecedor = cotacaoItemFornecedor ) ) ");
		hql.append(" 		   ), ");
		hql.append(" 		   ( select cotacaoFornecedor ");
		hql.append("			    from CotacaoFornecedor cotacaoFornecedor ");
		hql.append(" 		     where cotacaoFornecedor.codigoAprovacaoVencedor is not null ");
		hql.append(" 		        and cotacaoFornecedor.vencedorByUser = 1 ");
		hql.append(" 		        and cotacaoFornecedor.cotacao = cotacao ");
		hql.append(" 		   ) ");
		hql.append(" ) ");
		hql.append("   from Cotacao cotacao ");
		hql.append("  where 1 = 1 ");
		// hql.append(" and cotacao.situacaoCotacao in ( :situacoes ) ");

		hql.append("   and ( select count(cotacaoFornecedor.id) ");
		hql.append("		   from CotacaoFornecedor cotacaoFornecedor ");
		hql.append("		  where cotacaoFornecedor.cotacao = cotacao ");
		hql.append("			and cotacaoFornecedor.enviado = 1 ");
		hql.append("			and cotacaoFornecedor.dataRecusa is null  ");

		hql.append("			and (select count(1) ");
		hql.append("			       from CotacaoItemFornecedor cotacaoItemFornecedor ");
		hql.append("			      where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor) = (select count(1)  ");
		hql.append("                                                                                          from CotacaoItemFornecedor cotacaoItemFornecedor ");
		hql.append("                                                                                         where cotacaoItemFornecedor.cotacaoFornecedor = cotacaoFornecedor ");
		hql.append("                                                                                         and exists (select 1 ");
		hql.append("                                                                                                      from CotacaoItemFornecedorValor cotacaoItemFornecedorValor ");
		hql.append("                                                                                                     where cotacaoItemFornecedorValor.cotacaoItemFornecedor = cotacaoItemFornecedor ) ) ");
		hql.append(" 	   ) > 0 ");

		Map<String, Object> parameters = new HashMap<>();

		if (consultaRespostaFilter != null) {
			if (consultaRespostaFilter.getNome() != null && !consultaRespostaFilter.getNome().isEmpty()) {
				hql.append(" and upper(cotacao.nome) like :nome ");
				parameters.put("nome", "%" + consultaRespostaFilter.getNome().toUpperCase() + "%");
			}

			if (cliente != null) {
				hql.append(" and cotacao.cliente.id = :cliente ");
				parameters.put("cliente", cliente.getId());
			}

			if (consultaRespostaFilter.getPeriodoCadastro() != null) {

				// cadastro
				if (consultaRespostaFilter.getPeriodoCadastro().getInicio() != null) {
					hql.append(" and cotacao.dataCadastro >= :cadastro_inicial ");
					Date d = consultaRespostaFilter.getPeriodoCadastro().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("cadastro_inicial", d);
				}

				if (consultaRespostaFilter.getPeriodoCadastro().getFim() != null) {
					hql.append(" and cotacao.dataCadastro <= :cadastro_final ");
					Date d = consultaRespostaFilter.getPeriodoCadastro().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("cadastro_final", d);
				}

				// envio
				if (consultaRespostaFilter.getPeriodoEnvio().getInicio() != null) {
					hql.append(" and cotacao.dataEnvio >= :envio_inicial ");
					Date d = consultaRespostaFilter.getPeriodoEnvio().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("envio_inicial", d);
				}

				if (consultaRespostaFilter.getPeriodoEnvio().getFim() != null) {
					hql.append(" and cotacao.dataEnvio <= :envio_final ");
					Date d = consultaRespostaFilter.getPeriodoEnvio().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("envio_final", d);
				}

				// limite retorno
				if (consultaRespostaFilter.getPeriodoRetorno().getInicio() != null) {
					hql.append(" and cotacao.dataLimiteRetorno >= :retorno_inicial ");
					Date d = consultaRespostaFilter.getPeriodoRetorno().getInicio();
					d = DataUtil.adicionaHoraData(d, 0, 0, 0);
					parameters.put("retorno_inicial", d);
				}

				if (consultaRespostaFilter.getPeriodoRetorno().getFim() != null) {
					hql.append(" and cotacao.dataLimiteRetorno <= :retorno_final ");
					Date d = consultaRespostaFilter.getPeriodoRetorno().getFim();
					d = DataUtil.adicionaHoraData(d, 23, 59, 59);
					parameters.put("retorno_final", d);
				}
			}
		}

		if (pageable.getSort() == null) {
			hql.append("  order by cotacao.id desc ");
		} else {
			StringBuilder builder = new StringBuilder();
			String orderBy = "";

			pageable.getSort().forEach(order -> {
				builder.append(" cotacao." + order.getProperty());
				builder.append(" " + order.getDirection().name());
				builder.append(",");
			});

			orderBy = builder.toString();

			if (!orderBy.trim().isEmpty()) {
				orderBy = orderBy.substring(0, orderBy.length() - 1);
				orderBy = " order by " + orderBy;
			}

			hql.append(orderBy);
		}

		Page<Cotacao> retorno = searchPaginated(hql.toString(), pageable, parameters);
		retorno.forEach(_cot -> {
			String _message = messages.get(_cot.getSituacaoCotacao().getMessageKey());
			_cot.setSituacaoStr(_message);
		});

		return retorno;
	}

	@Override
	public List<Cotacao> findCotacaoExpirada(List<SituacaoCotacaoType> situacaoCotacao) {
		return cotacaoRepository.findCotacaoExpirada(situacaoCotacao);
	}

	public List<MinhasCotacoesVencedorasFornecedorHelper> findMinhasCotacoesVencedorasFornecedorPorMes(Fornecedor fornecedor) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append(" select year(c.data_finalizado) as ano, month(c.data_finalizado) as mes, ");
		builder.append("        sum(case when cf.data_recusa is null and cf.vencedor_by_user = 1 then 1 else 0 end ) as vencedor, ");
		builder.append("        count(1) as total ");
		builder.append("   from cotacao_fornecedor cf ");
		builder.append("  inner join cotacao c on c.cotacao_id = cf.cotacao_id ");
		builder.append("  where cf.fornecedor_id = :fornecedor_id ");
		builder.append("    and data_finalizado > data_finalizado - INTERVAL 365 DAY ");
		builder.append("  group by year(c.data_finalizado), month(c.data_finalizado) ");
		builder.append("  order by year(c.data_finalizado), month(c.data_finalizado) ");

		Query query = em.createNativeQuery(builder.toString());
		query.setParameter("fornecedor_id", fornecedor.getId());

		List<MinhasCotacoesVencedorasFornecedorHelper> _ret = new ArrayList<>();

		List<?> list = query.getResultList();
		for (Object row : list) {
			Object[] cols = (Object[]) row;
			Integer _mes = (Integer) cols[0];
			Integer _ano = (Integer) cols[1];
			Long _quantidade = ((BigDecimal) cols[2]).longValue();
			Long _total = ((BigInteger) cols[3]).longValue();

			MinhasCotacoesVencedorasFornecedorHelper _m = new MinhasCotacoesVencedorasFornecedorHelper();
			_m.setAno(_ano);
			_m.setMes(_mes);
			_m.setQuantidade(_quantidade);
			_m.setTotal(_total);

			_ret.add(_m);
		}

		if (_ret.isEmpty()) {
			MinhasCotacoesVencedorasFornecedorHelper _m = new MinhasCotacoesVencedorasFornecedorHelper();
			_m.setAno(Calendar.getInstance().get(Calendar.YEAR));
			_m.setMes(Calendar.getInstance().get(Calendar.MONTH) + 1);
			_m.setQuantidade(0l);
			_m.setTotal(0l);

			_ret.add(_m);

		}

		return _ret;

	}

	public List<EstatisticasCotacaoFornecedorHelper> findEstatisticasCotacaoFornecedor(Fornecedor fornecedor) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append(" select ");
		builder.append("       sum(case when data_recusa is not null then 1 else 0 end) recusado, ");
		builder.append("       sum(case when c.situacao_cotacao = 1 and cf.enviado = 0 then 1 else 0 end) pendente, ");
		builder.append("       sum(case when c.situacao_cotacao in (2,3) and data_recusa is null and cf.vencedor_by_user = 0 then 1 else 0 end) perdedor, ");
		builder.append("       sum(case when c.situacao_cotacao in (2,3) and data_recusa is null and cf.vencedor_by_user = 1 then 1 else 0 end) vencedor, ");
		builder.append("       sum(1) total ");

		builder.append("  from cotacao_fornecedor cf ");
		builder.append(" inner join cotacao c on c.cotacao_id = cf.cotacao_id ");
		builder.append(" where fornecedor_id = :fornecedor_id ");
		builder.append("   and situacao_cotacao in (1,2,3) ");

		Query query = em.createNativeQuery(builder.toString());
		query.setParameter("fornecedor_id", fornecedor.getId());

		List<EstatisticasCotacaoFornecedorHelper> _ret = new ArrayList<>();
		List<?> list = query.getResultList();
		for (Object row : list) {
			Object[] cols = (Object[]) row;

			Long _recusado = ((BigDecimal) cols[0]).longValue();
			Long _pendente = ((BigDecimal) cols[1]).longValue();
			Long _perdedor = ((BigDecimal) cols[2]).longValue();
			Long _vencedor = ((BigDecimal) cols[3]).longValue();
			Long _total = ((BigDecimal) cols[4]).longValue();

			EstatisticasCotacaoFornecedorHelper _e = new EstatisticasCotacaoFornecedorHelper();
			_e.setPendente(_pendente);
			_e.setPerdedor(_perdedor);
			_e.setRecusado(_recusado);
			_e.setTotal(_total);
			_e.setVencedor(_vencedor);

			_ret.add(_e);
		}

		return _ret;
	}

	public List<EstatisticasCotacaoClienteHelper> findEstatisticasCotacaoCliente(Cliente cliente) throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append(" SELECT ");
		builder.append(" 		sum(1) total,  ");
		builder.append(" 		sum(case when c.situacao_cotacao = 0 then 1 else 0 end) pendente, ");
		builder.append(" 		sum(case when c.situacao_cotacao in ( 1,2,3 ) and coalesce(tbl.quantidade, 0) = 0 then 1 else 0 end) enviada_sem_resposta, ");
		builder.append(" 		sum(case when c.situacao_cotacao in ( 1,2,3 ) and coalesce(tbl.quantidade, 0) > 0 then 1 else 0 end) enviada_com_resposta, ");
		builder.append(" 		sum(case when c.situacao_cotacao = 4 then 1 else 0 end) cancelado ");

		builder.append("   FROM cotacao c ");
		builder.append("   left join (select cf.cotacao_id, count(1) quantidade ");
		builder.append("                from cotacao_fornecedor cf ");
		builder.append("               where cf.enviado = 1 ");
		builder.append("               group by cf.cotacao_id) tbl on tbl.cotacao_id = c.cotacao_id ");

		builder.append(" where c.cliente_id = :cliente_id ");

		Query query = em.createNativeQuery(builder.toString());
		query.setParameter("cliente_id", cliente.getId());

		List<EstatisticasCotacaoClienteHelper> _ret = new ArrayList<>();
		List<?> list = query.getResultList();
		for (Object row : list) {
			Object[] cols = (Object[]) row;

			Long _total = ((BigDecimal) cols[0]).longValue();
			Long _pendente = ((BigDecimal) cols[1]).longValue();
			Long _enviadoSemResposta = ((BigDecimal) cols[2]).longValue();
			Long _enviadoComResposta = ((BigDecimal) cols[3]).longValue();
			Long _cancelado = ((BigDecimal) cols[4]).longValue();

			EstatisticasCotacaoClienteHelper _e = new EstatisticasCotacaoClienteHelper();
			_e.setPendente(_pendente);
			_e.setEnviadoSemResposta(_enviadoSemResposta);
			_e.setEnviadoComResposta(_enviadoComResposta);
			_e.setCancelado(_cancelado);
			_e.setTotal(_total);

			_ret.add(_e);
		}

		return _ret;
	}
	
//	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeral(Cliente cliente) throws Exception {
//		StringBuilder sql = new StringBuilder();
//		sql.append(" select round(sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end),2) vencedor, ");
//		sql.append("        round(sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end),2) maior, ");
//	       
//		sql.append("        round( case when sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end) > 0 and "); 
//		sql.append("                         sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) > 0 then ");
//	       
//		sql.append("                  ((sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) /  ");
//		sql.append("                    sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end)) - 1) * 100 ");
//		sql.append("               else 0 end, 2) percentual ");      
//		sql.append("   from cotacao c ");
//		sql.append("  inner join ( select cf.cotacao_id, cf.cotacao_fornecedor_id, tbl.total, cf.vencedor_by_user "); 
//		sql.append("                 from cotacao_fornecedor cf  ");
//		sql.append("                inner join (select cif.cotacao_fornecedor_id, ");
//		sql.append("                                   sum(ci.quantidade * cifv.unitario) total ");
//		sql.append("                              from cotacao_item_fornecedor cif ");
//		sql.append("                             inner join cotacao_item ci on ci.cotacao_item_id = cif.cotacao_item_id ");                
//		sql.append("                             inner join cotacao_item_fornecedor_valor cifv on cifv.cotacao_item_fornecedor_id = cif.cotacao_item_fornecedor_id ");
//		sql.append("   								                                          and cifv.selecionado = 1 ");
//		sql.append("                             group by cif.cotacao_fornecedor_id) tbl on tbl.cotacao_fornecedor_id = cf.cotacao_fornecedor_id) tbl1 on tbl1.cotacao_id = c.cotacao_id ");
//		sql.append("  where 1 = 1 ");
//		sql.append("    and c.cliente_id = :cliente_id ");  
//
//		Query query = em.createNativeQuery(sql.toString());
//		query.setParameter("cliente_id", cliente.getId());
//		List<?> _rows = query.getResultList();
//		
//		List<EstatisticasGeraisCotacaoHelper> _ret = new ArrayList<>();
//		
//		for (Object _row : _rows){
//			Object[] _cols = (Object[]) _row;
//				
//			BigDecimal _menorValor = (BigDecimal) _cols[0];
//			BigDecimal _maiorValor = (BigDecimal) _cols[1];
//			BigDecimal _percentual = (BigDecimal) _cols[2];
//			
//			EstatisticasGeraisCotacaoHelper _h = new EstatisticasGeraisCotacaoHelper();
//			_h.setMaiorValor(_maiorValor);
//			_h.setMenorValor(_menorValor);
//			_h.setPercentual(_percentual);
//			
//			_ret.add(_h);
//		}
//		
//		return _ret;
//	}
//	
//	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeralAno(Cliente cliente) throws Exception {
//		StringBuilder sql = new StringBuilder();
//		
//		sql.append(" select extract(year from c.data_cadastro) ano, ");
//		sql.append(" 		round(sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end),2) vencedor, ");
//		sql.append(" 		round(sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end),2) maior, ");
//	       
//		sql.append(" 		round( case when sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end) > 0 and "); 
//		sql.append(" 						 sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) > 0 then ");
//	       
//		sql.append(" 				((sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) / "); 
//		sql.append(" 			      sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end)) - 1) * 100 ");
//		sql.append(" 		       else 0 end, 2) percentual  ");     
//		sql.append("   from cotacao c ");
//		sql.append("  inner join ( select cf.cotacao_id, cf.cotacao_fornecedor_id, tbl.total, cf.vencedor_by_user "); 
//		sql.append("                 from cotacao_fornecedor cf  ");
//		sql.append("                inner join (select cif.cotacao_fornecedor_id, ");
//		sql.append("                                   sum(ci.quantidade * cifv.unitario) total ");
//		sql.append("                              from cotacao_item_fornecedor cif ");
//		sql.append("                             inner join cotacao_item ci on ci.cotacao_item_id = cif.cotacao_item_id ");                
//		sql.append("                             inner join cotacao_item_fornecedor_valor cifv on cifv.cotacao_item_fornecedor_id = cif.cotacao_item_fornecedor_id ");
//		sql.append("                             											  and cifv.selecionado = 1 ");
//		sql.append("						     group by cif.cotacao_fornecedor_id) tbl on tbl.cotacao_fornecedor_id = cf.cotacao_fornecedor_id) tbl1 on tbl1.cotacao_id = c.cotacao_id ");
//		sql.append(" where 1 = 1 ");
//		sql.append("   and c.cliente_id = :cliente_id ");  
//		sql.append(" group by extract(year from c.data_cadastro) ");
//		sql.append(" order by extract(year from c.data_cadastro) ");
//		
//		Query query = em.createNativeQuery(sql.toString());
//		query.setParameter("cliente_id", cliente.getId());
//		List<?> _rows = query.getResultList();
//		
//		List<EstatisticasGeraisCotacaoHelper> _ret = new ArrayList<>();
//		
//		for (Object _row : _rows){
//			Object[] _cols = (Object[]) _row;
//			
//			Integer _ano = (Integer) _cols[0];
//			BigDecimal _menorValor = (BigDecimal) _cols[1];
//			BigDecimal _maiorValor = (BigDecimal) _cols[2];
//			BigDecimal _percentual = (BigDecimal) _cols[3];
//			
//			EstatisticasGeraisCotacaoHelper _h = new EstatisticasGeraisCotacaoHelper();
//			_h.setAno(_ano);
//			_h.setMaiorValor(_maiorValor);
//			_h.setMenorValor(_menorValor);
//			_h.setPercentual(_percentual);
//			
//			_ret.add(_h);
//		}
//		
//		return _ret;
//	}
	
	
}
