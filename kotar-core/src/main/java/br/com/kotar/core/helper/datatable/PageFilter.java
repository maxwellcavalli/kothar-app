package br.com.kotar.core.helper.datatable;

import java.io.Serializable;

public class PageFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object filterValue;
	private Page page;
	private Sort sort;

	public Object getFilterValue() {
		return filterValue;
	}

	public void setFilterValue(Object filterValue) {
		this.filterValue = filterValue;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Sort getSort() {
		return sort;
	}

	public void setSort(Sort sort) {
		this.sort = sort;
	}

}
