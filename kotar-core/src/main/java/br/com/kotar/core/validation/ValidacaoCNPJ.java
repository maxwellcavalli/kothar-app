package br.com.kotar.core.validation;

public class ValidacaoCNPJ {

	private static boolean numeroInvalido(String cnpj) {
		for (int i = 0; i < 10; i++) {
			String str = String.valueOf(i);
			while (str.length() < 14) {
				str = str + String.valueOf(i);
			}

			if (cnpj.equals(str)) {
				return true;
			}
		}

		return false;
	}

	static public boolean valida(String str_cnpj) {
		if (numeroInvalido(str_cnpj)) {
			return false;
		}

		int soma = 0, dig;
		String cnpj_calc = str_cnpj.substring(0, 12);

		if (str_cnpj.length() != 14) {
			return false;
		}

		final char[] chr_cnpj = str_cnpj.toCharArray();

		/* Primeira parte */
		for (int i = 0; i < 4; i++) {
			if (((chr_cnpj[i] - 48) >= 0) && ((chr_cnpj[i] - 48) <= 9)) {
				soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
			}
		}
		for (int i = 0; i < 8; i++) {
			if (((chr_cnpj[i + 4] - 48) >= 0) && ((chr_cnpj[i + 4] - 48) <= 9)) {
				soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
			}
		}
		dig = 11 - (soma % 11);

		cnpj_calc += ((dig == 10) || (dig == 11)) ? "0" : Integer.toString(dig);

		/* Segunda parte */
		soma = 0;
		for (int i = 0; i < 5; i++) {
			if (((chr_cnpj[i] - 48) >= 0) && ((chr_cnpj[i] - 48) <= 9)) {
				soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
			}
		}
		for (int i = 0; i < 8; i++) {
			if (((chr_cnpj[i + 5] - 48) >= 0) && ((chr_cnpj[i + 5] - 48) <= 9)) {
				soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
			}
		}
		dig = 11 - (soma % 11);
		cnpj_calc += ((dig == 10) || (dig == 11)) ? "0" : Integer.toString(dig);

		return str_cnpj.equals(cnpj_calc);
	}

}
