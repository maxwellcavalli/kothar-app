package br.com.kotar.web.repository.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.repository.impl.BaseRepositoryImpl;
import br.com.kotar.core.util.DataUtil;
import br.com.kotar.domain.business.PendenciaIntegracao;
import br.com.kotar.domain.helper.PendenciaIntegracaoFilter;
import br.com.kotar.web.repository.PendenciaIntegracaoRepository;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class PendenciaIntegracaoDAOImpl extends BaseRepositoryImpl<PendenciaIntegracao>
		implements PendenciaIntegracaoRepository {

	//@formatter:off
	@Autowired PendenciaIntegracaoRepository pendenciaIntegracaoRepository;
	//@formatter:on

	@Override
	public BaseRepository<PendenciaIntegracao> getRepository() {
		return pendenciaIntegracaoRepository;
	}

	public Page<PendenciaIntegracao> findPedencias(PendenciaIntegracaoFilter pendenciaIntegracaoFilter) throws Exception {
		Pageable pageable = pendenciaIntegracaoFilter.getPageable();

		StringBuilder hql = new StringBuilder();
		hql.append(" select pendenciaIntegracao ");
		hql.append("   from PendenciaIntegracao pendenciaIntegracao ");
		hql.append("  where 1 = 1 ");
		hql.append("    and pendenciaIntegracao.finalizado = false ");
		
		Map<String, Object> parameters = new HashMap<>();
		
		if (pendenciaIntegracaoFilter.getPeriodoCadastro() != null) {
			if (pendenciaIntegracaoFilter.getPeriodoCadastro().getInicio() != null){
				Date d1 = DataUtil.adicionaHoraData(pendenciaIntegracaoFilter.getPeriodoCadastro().getInicio(), 0, 0, 0);
				hql.append("   and pendenciaIntegracao.dataCriacao >= :data_criacao_inicial");
				parameters.put("data_criacao_inicial", d1);
			}
			
			if (pendenciaIntegracaoFilter.getPeriodoCadastro().getFim() != null){
				Date d2 = DataUtil.adicionaHoraData(pendenciaIntegracaoFilter.getPeriodoCadastro().getFim(), 0, 0, 0);
				hql.append("   and pendenciaIntegracao.dataCriacao <= :data_criacao_final ");
				parameters.put("data_criacao_final", d2);
			}
		}
			
		if (pageable.getSort() == null) {
			hql.append("  order by pendenciaIntegracao.dataCriacao desc ");
		} else {
			StringBuilder builder = new StringBuilder();
			String orderBy = "";

			pageable.getSort().forEach(order -> {
				builder.append(" pendenciaIntegracao." + order.getProperty());
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

		Page<PendenciaIntegracao> list = searchPaginated(hql.toString(), pageable, parameters);
		
		list.forEach(el -> {
			String _str = messages.get(el.getTipoPendenciaIntegracao().getMessageKey());
			el.setTipoPendenciaStr(_str);
		});
		
		return list; 
	}
}