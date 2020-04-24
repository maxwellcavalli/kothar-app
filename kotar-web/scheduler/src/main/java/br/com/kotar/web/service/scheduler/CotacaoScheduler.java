package br.com.kotar.web.service.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.kotar.core.exception.RecordNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.android.gcm.server.Notification;

import br.com.kotar.core.util.GCMUtil;
import br.com.kotar.domain.business.ConfiguracaoServidor;
import br.com.kotar.domain.business.Cotacao;
import br.com.kotar.domain.business.CotacaoFornecedor;
import br.com.kotar.domain.business.Fornecedor;
import br.com.kotar.domain.business.type.SituacaoCotacaoType;
import br.com.kotar.domain.security.Usuario;
import br.com.kotar.domain.security.UsuarioToken;
import br.com.kotar.web.service.ConfiguracaoServidorService;
import br.com.kotar.web.service.CotacaoFornecedorService;
import br.com.kotar.web.service.CotacaoService;
import br.com.kotar.web.service.UsuarioTokenService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Component
public class CotacaoScheduler {

	 private static final Logger log = LoggerFactory.getLogger(CotacaoScheduler.class);
	 
	//@formatter:off
	@Autowired CotacaoService cotacaoService;
	@Autowired CotacaoFornecedorService cotacaoFornecedorService;
	@Autowired UsuarioTokenService usuarioTokenService;
	@Autowired ConfiguracaoServidorService configuracaoServidorService;
	@Autowired protected CacheManager cacheManager;
	//@formatter:on
	
	@Scheduled(fixedRate = 2000)
    public void reportCurrentTime() {
		log.info("Verificando situacao da cotacao");
		
		List<Cotacao> list = cotacaoService.findCotacaoExpirada();
		log.info("Quantidade de cotacoes expiradas: " + list.size());		
		
		for (Cotacao cotacao : list){
			Long quantidadeRespostas = cotacao.getQuantidadeRespostas();
			Optional<Cotacao> cotacaoOptional = cotacaoService.findById(cotacao.getId());

			if (!cotacaoOptional.isPresent()){
				throw new RecordNotFound();
			}

			cotacao = cotacaoOptional.get();
			
			if (quantidadeRespostas == 0){
				cotacao.setSituacaoCotacao(SituacaoCotacaoType.FINALIZADA);	
			} else {
				cotacao.setSituacaoCotacao(SituacaoCotacaoType.ENCERRADA);
			}
			
			cotacaoService.save(cotacao);
		}
    }
	
	
	@Scheduled(fixedRate = 2000)
    public void enviarPushRepostas() {
		ConfiguracaoServidor configuracaoServidor = null;
		
		Cache cache = cacheManager.getCache("serverConfig");
		if (cache.isElementInMemory("config")){
			Element element = cache.get("config");
			configuracaoServidor = (ConfiguracaoServidor) element.getObjectValue();
		}

		if (configuracaoServidor == null) {
			Optional<ConfiguracaoServidor> configuracaoServidorOptional = configuracaoServidorService.findById(1L);

			if (!configuracaoServidorOptional.isPresent()){
				throw new RecordNotFound();
			}

			configuracaoServidor = configuracaoServidorOptional.get();

			Element element = new Element("config", configuracaoServidor);
			cache.put(element);
		}
		
		String pushServerKey = configuracaoServidor.getPushServerKey();
		
		log.info("Verificando respostas para enviar push");
		
		List<CotacaoFornecedor> list = cotacaoFornecedorService.findRespostasEnviadasParaPush();
		log.info("Quantidade de respostas para enviar push: " + list.size());		

		
		for (CotacaoFornecedor cotacaoFornecedor : list) {

			Fornecedor fornecedor = cotacaoFornecedor.getFornecedor();
			Cotacao cotacao = cotacaoFornecedor.getCotacao();
			Usuario usuario = cotacao.getCliente().getUsuario();
			
			String pushResposta = "Cotação "+cotacao.getNome().trim()+" respondida por "+fornecedor.getNome();			
			
			try {
				List<UsuarioToken> listTokens = usuarioTokenService.findByUsuario(usuario);			
				for (UsuarioToken usuarioToken : listTokens) {
					
					Notification notification = 
							new Notification.Builder("pipa").body(pushResposta).color("#3575b4")
							                .sound("padrao").title("Kothar - Resposta "+fornecedor.getNome()).build();
					
					Map<String,String> data = new HashMap<>();
					
					data.put("codigo_push", cotacaoFornecedor.getId().toString());
					data.put("cotacao_id", cotacao.getId().toString());
					data.put("cotacao_fornecedor_id", cotacaoFornecedor.getId().toString());
					
					GCMUtil.sendMessage(data, notification, pushServerKey, usuarioToken.getPushTokenId());					
				}
				
				cotacaoFornecedor.setPushEnviado(true);				
				cotacaoFornecedorService.save(cotacaoFornecedor);
			} catch (Exception e) {
				log.info("Erro ao enviar push: " + e.getLocalizedMessage() + " "+e);
			}
			
		}
		
		
	}
	
}
