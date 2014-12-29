package br.com.andradesolucoes.repository;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;

public class RepositoryFactory {
	
	@Inject
	private EntityManager em;
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Produces @Dependent
	public <T> Repository<T> create(final InjectionPoint injectionPoint){
		ParameterizedType parameterizedType = (ParameterizedType) injectionPoint.getType();
		Class classe = (Class) parameterizedType.getActualTypeArguments()[0];
		return new JPARepository<>(classe, em);
	}
}
