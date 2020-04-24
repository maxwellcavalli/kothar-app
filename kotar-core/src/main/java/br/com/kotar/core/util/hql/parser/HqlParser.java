package br.com.kotar.core.util.hql.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.com.kotar.core.util.hql.helper.Join;
import br.com.kotar.core.util.hql.helper.OrderBy;
import br.com.kotar.core.util.hql.helper.ParsedObject;
import br.com.kotar.core.util.hql.helper.Where;

public class HqlParser {
	
	public static ParsedObject parse(String hql) {
		ParsedObject parsedObject = new ParsedObject();

		StringTokenizer st = new StringTokenizer(hql);
		int controle = 0;
		
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreElements()) {
			String aux = st.nextToken();
			
			tokens.add(aux);
		}

		int i = 0; 
		while (i < tokens.size()){
			String aux = tokens.get(i);
			if (aux.indexOf("(") > -1) {
				controle++;
			}

			if (aux.indexOf(")") > -1) {
				controle--;
			}

			if (controle != 0) {
				i++;
				continue;
			}

			if (aux.equalsIgnoreCase("join")) {
				String joinType = "";
				
				if (i - 1 > 0){
					String auxJoinType = tokens.get(i - 1);
					if (auxJoinType.equals("inner") || auxJoinType.equals("left")){
						joinType = auxJoinType;
					}
				}
				
				
				if (i + 1 < tokens.size()){
					i++;
					
					aux = tokens.get(i);
					boolean isFetch = aux.equalsIgnoreCase("fetch");

					if (isFetch) {
						if (i + 1 < tokens.size()){
							i++;
							aux = tokens.get(i);
						}
					}

					String join = aux;
					String alias = "";

					//@formatter:off
					if (i + 1 < tokens.size()){
						i++;
						aux = tokens.get(i);
						if (!aux.equalsIgnoreCase("inner") && 
							!aux.equalsIgnoreCase("left") && 
							!aux.equalsIgnoreCase("join") && 
							!aux.equalsIgnoreCase("select") && 
							!aux.equalsIgnoreCase("where")) {
							alias = aux;
						}
					}
					//@formatter:on
					parsedObject.getListJoin().add(new Join(join, alias, joinType));
				}
			}

			if (aux.equalsIgnoreCase("where")) {
				StringBuilder whereExpression = new StringBuilder();
				whereExpression.append(aux).append(" ");
				i++;
				
				while (i < tokens.size()){
					aux = tokens.get(i);
					boolean isOrderBy = aux.equalsIgnoreCase("order");
					if (!isOrderBy) {
						whereExpression.append(aux).append(" ");
					} else {
						break;
					}
					i++;
				}

				parsedObject.setWhere(new Where(whereExpression.toString()));
			}

			if (aux.equalsIgnoreCase("order")) {
				StringBuilder orderByExpression = new StringBuilder();
				orderByExpression.append(aux).append(" ");
				i++;

				while (i < tokens.size()){
					aux = tokens.get(i);
					orderByExpression.append(aux).append(" ");
					i++;
				}

				parsedObject.setOrderBy(new OrderBy(orderByExpression.toString()));
			}
			
			i++;
		}
		
		return parsedObject;
	}
	
	
}
