package br.com.kotar.core.util.formatter;

import javax.swing.text.MaskFormatter;

public class CpfCnpjFormatter {

	public static String cleanFormat(String value){
		String cnpjCpf = value;
		cnpjCpf = cnpjCpf.replaceAll("\\.", "");
		cnpjCpf = cnpjCpf.replaceAll("/", "");
		cnpjCpf = cnpjCpf.replaceAll("-", "");
		cnpjCpf = cnpjCpf.replaceAll("_", "");
		
		return cnpjCpf; 
	}
	
	public static String format(String value){
		try {
			String v = ((String) value).trim();
			v = cleanFormat(v);
			if (v.length() == 11) {
				final MaskFormatter formatter = new MaskFormatter("###.###.###-##");
				formatter.setValueContainsLiteralCharacters(false);
				return formatter.valueToString(v);
			} else if (v.length() == 14) {
				final MaskFormatter formatter = new MaskFormatter("##.###.###/####-##");
				formatter.setValueContainsLiteralCharacters(false);
				return formatter.valueToString(v);
			} else {
				//Se o tamanho do campo for incorreto retorna o valor que está
				return (String) value;
			} 
		} catch (Exception e) {
			return value;
		}
	}
}
