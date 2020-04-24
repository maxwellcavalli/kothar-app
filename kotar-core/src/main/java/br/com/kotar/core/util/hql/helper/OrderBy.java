package br.com.kotar.core.util.hql.helper;

public class OrderBy {

	private String orderByExpression;

	public OrderBy() {
	}

	public OrderBy(String orderByExpression) {
		super();
		this.orderByExpression = orderByExpression;
	}

	@Override
	public String toString() {
		return "OrderBy [orderByExpression=" + orderByExpression + "]";
	}

	public String getOrderByExpression() {
		return orderByExpression;
	}

	public void setOrderByExpression(String orderByExpression) {
		this.orderByExpression = orderByExpression;
	}

}
