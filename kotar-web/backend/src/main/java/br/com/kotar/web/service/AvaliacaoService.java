package br.com.kotar.web.service;

import java.util.*;

import br.com.kotar.core.exception.RecordNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.domain.business.Avaliacao;
import br.com.kotar.domain.business.Cliente;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.type.MotivoDesistenciaCompraType;
import br.com.kotar.domain.helper.AvaliacaoFilter;
import br.com.kotar.domain.helper.MotivoDesistenciaCompraHelper;
import br.com.kotar.domain.helper.TotaisAvaliacaoFornecedorHelper;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.web.repository.CotacaoRepository;
import br.com.kotar.web.repository.impl.AvaliacaoDAOImpl;

@Service
public class AvaliacaoService extends BaseService<Avaliacao> {

	//@formatter:off
	@Autowired AvaliacaoDAOImpl avaliacaoDAO;
	@Autowired CotacaoRepository cotacaoRepository;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	@Autowired CotacaoService cotacaoService;
	//@formatter:on

	@Override
	public BaseRepository<Avaliacao> getRepository() {
		return avaliacaoDAO;
	}

	public Page<Avaliacao> findAvaliacao(Usuario usuario, AvaliacaoFilter avaliacaoFilter) throws Exception {
		return avaliacaoDAO.findAvaliacao(usuario, avaliacaoFilter);
	}

	// public Page<Avaliacao> findByFornecedor(Fornecedor fornecedor, Pageable
	// pageable) throws Exception {
	// return avaliacaoDAO.findByFornecedor(fornecedor, pageable);
	// }

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Optional<Avaliacao> findById(Long id){
		Optional<Avaliacao> optional = getRepository().findById(id);
		if (optional.isPresent()){
			Avaliacao avaliacao = optional.get();
			String _message = messages.get(avaliacao.getMotivoDesistenciaCompraType().getMessageKey());
			avaliacao.setMotivoDesistenciaCompraStr(_message);

			MotivoDesistenciaCompraHelper motivoDesistenciaCompraHelper = new MotivoDesistenciaCompraHelper();
			motivoDesistenciaCompraHelper.setValue(avaliacao.getMotivoDesistenciaCompraType());
			motivoDesistenciaCompraHelper.setDescription(_message);
			avaliacao.setMotivoDesistenciaCompraHelper(motivoDesistenciaCompraHelper);

			return Optional.of(avaliacao);

		} else {
			return Optional.empty();
		}
	}

	@Override
	public Avaliacao saveOrUpdate(Avaliacao avaliacao) throws Exception {
		CotacaoFornecedor cotacaoFornecedor = avaliacao.getCotacaoFornecedor();
		Optional<CotacaoFornecedor> cotacaoFornecedorOptional = cotacaoFornecedorService.findById(cotacaoFornecedor.getId());

		if (!cotacaoFornecedorOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacaoFornecedor = cotacaoFornecedorOptional.get();
		avaliacao.setCotacaoFornecedor(cotacaoFornecedor);
		
		Long idCotacao = avaliacao.getCotacaoFornecedor().getCotacao().getId();

		Optional<Cotacao> cotacaoOptional = cotacaoRepository.findById(idCotacao);
		if (!cotacaoOptional.isPresent()){
			throw new RecordNotFound();
		}

		Cotacao cotacao = cotacaoOptional.get();
		cotacao.setDataAvaliacao(Calendar.getInstance().getTime());
		cotacaoRepository.save(cotacao);

		return super.saveOrUpdate(avaliacao);
	}

	public List<MotivoDesistenciaCompraHelper> getMotivos() {
		List<MotivoDesistenciaCompraHelper> _l = new ArrayList<MotivoDesistenciaCompraHelper>();

		for (MotivoDesistenciaCompraType _t : MotivoDesistenciaCompraType.values()) {
			MotivoDesistenciaCompraHelper _m = new MotivoDesistenciaCompraHelper();
			_m.setValue(_t);
			_m.setDescription(messages.get(_t.getMessageKey()));

			_l.add(_m);
		}

		return _l;
	}
	
	public Avaliacao findByCotacao(Cotacao cotacao){
		return ((AvaliacaoDAOImpl)getRepository()).findByCotacao(cotacao); 
	}
	
	public TotaisAvaliacaoFornecedorHelper findTotaisByFornecedor(Fornecedor fornecedor) throws Exception{
		return ((AvaliacaoDAOImpl)getRepository()).findTotaisByFornecedor(fornecedor);
	}
	
	public void atualizarLembrete(CotacaoFornecedor cotacaoFornecedor) throws Exception {
		Optional<CotacaoFornecedor> cotacaoFornecedorOptional = cotacaoFornecedorService.findById(cotacaoFornecedor.getId());

		if (!cotacaoFornecedorOptional.isPresent()){
			throw new RecordNotFound();
		}

		cotacaoFornecedor = cotacaoFornecedorOptional.get();
		Cotacao cotacao = cotacaoFornecedor.getCotacao();
		
		Avaliacao avaliacao = findByCotacao(cotacao);
		if (avaliacao != null){
			delete(avaliacao);
		}
		
		Date dataLembrete = cotacao.getDataLembreteAvaliacao();
		if (dataLembrete == null) {
			dataLembrete = Calendar.getInstance().getTime();
		}				
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataLembrete);
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		
		cotacao.setDataAvaliacao(null);
		cotacao.setDataLembreteAvaliacao(calendar.getTime());
		cotacaoService.save(cotacao);
	}
	
	public Long findQuantidadeAvaliacoesPendentes(Cliente cliente) {
		return avaliacaoDAO.findQuantidadeAvaliacoesPendentes(cliente);
	}
}