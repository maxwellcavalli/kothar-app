package br.com.kotar.web.util;

import java.text.DecimalFormat;

import br.com.kotar.core.component.Messages;
import br.com.kotar.domain.business.CotacaoFornecedor;

public class ServiceUtil {
	final static DecimalFormat df = new DecimalFormat("0.00");	
	
	public static void popularTextoPrazo(CotacaoFornecedor cf, Messages messages) {
		cf.setTextoPrazo("");
		
		if (cf.getTipoPagamento() != null){
			cf.setTipoPagamentoStr(messages.get(cf.getTipoPagamento().getMessageKey()));
		}
		else {
			cf.setTipoPagamentoStr("");
		}

		if (cf.getTipoJurosPagamento() != null){
			cf.setTipoJurosPagamentoStr(messages.get(cf.getTipoJurosPagamento().getMessageKey()));
		}		    
		else {
			cf.setTipoJurosPagamentoStr("");
		}
		
		String taxaJuros = "";
		String numeroParcelas = "";
		if (cf.getNumeroParcelas() != null) {
			numeroParcelas = cf.getNumeroParcelas().toString();
		}

		if (cf.getJurosPacelamento() != null) {
			taxaJuros = df.format(cf.getJurosPacelamento());
		}		
		
		if (numeroParcelas != null && !numeroParcelas.trim().isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append(numeroParcelas);
			builder.append("x");

			if (taxaJuros != null && !taxaJuros.trim().isEmpty()) {
				builder.append(" (" + taxaJuros + ")");
			}
			cf.setTextoPrazo(builder.toString());
		}
	}
}
