package br.com.kotar.core.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

public class StringUtil {

	private static final char[] semAcento = { 'a', 'e', 'i', 'o', 'u', 'A',
			'E', 'I', 'O', 'U', 'c', 'C', 'a', 'e', 'i', 'o', 'u', 'A', 'E',
			'I', 'O', 'U', 'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U',
			'a', 'o', 'A', 'O' };
	private static final char[] comAcento = { 'á', 'é', 'í', 'ó', 'ú', 'Á',
			'É', 'Í', 'Ó', 'Ú', 'ç', 'Ç', 'à', 'è', 'ì', 'ò', 'ù', 'À', 'È',
			'Ì', 'Ò', 'Ù', 'â', 'ê', 'î', 'ô', 'û', 'Â', 'Ê', 'Î', 'Ô', 'Û',
			'ã', 'õ', 'Ã', 'Õ' };

	/**
	 * verifica se o caracter Ã© um delimitador
	 * 
	 * @param ch
	 *            o caracter a ser checado
	 * @param delimiters
	 *            o(s) delimitador(es)
	 * @return true se for um delimitador
	 */
	public static boolean isDelimiter(final char ch, final char[] delimiters) {
		if (delimiters == null) {
			return Character.isWhitespace(ch);
		}
		for (final char delimiter : delimiters) {
			if (ch == delimiter) {
				return true;
			}
		}
		return false;
	}

	/**
	 * MÃ©todo para retirar todos os espaÃ§os a mais que encontrar na string.
	 * (Inclusive o espaÃ§o final com um trim()).
	 * 
	 * @param valor
	 *            String para retirar os espaÃ§os a mais.
	 * @return Retorna a string sem nenhum espaÃ§o a mais.
	 */
	public static String retirarTodosOsEspacosSobrando(String valor) {
		if (valor != null) {
			while (valor.indexOf("  ") != -1) {
				valor = valor.replaceAll("  ", " ");
			}
			valor = valor.trim();
			
			if (valor.indexOf(' ') == valor.length()){
				StringBuffer remove = new StringBuffer(valor);
				remove.delete(valor.length()-1, valor.length());
				valor = remove.toString();
			}
			if (valor.indexOf(' ') == 0){
				StringBuffer remove = new StringBuffer(valor);
				remove.delete(0, 1);
				valor = remove.toString();
			}

		}
		return valor;
	}

	public static String removerEspacos(String valor) {
		if (valor != null) {
			while (valor.indexOf("  ") != -1) {
				valor = valor.replaceAll("  ", " ");
			}
			
			while (valor.indexOf(" ") != -1) {
				valor = valor.replaceAll(" ", "");
			}
			
			valor = valor.trim();
		}
		return valor;
	}
	
