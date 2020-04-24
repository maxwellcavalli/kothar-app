package br.com.kotar.core.util.hql.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ParsedObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Join> listJoin;
	private Where where;
	private OrderBy orderBy;

	public ParsedObject() {
		listJoin = new ArrayList<Join>();
		where = new Where();
		orderBy = new OrderBy();
	}

	public List<Join> getListJoin() {
		return listJoin;
	}

	public void setListJoin(List<Join> listJoin) {
		this.listJoin = listJoin;
	}

	public Where getWhere() {
		return where;
	}

	public void setWhere(Where where) {
		this.where = where;
	}

	public OrderBy getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

}
