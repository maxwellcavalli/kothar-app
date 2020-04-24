package br.com.kotar.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataUtil {

	/**
	 * Converte uma String em Data "dd/MM/yyyy"
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static Date paraData(String data) {
		final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			return sdf.parse(data);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Converte um Date para dd/MM/yyyy HH:mm:ss
	 * 
	 * @param data
	 * @return
	 */
	public static String paraStringComHora(Date data) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			return sdf.format(data);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Converte um Date para HH:mm:ss
	 * 
	 * @param data
	 * @return
	 */
	public static String paraStringHora(Date data) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			return sdf.format(data);
		} catch (final Exception e) {
			return null;
		}

	}

	/**
	 * Retorno o primeiro dia do mes atual
	 * 
	 * @return
	 */
	public static Date primeiroDiaMesAtual() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Retorna o ultimo dia do mes atual
	 * 
	 * @return
	 */
	public static Date ultimoDiaMesAtual() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTime();
	}

	/**
	 * Adiciona HORA/MINUTO/SEGUNDO em uma determinada data
	 * 
	 * @param data
	 * @param hora
	 * @param minuto
	 * @param segundo
	 * @return
	 */
	public static Date adicionaHoraData(Date data, int hora, int minuto, int segundo) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);

		calendar.set(Calendar.HOUR_OF_DAY, hora);
		calendar.set(Calendar.MINUTE, minuto);
		calendar.set(Calendar.SECOND, segundo);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Retorna a mesma data com a �ltima hora do dia: 23:59:59.999
	 * 
	 * @param data
	 * @return
	 */
	public static Date dataComHoraFim(Date data) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);

		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		return calendar.getTime();
	}

	/**
	 * Retorna a mesma data com a primeira hora do dia: 00:00:00.000
	 * 
	 * @param data
	 * @return
	 */
	public static Date dataComHoraInicio(Date data) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Adiciona N meses em uma data
	 * 
	 * @param qtdMes
	 * @param data
	 * @return
	 */
	public static Date adicionaMesData(int qtdMes, Date data) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.add(Calendar.MONTH, qtdMes);

		return calendar.getTime();
	}

	/**
	 * Criar um objeto do tipo Date atraves dos parametros informados
	 * 
	 * @param dia
	 * @param mes
	 * @param ano
	 * @param hora
	 * @param minuto
	 * @param segundo
	 * @return
	 */
	public static Date paraData(int dia, int mes, int ano, int hora, int minuto, int segundo) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, dia);
		calendar.set(Calendar.MONTH, mes - 1);
		calendar.set(Calendar.YEAR, ano);
		calendar.set(Calendar.HOUR_OF_DAY, hora);
		calendar.set(Calendar.MINUTE, minuto);
		calendar.set(Calendar.SECOND, segundo);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Converte um Data para String
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String paraString(Date data) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			return sdf.format(data);
		} catch (final Exception e) {
			return "";
		}
	}

	/**
	 * Retorna o ultimo dia do mes/ano informado
	 * 
	 * @param mes
	 * @param ano
	 * @return
	 */
	public static Date ultimoDiaMes(int mes, int ano) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, mes);
		calendar.set(Calendar.YEAR, ano);

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
		calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
		calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
		calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));

		return calendar.getTime();
	}

	/**
	 * Retorna o primeiro dia do mes/ano informado
	 * 
	 * @param mes
	 * @param ano
	 * @return
	 */
	public static Date primeiroDiaMes(int mes, int ano) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, (mes - 1));
		calendar.set(Calendar.YEAR, ano);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Retorna o dia e mes correntes com o ano informado
	 * 
	 * @param ano
	 * @return
	 */
	public static Date diaCorrenteAno(int ano) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, ano);

		return calendar.getTime();

	}

	/**
	 * Retorna o ano corrente
	 * 
	 * @return
	 */
	public static int anoCorrente() {
		final Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * Retorna a data atual com hora 00:00:00
	 * 
	 * @return
	 */
	public static Date dataAtual() {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/**
	 * Retorna a data e hora.
	 * 
	 * @return
	 */
	public static Date dataHoraAtual() {
		final Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	/**
	 * Retorna o primeiro dia do ano atual com hora 00:00:00
	 * 
	 * @return
	 */
	public static Date primeiroDiaAnoAtual() {
		final Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Retorna o ultimo dia do ano atual com hora 00:00:00
	 * 
	 * @return
	 */
	public static Date ultimoDiaAnoAtual() {
		final Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.add(Calendar.YEAR, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	/**
	 * Retorna o mes por extenso
	 * 
	 * @param mes
	 * @return
	 */
	public static String retornaMesString(int mes) {
		Map<Integer, String> mapMes = retornaMesesHashMap();
		return mapMes.get(mes);
	}

	/**
	 * Retorna os meses
	 * 
	 * @return
	 */
	public static Map<Integer, String> retornaMesesHashMap() {
		Map<Integer, String> mapMes = new HashMap<Integer, String>();

		mapMes.put(1, "Janeiro");
		mapMes.put(2, "Fevereiro");
		mapMes.put(3, "Mar�o");
		mapMes.put(4, "Abril");
		mapMes.put(5, "Maio");
		mapMes.put(6, "Junho");
		mapMes.put(7, "Julho");
		mapMes.put(8, "Agosto");
		mapMes.put(9, "Setembro");
		mapMes.put(10, "Outubro");
		mapMes.put(11, "Novembro");
		mapMes.put(12, "Dezembro");

		return mapMes;
	}
	
	/**
	 * Retorna inteiro o mes
	 * 
	 * @param mes
	 * @return
	 */
	public static Integer retornaMesInteger(String mes) {
		Map<String, Integer> mapMes = new HashMap<String, Integer>();
		mapMes.put("Janeiro", 1);
		mapMes.put("Fevereiro", 2);
		mapMes.put("Mar�o", 3);
		mapMes.put("Abril", 4);
		mapMes.put("Maio", 5);
		mapMes.put("Junho", 6);
		mapMes.put("Julho", 7);
		mapMes.put("Agosto", 8);
		mapMes.put("Setembro", 9);
		mapMes.put("Outubro", 10);
		mapMes.put("Novembro", 11);
		mapMes.put("Dezembro", 12);

		return mapMes.get(mes);
	}

	public static String retornaMesAbreviadoString(int mes) {
		Map<Integer, String> mapMes = new HashMap<Integer, String>();
		mapMes.put(1, "Jan");
		mapMes.put(2, "Fev");
		mapMes.put(3, "Mar");
		mapMes.put(4, "Abr");
		mapMes.put(5, "Mai");
		mapMes.put(6, "Jun");
		mapMes.put(7, "Jul");
		mapMes.put(8, "Ago");
		mapMes.put(9, "Set");
		mapMes.put(10, "Out");
		mapMes.put(11, "Nov");
		mapMes.put(12, "Dez");

		return mapMes.get(mes);
	}

	public static Calendar retornaCalendar(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		return calendar;
	}
	
	public static Long diferrenceBetweenTwoDatesInDays(Date ini, Date end) {
		long secs = (end.getTime() - ini.getTime()) / 1000;
		long hours = secs / 3600;
		long days = hours / 24;
		
		return days;
	}
	
	public static Long diferrenceBetweenTwoDatesInHours(Date ini, Date end) {
		long secs = (end.getTime() - ini.getTime()) / 1000;
		long hours = secs / 3600;
				
		return hours;
	}
	

	/**
	 * Efetua o calculo da difereca entre duas datas e retorna a string do valor
	 * 
	 * @param ini
	 * @param end
	 * @return
	 */
	public static String diferrenceBetweenTwoDates(Date ini, Date end) {
		long secs = (end.getTime() - ini.getTime()) / 1000;
		long hours = secs / 3600;
		long days = hours / 24;
		hours = hours % 24;

		secs = secs % 3600;
		long mins = secs / 60;
		secs = secs % 60;

		if (days > 0) {
			return days + " dias " + hours + " h " + mins + " min " + secs + " seg";
		} else {
			return hours + " h " + mins + " min " + secs + " seg";
		}
	}

	public static int getYear(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);

		return c.get(Calendar.YEAR);
	}

	public static int getMonth(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);

		return c.get(Calendar.MONTH) + 1;
	}

	public static Date firstDayOfYear(int year) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.YEAR, year);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar.getTime();
	}

	public static Date removeHora(final Date data) {
		if (data != null) {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				return sdf.parse(sdf.format(data));
			} catch (final ParseException pe) {
				return null;
			}
		}
		return null;
	}

	public static Date decDay(Date data, int qtde) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);

		calendar.add(Calendar.DAY_OF_MONTH, -qtde);

		return calendar.getTime();
	}

	public static String dataExtenso(Date data) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd ' de ' MMMM ' de ' yyyy");
			return sdf.format(data);
		} catch (final Exception e) {
			return "";
		}
	}

	public static Integer mesAtual() {
		final Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1;
	}

	public static String mesExtenso(final Integer mes) {
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
			final Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.MONTH, (mes - 1));
			return sdf.format(cal.getTime());
		} catch (final Exception e) {
			return "xxxx";
		}
	}
	
}
