package br.com.kotar.web.service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.kotar.core.repository.BaseRepository;
import br.com.kotar.core.service.BaseService;
import br.com.kotar.core.util.ImageUtil;
import br.com.kotar.domain.business.CotacaoItem;
import br.com.kotar.domain.business.CotacaoItemArquivo;
import br.com.kotar.web.repository.CotacaoItemArquivoRepository;

@Service
public class CotacaoItemArquivoService extends BaseService<CotacaoItemArquivo> {

	//@formatter:off
	@Autowired CotacaoItemArquivoRepository cotacaoItemArquivoRepository;
	//@formatter:on

	@Override
	public BaseRepository<CotacaoItemArquivo> getRepository() {
		return cotacaoItemArquivoRepository;
	}
	
	public List<CotacaoItemArquivo> findByCotacaoItem(CotacaoItem cotacaoItem){
		return cotacaoItemArquivoRepository.findByCotacaoItem(cotacaoItem);
	}
	
	public Optional<CotacaoItemArquivo> findById(Long id){
		return cotacaoItemArquivoRepository.findById(id);
	}
	
	public void saveOrUpdate(CotacaoItem cotacaoItem, List<CotacaoItemArquivo> arquivos) throws Exception {
		List<CotacaoItemArquivo> listDB = findByCotacaoItem(cotacaoItem);
		
		for (CotacaoItemArquivo cotacaoItemArquivoDB : listDB){
			boolean exists = false;
			
			for (CotacaoItemArquivo cotacaoItemArquivo : arquivos){
				
				if (cotacaoItemArquivo.getId() == null || cotacaoItemArquivo.getId().longValue() == 0){
					continue;
				}
				
				if (cotacaoItemArquivo.getId().longValue() == cotacaoItemArquivoDB.getId().longValue()){
					exists = true;
					break;
				}				
			}
			
			if (!exists){
				cotacaoItemArquivoRepository.delete(cotacaoItemArquivoDB);
			}
		}
		
		for (CotacaoItemArquivo cotacaoItemArquivo : arquivos){
			if (cotacaoItemArquivo.getArquivo() == null || cotacaoItemArquivo.getArquivo().length == 0){
				continue;
			}
			
			cotacaoItemArquivo.setDataAtualizacao(Calendar.getInstance().getTime());
			cotacaoItemArquivo.setCotacaoItem(cotacaoItem);
			
			if (cotacaoItemArquivo.getThumb() == null) {
				byte[] thumb = ImageUtil.gerarThumb(cotacaoItemArquivo.getArquivo(), 70, 70);
				cotacaoItemArquivo.setThumb(thumb);
			}
			
			cotacaoItemArquivoRepository.save(cotacaoItemArquivo);
			
		}
	}
	
	public void delete(CotacaoItem cotacaoItem) throws Exception {
		List<CotacaoItemArquivo> listDB = findByCotacaoItem(cotacaoItem);
		cotacaoItemArquivoRepository.deleteAll(listDB);
	}
	
	public void copiarArquivos(CotacaoItem cotacaoItemOrigem, CotacaoItem cotacaoItemDestino) throws Exception {
		List<CotacaoItemArquivo> list = cotacaoItemArquivoRepository.findByCotacaoItemWithFile(cotacaoItemOrigem);
		for (CotacaoItemArquivo cotacaoItemArquivo : list){
			CotacaoItemArquivo _new = cotacaoItemArquivo.clone(); //BeanUtils.cloneBean(cotacaoItemArquivo);
			_new.setId(null);
			_new.setCotacaoItem(cotacaoItemDestino);
			save(_new);
		}
	}
	
	public CotacaoItemArquivo createThumbIfNull(CotacaoItemArquivo cotacaoItemArquivo) {
		
		if (cotacaoItemArquivo.getThumb() == null) {
			byte[] thumb = ImageUtil.gerarThumb(cotacaoItemArquivo.getArquivo(), 70, 70);
			cotacaoItemArquivo.setThumb(thumb);
			
			cotacaoItemArquivo = cotacaoItemArquivoRepository.save(cotacaoItemArquivo);		
		}
		
		return cotacaoItemArquivo;
	}

}