	/**
	 * MÃ©todo para truncar uma String.
	 * 
	 * @param valor
	 * @param tamanho
	 * @return String truncada
	 */
	public static String truncate(String valor, int tamanho) {
		if (temValor(valor)) {
			if (valor.length() > tamanho) {
				return valor.substring(0, tamanho);
			}
		}
		return valor;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static String toLikeUpperSql(String str) {
		String retorno = null;
		if (temValor(str)) {
			Scanner sc = null;
			str = tiraAcentos(retirarTodosOsEspacosSobrando(str.trim()));
			str = str.toUpperCase();
			try {
				sc = new Scanner(str);
				while (sc.hasNext()) {
					if (retorno == null) {
						retorno = "";
					}
					retorno += "%" + sc.next() + "%";
				}
			} finally {
				if (sc != null) {
					sc.close();
				}
			}
			if (temValor(retorno)) {
				retorno = fixStringToSQL(retorno);
				retorno = retorno.replaceAll("%%", "%");
			}
		}
		return retorno;
	}

	/**
	 * Retira os acentos de uma string.
	 * 
	 * @param frase
	 *            String para retirar os acentos.
	 * @return String com os acentos retirados.
	 * @author 
	 * @version 19/01/2004
	 */
	public static String tiraAcentos(final String frase) {
		if (frase != null) {
			String novaFrase = frase;
			for (int i = 0; i < semAcento.length; i++) {
				novaFrase = novaFrase.replace(comAcento[i], semAcento[i]);
			}
			return novaFrase;
		}
		return null;
	}

	/**
	 * MÃ©todo para verificar se uma String nÃ£o estÃ¡ vazia.<br>
	 * TambÃ©m jÃ¡ verifica se estÃ¡ nulo e faz um trim antes para tirar valores em
	 * branco.
	 * 
	 * @author 
	 * @param valor
	 *            String para verificaÃ§Ã£o
	 * @version 15/07/2003
	 */
	public static boolean temValor(final String valor) {
		return (valor != null && valor.trim().compareTo("") != 0);
	}

	/**
	 * MÃ©todo para verificar se um objeto Ã© considerado vazio.<br>
	 * Caso o objeto seja instancia de String, verifica se esta vazio.
	 * 
	 * @author 
	 * @param valor
	 *            Object para verificaÃ§Ã£o
	 * @version 23/02/2010
	 */
	public static boolean temValor(final Object valor) {
		if (valor instanceof String) {
			return temValor((String) valor);
		} else {
			return (valor != null);
		}
	}

	/**
	 * Corrige a string para ser feita uma operaÃ§Ã£o DML
	 * 
	 * @param str
	 *            string para corrigir
	 * @return a string corrigida
	 * @author 
	 * @version 20/02/2004
	 */
	public static String fixStringToSQL(final String str) {
		String temp = "";
		if (str != null) {
			int position = 0;
			int oldposition = 0;
			int current = 0;
			while (current > -1) {
				position = str.indexOf("'", position);
				current = position;
				if (position > -1) {
					temp = temp + str.substring(oldposition, position) + "'";
					oldposition = position;
				}
				position++;
			}
			temp = temp + str.substring(oldposition, str.length());
		} else
			return null;

		return temp;
	}
	
	public static String formata(String entrada) {
		String saida = "";

		if (entrada != null) {
			saida = Normalizer.normalize(entrada.toUpperCase(),
					Normalizer.Form.NFD);
			saida = saida.replaceAll("[^\\p{ASCII}]", "");
		}
		return saida;
	}

	/**
	 * 
	 * @param valor
	 * @param delimitador
	 * @return
	 * @throws Exception
	 */
    public static String[] split(final String valor, final String delimitador) throws Exception {
	    List<String> list = new ArrayList<String>();	    
	    StringTokenizer st = new StringTokenizer(valor);
	    
	    while (st.hasMoreTokens()) {
	      list.add(st.nextToken(delimitador).trim());
	    }
	    
	    return list.toArray(new String[list.size()]);
    }	
    
    /**
     * 
     * @param string
     * @return
     */
    public static String retiraUltimoCaracter(String string){
    	return string.substring(0, string.length()-1);
    }
    
    /**
     * 
     * @param string
     * @return
     */
    public static String retiraUltimosCaracteres(String string, int numCaracteres){
    	return string.substring(0, string.length()-numCaracteres);
    }

    /**
     * 
     * @param string
     * @return
     */
    public static String retiraUltimosCaracteresCondicional(String string, int numCaracteres, String subStringFinal){
    	
    	int tamanho = subStringFinal.length();
    	
    	String stringFinal = string.substring(string.length()-tamanho, string.length());
    	
    	if (stringFinal.compareTo(subStringFinal) == 0) {
    		return retiraUltimosCaracteres(string, numCaracteres);
    	}
    	else{
    		return string;
    	}
    }
	
	private static final char[] delimiters = { ' ', '.', '(', ')', '[', ']', ':' };
	
	/**
	 * Gera string randômica conforme o tamanho passado.
	 * 
	 * @param length - Tamanho da string.
	 * @return {@link String} - String randômica.
	 * @author 
	 * @date 24/01/2012 - 08:58:31
	 */
	public static String generateRandString(int length) {
		return generateRandString(length, "a-zA-Z0-9");
	}
	
	/**
	 * Gera string randômica conforme o tamanho passado.
	 * Aceitando apenas os caracteres definidos no regexDelimiter.
	 * 
	 * @param length - Tamanho da string.
	 * @param regexDelimiter - Regex que irá definir os caracteres aceitos. Bas apenas colocar "a-z" para gerar apenas
	 *            caracteres minúsculos de 'a' a 'z'.
	 * @return {@link String} - String randômica.
	 * @author 
	 * @date 24/01/2012 - 08:58:31
	 */
	public static String generateRandString(int length, String regexDelimiter) {
		final StringBuffer saida = new StringBuffer();
		final String regex = "[^" + regexDelimiter + "]";
		for (int i = 0; i < length; i++) {
			String aux = String.valueOf((char) Double.valueOf(Math.random() * 100).intValue());
			while (aux.replaceAll(regex, "").length() == 0) {
				aux = String.valueOf((char) Double.valueOf(Math.random() * 100).intValue());
			}
			saida.append(aux);
		}
	
		return saida.toString();
	}
	
	/**
	 * Capitaliza a str de entrada.
	 * 
	 * @param str - String de entrada.
	 * @return String - Entrada capitalizada.
	 */
	public static String capitalize(final String str) {
		final StringBuffer sb = new StringBuffer();
	
		if (StringUtil.isEmpty(str)) {
			sb.append(str);
		} else {
			boolean capitalizeNext = true;
			boolean isDelimiter = false;
	
			for (int i = 0; i < str.length(); i++) {
				final char ch = str.charAt(i);
	
				for (char del : delimiters) {
					if (ch == del) {
						isDelimiter = true;
						capitalizeNext = true;
					}
				}
	
				if (capitalizeNext && !isDelimiter) {
					sb.append(Character.toTitleCase(ch));
					capitalizeNext = false;
				} else {
					sb.append(ch);
				}
				isDelimiter = false;
			}
		}
	
		return sb.toString();
	}
	
	/**
	 * Retira os acentos de uma string.
	 * 
	 * @param frase - String para retirar os acentos.
	 * @return String - com os acentos retirados.
	 */
	public static String retirarAcentuacao(final String frase) {
		if (frase != null) {
			String novaFrase = frase;
			for (int i = 0; i < semAcento.length; i++) {
				novaFrase = novaFrase.replace(comAcento[i], semAcento[i]);
			}
			return novaFrase;
		}
		return null;
	}
	
	/**
	 * Remove todos os caracteres acentuados especiais.
	 * Só irá permanecer letras de a-z e espaços.
	 * 
	 * @param frase - Frase a ser tratada.
	 * @return {@link String} - Frase tratada.
	 * @author 
	 * @date 24/01/2012 - 08:56:48
	 */
	public static String retiraCaracteresEspeciais(final String frase) {
		if (frase != null) {
			return frase.replaceAll("[^a-zA-Z ]", "");
		}
		return null;
	}
	
	/**
	 * Remove todos os caracteres acentuados especiais com excessao do ponto e do hífen.
	 * Só irá permanecer letras de a-z, espaços, pontos e hífens.
	 * 
	 * @param frase - Frase a ser tratada.
	 * @return {@link String} - Frase tratada.
	 * @author 
	 * @date 04/03/2016 - 16:01:00
	 */
	public static String retiraCaracteresEspeciaisNomeArquivo(final String frase) {
		if (frase != null) {
			return frase.replaceAll("[^a-zA-Z0-9 .-]", "");
		}
		return null;
	}
	
	/**
	 * Retorna se uma String é vazia ou não
	 * 
	 * @param valor
	 * @return Se uma String é vazia ou não
	 */
	public static boolean isNotEmpty(String valor) {
		return (valor != null && valor.trim().length() > 0);
	}
	
	/**
	 * Retorna se uma String é vazia ou não
	 * 
	 * @param valor
	 * @return Se uma String é vazia ou não
	 */
	public static boolean isEmpty(String valor) {
		return !isNotEmpty(valor);
	}
	
	/**
	 * Faz conversão de String para Integer, com remoção dos caracteres inválidos.
	 * 
	 * @param value - Valor a ser convertido.
	 * @return Integer - Valor convertido.
	 */
	public static Integer convertToInteger(String value) {
		final String partial = value.replaceAll("[^0-9]", "");
		return Integer.valueOf(partial);
	}
	
	/**
	 * Trunca uma String, para o valor máximo de caracteres informados. Se o booleano, 
	 * mostrarReticencia for true, concatena reticencias ao final da String truncada
	 * 
	 * @param valor - String a truncar
	 * @param max - Maximo de caracteres
	 * @param mostrarReticencia - Se deve colocar reticencias no fim
	 * @return String truncada (com ou sem reticencias)
	 */
	public static String truncate(String valor, int max, boolean mostrarReticencia) {
		if(valor != null && valor.length() > max) {
			if(mostrarReticencia) {
				return valor.substring(0, max) + "...";
			}
			else {
				return valor.substring(0, max);
			}
		}
		return valor;
	}
	
	/**
	 * Substitui quebra de linha do java para o xhtml em um outputText por exemplo
	 * 
	 * @param valor - String à tratar
	 * @return String com os quebras de linhas do java substituidos por <br/>
	 */
	public static String quebraLinha(String valor){
		if(valor != null && valor.length() > 0){
			valor = valor.replaceAll("\n", "<br/>");
		}
		return valor;
	}
	/**
	 * Metodo que remove todo o html da string via java
	 * @param valor
	 * @return
	 */
	public static String removeExpressaoHtml(String valor){
		if(valor != null && valor.length() > 0){
			valor = valor.replaceAll("<br/>", "\n");
			valor = valor.replaceAll("<p/>", "\n");
			valor = valor.replaceAll("<p>", "\n");
			valor = valor.replaceAll("<.*?>", " ");
			valor = valor.replaceAll("&nbsp;", " ");
		}
		return valor;
	}
	
	/**
	 * Obtém as chaves de um objeto Properties que começam com o prefixo informado
	 * @param preffix Prefixo da chave
	 * @param p Properties contendo os valores
	 * @return Lista de chaves para o prefixo informado
	 */
	public static List<String> getKeysFromPreffix(String preffix, Properties p) {
		List<String> retorno = null;
		if(preffix != null && p != null) {
			for(Object k : p.keySet()) {
				if(k.toString().startsWith(preffix)) {
					if(retorno == null) {
						retorno = new ArrayList<String>();
					}
					retorno.add(k.toString());
				}
			}
		}
		return retorno;
	}
	
	/**
	 * 
	 * @param string
	 * @param arrayList
	 * @return
	 */
	public static boolean contemStringIgnoreUpperCase(String string, List<String> arrayList){
		boolean retorno = false;
		
		if (temValor(string)){
			for (String valor : arrayList){
				if (temValor(valor)){
					if (valor.compareToIgnoreCase(string) == 0){
						retorno = true;
						break;
					}
				}
			}
		}
		
		return retorno;
	}
}
