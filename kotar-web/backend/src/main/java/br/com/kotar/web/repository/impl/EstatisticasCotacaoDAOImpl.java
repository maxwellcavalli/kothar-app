package br.com.kotar.web.repository.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.repository.impl.BaseCrudRepositoryImpl;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.helper.EstatisticasGeraisCotacaoHelper;
import br.com.kotar.web.repository.EstatisticasCotacaoRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class EstatisticasCotacaoDAOImpl extends BaseCrudRepositoryImpl<Cotacao> implements EstatisticasCotacaoRepository {

	//@formatter:off
	@Autowired EstatisticasCotacaoRepository estatisticasCotacaoRepository;
	//@formatter:on

	@Override
	public CrudRepository<Cotacao> getRepository() {
		return estatisticasCotacaoRepository;
	}

	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeral(Cliente cliente) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" select round(sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end),2) vencedor, ");
		sql.append("        round(sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end),2) maior, ");

		sql.append("        round( case when sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end) > 0 and ");
		sql.append("                         sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) > 0 then ");

		sql.append("                  ((sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) /  ");
		sql.append("                    sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end)) - 1) * 100 ");
		sql.append("               else 0 end, 2) percentual ");
		sql.append("   from cotacao c ");
		sql.append("  inner join ( select cf.cotacao_id, cf.cotacao_fornecedor_id, tbl.total, cf.vencedor_by_user ");
		sql.append("                 from cotacao_fornecedor cf  ");
		sql.append("                inner join (select cif.cotacao_fornecedor_id, ");
		sql.append("                                   sum(ci.quantidade * cifv.unitario) total ");
		sql.append("                              from cotacao_item_fornecedor cif ");
		sql.append("                             inner join cotacao_item ci on ci.cotacao_item_id = cif.cotacao_item_id ");
		sql.append(
				"                             inner join cotacao_item_fornecedor_valor cifv on cifv.cotacao_item_fornecedor_id = cif.cotacao_item_fornecedor_id ");
		sql.append("   								                                          and cifv.selecionado = 1 ");
		sql.append(
				"                             group by cif.cotacao_fornecedor_id) tbl on tbl.cotacao_fornecedor_id = cf.cotacao_fornecedor_id) tbl1 on tbl1.cotacao_id = c.cotacao_id ");
		sql.append("  where 1 = 1 ");
		sql.append("    and c.cliente_id = :cliente_id ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("cliente_id", cliente.getId());
		List<?> _rows = query.getResultList();

		List<EstatisticasGeraisCotacaoHelper> _ret = new ArrayList<>();

		for (Object _row : _rows) {
			Object[] _cols = (Object[]) _row;

			BigDecimal _menorValor = (BigDecimal) _cols[0];
			BigDecimal _maiorValor = (BigDecimal) _cols[1];
			BigDecimal _percentual = (BigDecimal) _cols[2];

			EstatisticasGeraisCotacaoHelper _h = new EstatisticasGeraisCotacaoHelper();
			_h.setMaiorValor(_maiorValor);
			_h.setMenorValor(_menorValor);
			_h.setPercentual(_percentual);

			_ret.add(_h);
		}

		return _ret;
	}

	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeralAno(Cliente cliente) throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" select extract(year from c.data_cadastro) ano, ");
		sql.append(" 		round(sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end),2) vencedor, ");
		sql.append(" 		round(sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end),2) maior, ");

		sql.append(" 		round( case when sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end) > 0 and ");
		sql.append(" 						 sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) > 0 then ");

		sql.append(" 				((sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) / ");
		sql.append(" 			      sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end)) - 1) * 100 ");
		sql.append(" 		       else 0 end, 2) percentual  ");
		sql.append("   from cotacao c ");
		sql.append("  inner join ( select cf.cotacao_id, cf.cotacao_fornecedor_id, tbl.total, cf.vencedor_by_user ");
		sql.append("                 from cotacao_fornecedor cf  ");
		sql.append("                inner join (select cif.cotacao_fornecedor_id, ");
		sql.append("                                   sum(ci.quantidade * cifv.unitario) total ");
		sql.append("                              from cotacao_item_fornecedor cif ");
		sql.append("                             inner join cotacao_item ci on ci.cotacao_item_id = cif.cotacao_item_id ");
		sql.append(
				"                             inner join cotacao_item_fornecedor_valor cifv on cifv.cotacao_item_fornecedor_id = cif.cotacao_item_fornecedor_id ");
		sql.append("                             											  and cifv.selecionado = 1 ");
		sql.append(
				"						     group by cif.cotacao_fornecedor_id) tbl on tbl.cotacao_fornecedor_id = cf.cotacao_fornecedor_id) tbl1 on tbl1.cotacao_id = c.cotacao_id ");
		sql.append(" where 1 = 1 ");
		sql.append("   and c.cliente_id = :cliente_id ");
		sql.append(" group by extract(year from c.data_cadastro) ");
		sql.append(" order by extract(year from c.data_cadastro) ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("cliente_id", cliente.getId());
		List<?> _rows = query.getResultList();

		List<EstatisticasGeraisCotacaoHelper> _ret = new ArrayList<>();

		for (Object _row : _rows) {
			Object[] _cols = (Object[]) _row;

			Integer _ano = (Integer) _cols[0];
			BigDecimal _menorValor = (BigDecimal) _cols[1];
			BigDecimal _maiorValor = (BigDecimal) _cols[2];
			BigDecimal _percentual = (BigDecimal) _cols[3];

			EstatisticasGeraisCotacaoHelper _h = new EstatisticasGeraisCotacaoHelper();
			_h.setAno(_ano);
			_h.setMaiorValor(_maiorValor);
			_h.setMenorValor(_menorValor);
			_h.setPercentual(_percentual);

			_ret.add(_h);
		}

		return _ret;
	}

	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeralMes(Cliente cliente, Integer ano) throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" select extract(year from c.data_cadastro) ano, ");
		sql.append(" 		extract(month from c.data_cadastro) mes, ");
		sql.append(" 		round(sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end),2) vencedor, ");
		sql.append(" 		round(sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end),2) maior, ");

		sql.append(" 		round( case when sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end) > 0 and ");
		sql.append(" 					     sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) > 0 then ");

		sql.append(" 					     ((sum(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) / ");
		sql.append(" 					       sum(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end)) - 1) * 100 ");
		sql.append(" 			   else 0 end, 2) percentual ");
		sql.append("   from cotacao c ");
		sql.append("  inner join ( select cf.cotacao_id, cf.cotacao_fornecedor_id, tbl.total, cf.vencedor_by_user ");
		sql.append("                 from cotacao_fornecedor cf  ");
		sql.append("                inner join (select cif.cotacao_fornecedor_id, ");
		sql.append("                                   sum(ci.quantidade * cifv.unitario) total ");
		sql.append("                              from cotacao_item_fornecedor cif ");
		sql.append("                             inner join cotacao_item ci on ci.cotacao_item_id = cif.cotacao_item_id ");
		sql.append(
				"                             inner join cotacao_item_fornecedor_valor cifv on cifv.cotacao_item_fornecedor_id = cif.cotacao_item_fornecedor_id ");
		sql.append("                                                                          and cifv.selecionado = 1 ");
		sql.append(
				"                             group by cif.cotacao_fornecedor_id) tbl on tbl.cotacao_fornecedor_id = cf.cotacao_fornecedor_id) tbl1 on tbl1.cotacao_id = c.cotacao_id ");
		sql.append(" where 1 = 1 ");

		sql.append("   and c.cliente_id = :cliente_id ");
		sql.append("   and extract(year from c.data_cadastro) = :ano  ");
		sql.append(" group by extract(year from c.data_cadastro), extract(month from c.data_cadastro) ");
		sql.append(" order by extract(year from c.data_cadastro), extract(month from c.data_cadastro) ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("cliente_id", cliente.getId());
		query.setParameter("ano", ano);
		List<?> _rows = query.getResultList();

		List<EstatisticasGeraisCotacaoHelper> _ret = new ArrayList<>();

		for (Object _row : _rows) {
			Object[] _cols = (Object[]) _row;

			Integer _ano = (Integer) _cols[0];
			Integer _mes = (Integer) _cols[1];
			BigDecimal _menorValor = (BigDecimal) _cols[2];
			BigDecimal _maiorValor = (BigDecimal) _cols[3];
			BigDecimal _percentual = (BigDecimal) _cols[4];

			EstatisticasGeraisCotacaoHelper _h = new EstatisticasGeraisCotacaoHelper();
			_h.setAno(_ano);
			_h.setMes(_mes);
			_h.setMaiorValor(_maiorValor);
			_h.setMenorValor(_menorValor);
			_h.setPercentual(_percentual);

			_ret.add(_h);
		}

		return _ret;
	}

	public List<EstatisticasGeraisCotacaoHelper> findEstatisticaGeralCotacao(Cliente cliente, Integer ano, Integer mes) throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" select extract(year from c.data_cadastro) ano, ");
		sql.append(" 		extract(month from c.data_cadastro) mes, ");
		sql.append(" 		c.cotacao_id, c.nome, ");
		sql.append(" 		round(max(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end),2) vencedor, ");
		sql.append(" 		round(max(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end),2) maior, ");

		sql.append(" 		round( case when max(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end) > 0 and ");
		sql.append(" 						 max(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) > 0 then ");

		sql.append(" 					((max(case when tbl1.vencedor_by_user = 0 then tbl1.total else 0 end) / ");
		sql.append(" 					  max(case when tbl1.vencedor_by_user = 1 then tbl1.total else 0 end)) - 1) * 100 ");
		sql.append(" 			   else 0 end, 2) percentual ");
		sql.append("  from cotacao c ");
		sql.append(" inner join ( select cf.cotacao_id, cf.cotacao_fornecedor_id, tbl.total, cf.vencedor_by_user ");
		sql.append("                from cotacao_fornecedor cf  ");
		sql.append("               inner join (select cif.cotacao_fornecedor_id, ");
		sql.append("                                  sum(ci.quantidade * cifv.unitario) total ");
		sql.append("                             from cotacao_item_fornecedor cif ");
		sql.append("                            inner join cotacao_item ci on ci.cotacao_item_id = cif.cotacao_item_id ");
		sql.append(
				"                            inner join cotacao_item_fornecedor_valor cifv on cifv.cotacao_item_fornecedor_id = cif.cotacao_item_fornecedor_id ");
		sql.append(" 				                                                         and cifv.selecionado = 1 ");
		sql.append(
				"                            group by cif.cotacao_fornecedor_id) tbl on tbl.cotacao_fornecedor_id = cf.cotacao_fornecedor_id) tbl1 on tbl1.cotacao_id = c.cotacao_id ");
		sql.append("  where 1 = 1 ");
		sql.append("    and c.cliente_id = :cliente_id ");
		sql.append("    and extract(year from c.data_cadastro) = :ano ");
		sql.append("    and extract(month from c.data_cadastro) = :mes ");
		sql.append("  group by extract(year from c.data_cadastro),extract(month from c.data_cadastro), c.cotacao_id, c.nome ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("cliente_id", cliente.getId());
		query.setParameter("ano", ano);
		query.setParameter("mes", mes);
		List<?> _rows = query.getResultList();

		List<EstatisticasGeraisCotacaoHelper> _ret = new ArrayList<>();

		for (Object _row : _rows) {
			Object[] _cols = (Object[]) _row;

			Integer _ano = (Integer) _cols[0];
			Integer _mes = (Integer) _cols[1];
			Long _cotacaoId = (Long) _cols[2];
			String _cotacaoNome = (String) _cols[3];
			BigDecimal _menorValor = (BigDecimal) _cols[4];
			BigDecimal _maiorValor = (BigDecimal) _cols[5];
			BigDecimal _percentual = (BigDecimal) _cols[6];

			EstatisticasGeraisCotacaoHelper _h = new EstatisticasGeraisCotacaoHelper();
			_h.setAno(_ano);
			_h.setMes(_mes);
			_h.setCotacaoId(_cotacaoId);
			_h.setCotacaoNome(_cotacaoNome);
			_h.setMaiorValor(_maiorValor);
			_h.setMenorValor(_menorValor);
			_h.setPercentual(_percentual);

			_ret.add(_h);
		}

		return _ret;
	}

}
