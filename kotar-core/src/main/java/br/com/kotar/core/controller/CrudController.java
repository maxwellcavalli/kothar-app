package br.com.kotar.core.controller;

import org.springframework.data.domain.Page;

import br.com.kotar.core.domain.CrudDomain;
import br.com.kotar.core.helper.datatable.PageFilter;
import br.com.kotar.core.service.BaseCrudService;

public abstract class CrudController<T extends CrudDomain> extends BaseController<T>{

	public abstract BaseCrudService<T> getService();	
	
	protected Page<T> onSearch(PageFilter pageFilter) throws Exception {
		String filter = "%%";
		if (pageFilter.getFilterValue() != null) {
			filter = (String) pageFilter.getFilterValue();
		}

		if (filter.indexOf("%") == -1) {
			filter = "%" + filter + "%";
		}
		
		Page<T> paged = getService().findByNomeLikeIgnoreCase(filter, getPageable(pageFilter));
		
		return paged;
	}
	
	@Override
	public void beforeSave(T c) {
	}

	@Override
	public void validationBeforeSave(T c) throws Exception {
	}


}