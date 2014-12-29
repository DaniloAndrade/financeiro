package br.com.andradesolucoes.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by danilo on 28/12/14.
 */
public interface Repository<T> extends Serializable{
	void add(T t);

	void remove(T t);

	T update(T t);

	List<T> listAll();

	Long count();

	List<T> list(int firstResult, int maxResults);

	T findBy(Serializable id);
	
	List<T> findBy(Map<String,Object> filtro);

	List<T> findBy(String namedQuery,Map<String,Object> filtro);

	T load(T t);
	
}
