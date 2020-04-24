package br.com.kotar.core.repository;

public interface TransformData<T extends Object> {

	public T transform(Object[] row) throws Exception;
	
	
}
