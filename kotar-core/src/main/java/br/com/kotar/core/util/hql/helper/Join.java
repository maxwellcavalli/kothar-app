package br.com.kotar.core.util.hql.helper;

public class Join {

	private String joinExpression;
	private String alias;
	private String joinType;

	public Join(String joinExpression, String alias, String joinType) {
		super();
		this.joinExpression = joinExpression;
		this.alias = alias;
		this.joinType = joinType;
	}

	public String getJoinExpression() {
		return joinExpression;
	}

	public void setJoinExpression(String joinExpression) {
		this.joinExpression = joinExpression;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getJoinType() {
		return joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	@Override
	public String toString() {
		return "Join [joinExpression=" + joinExpression + ", alias=" + alias + ", joinType=" + joinType + "]";
	}

}
