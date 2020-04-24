package br.com.kotar.core.util.hql.helper;

public class Where {

	private String whereExpression;

	public Where() {
	}

	public Where(String whereExpression) {
		super();
		this.whereExpression = whereExpression;
	}

	@Override
	public String toString() {
		return "Where [whereExpression=" + whereExpression + "]";
	}

	
	public String getWhereExpression() {
		return whereExpression;
	}

	
	public void setWhereExpression(String whereExpression) {
		this.whereExpression = whereExpression;
	}
}
