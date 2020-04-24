package br.com.kotar.web.repository.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.CrudRepository;
import br.com.kotar.core.repository.impl.BaseCrudRepositoryImpl;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.GrupoProduto;
import br.com.kotar.domain.helper.AvaliacaoFornecedorHelper;
import br.com.kotar.domain.helper.MelhoresClientesFornecedorHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.FornecedorRepository;
import br.com.kotar.web.service.ClienteService;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class FornecedorDAOImpl extends BaseCrudRepositoryImpl<Fornecedor> implements FornecedorRepository {

	//@formatter:off
	@Autowired FornecedorRepository fornecedorRepository;
	@Autowired ClienteService clienteService;
	//@formatter:on

	@Override
	public CrudRepository<Fornecedor> getRepository() {
		return fornecedorRepository;
	}

	public Page<Fornecedor> findByNomeOrCpfCnpjLikeIgnoreCase(String nome, String cpfCnpj, Usuario usuario, Pageable pageable) throws Exception {

		StringBuilder hql = new StringBuilder();
		hql.append(" select fornecedor ");
		hql.append("   from Fornecedor fornecedor ");
		hql.append("  inner join fetch fornecedor.cep cep ");
		hql.append("  inner join fetch cep.bairro bairro ");
		hql.append("  inner join fetch bairro.cidade cidade ");
		hql.append("  inner join fetch cidade.estado estado ");
		hql.append("   left join fetch fornecedor.enderecoComplemento ");
		hql.append("  where 1 = 1 ");

		Map<String, Object> parameters = new HashMap<>();
		if (nome != null && !nome.trim().isEmpty()) {
			hql.append("    and upper(fornecedor.nome) like upper(:nome) ");
			parameters.put("nome", nome);
		}

		if (cpfCnpj != null && !cpfCnpj.trim().isEmpty()) {
			hql.append("    and upper(fornecedor.cpfCnpj) = (:cpfCnpj) ");
			parameters.put("cpfCnpj", cpfCnpj);
		}

		if (!usuario.isAdmin()) {
			Cliente cliente = clienteService.findByUsuario(usuario);

			if (cliente == null) {
				if (cliente == null) {
					throw new Exception(messages.get("fornecedor.not.agent.login"));
				}
			}

			hql.append(" and exists (select 1 ");
			hql.append("               from ClienteFornecedor clienteFornecedor ");
			hql.append("              where clienteFornecedor.fornecedor = fornecedor ");
			hql.append("                and clienteFornecedor.cliente = :cliente) ");

			parameters.put("cliente", cliente);
		}

		return searchPaginated(hql.toString(), pageable, parameters);
	}

	public List<Fornecedor> findByGrupoProduto(GrupoProduto grupoProduto) throws Exception {
		StringBuilder hql = new StringBuilder();
		hql.append(" select fornecedor ");
		hql.append("   from Fornecedor fornecedor ");
		hql.append("  where exists (select 1 ");
		hql.append("                  from FornecedorGrupoProduto fornecedorGrupoProduto ");
		hql.append("                 where fornecedorGrupoProduto.fornecedor = fornecedor ");
		hql.append("                   and fornecedorGrupoProduto.grupoProduto = :grupoProduto ");
		hql.append("                    and not exists (select 1");
		hql.append("                                      from FornecedorIgnoreProduto fornecedorIgnoreProduto ");
		hql.append(
				"                                      where fornecedorIgnoreProduto.produto.grupoProduto = fornecedorGrupoProduto.grupoProduto ))");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("grupoProduto", grupoProduto);

		return search(hql.toString(), Integer.MAX_VALUE, 0, parameters);
	}

	public List<AvaliacaoFornecedorHelper> findAvaliacaoFornecedor(Fornecedor fornecedor) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append(" select sum(1) as total, ");
		sql.append("        avg(numero_estrelas) as estrelas ");
		sql.append("   from avaliacao a ");
		sql.append("  inner join cotacao_fornecedor cf on cf.cotacao_fornecedor_id = a.cotacao_fornecedor_id ");
		sql.append("  where cf.fornecedor_id = :fornecedor_id ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("fornecedor_id", fornecedor.getId());

		List<AvaliacaoFornecedorHelper> ret = new ArrayList<>();
		List<?> _l = query.getResultList();
		for (Object _row : _l) {
			Object[] _cols = (Object[]) _row;

			Long _total = _cols[0] == null ? 0l : ((BigDecimal) _cols[0]).longValue();
			BigDecimal _estrelas = _cols[1] == null ? BigDecimal.ZERO : ((BigDecimal) _cols[1]);

			AvaliacaoFornecedorHelper _a = new AvaliacaoFornecedorHelper();
			_a.setTotal(_total);
			_a.setEstrelas(_estrelas);

			ret.add(_a);
		}

		return ret;
	}

	public List<MelhoresClientesFornecedorHelper> findMelhoresClientes(Fornecedor fornecedor) throws Exception {
		StringBuilder sql = new StringBuilder();
		sql.append("  select cli.nome, "); 
		sql.append("  		 cast(tbl.total as decimal(16,2)) as total ");
		sql.append("    from cliente cli ");
		sql.append("   inner join (select cot.cliente_id, ");
		sql.append("                      sum(ci.quantidade * cifv.unitario) as total ");
		sql.append("                 from cotacao_item ci ");
		sql.append("                inner join cotacao cot on cot.cotacao_id = ci.cotacao_id ");
		sql.append("                inner join cotacao_fornecedor cf on cf.cotacao_id = cot.cotacao_id ");
		sql.append("                inner join cotacao_item_fornecedor cif on cif.cotacao_item_id = ci.cotacao_item_id ");
		sql.append("                                                      and cif.cotacao_fornecedor_id = cf.cotacao_fornecedor_id ");
		sql.append("                inner join cotacao_item_fornecedor_valor cifv on cifv.cotacao_item_fornecedor_id = cif.cotacao_item_fornecedor_id ");
		sql.append("                where cifv.selecionado = 1 ");
		sql.append("                  and cf.fornecedor_id = :fornecedor_id ");
		sql.append("                group by cot.cliente_id) tbl on tbl.cliente_id = cli.cliente_id ");
		sql.append(" order by tbl.total desc ");
		
		DecimalFormat df = new DecimalFormat("###,###,###,###,###.00");
		
		List<MelhoresClientesFornecedorHelper> ret = new ArrayList<>();	 
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("fornecedor_id", fornecedor.getId());
		query.setMaxResults(20);
		
		List<?> rows = query.getResultList();
		for (Object row: rows){
			Object[] col = (Object[]) row;
			
			String _nome = (String) col[0];
			BigDecimal _total = (BigDecimal) col[1];
			
			_total = _total == null ? BigDecimal.ZERO : _total;
			
			MelhoresClientesFornecedorHelper _m = new MelhoresClientesFornecedorHelper();
			_m.setNome(_nome);
			_m.setValor(df.format(_total.doubleValue()));
			
			ret.add(_m);
		}
		
		return ret;
	}

	@Override
	public List<Fornecedor> findByCliente(Cliente cliente) {
		return fornecedorRepository.findByCliente(cliente);
	}

	@Override
	public Fornecedor findByUuid(String uuid) {
		return fornecedorRepository.findByUuid(uuid);
	}

}
