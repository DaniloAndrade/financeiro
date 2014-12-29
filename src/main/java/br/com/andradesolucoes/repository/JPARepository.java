package br.com.andradesolucoes.repository;

import com.uaihebert.uaicriteria.UaiCriteria;
import com.uaihebert.uaicriteria.UaiCriteriaFactory;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class JPARepository<T> implements Repository<T> {
	private final Class<T> classe;
	private EntityManager em;
	
	
	public JPARepository(Class<T> classe, EntityManager em) {
		this.classe = classe;
		this.em = em;
	}

	@Override
	public void add(T t) {
		em.persist(t);
	}

	@Override
	public void remove(T t) {
		em.remove(em.merge(t));
	}

	@Override
	public T update(T t) {
		return em.merge(t);
	}

	@Override
	public List<T> listAll() {
		CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(classe);
		query.select(query.from(classe));
		List<T> lista = em.createQuery(query).getResultList();
		return lista;
	}
	
	@Override
	public Long count() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> alias = query.from(classe);
		query.select(builder.count(alias));
		Long result = em.createQuery(query).getSingleResult();
		return result;
	}

	@Override
	public List<T> list(int firstResult, int maxResults) {
		CriteriaQuery<T> query = em.getCriteriaBuilder().createQuery(classe);
		query.select(query.from(classe));
		List<T> lista = em.createQuery(query).setFirstResult(firstResult)
				.setMaxResults(maxResults).getResultList();
		return lista;
	}

	@Override
	public T findBy(Serializable id) {
		T instancia = em.find(classe, id);
		return instancia;
	}

	@Override
	public List<T> findBy(Map<String, Object> filtro) {
		UaiCriteria<T> criteria = UaiCriteriaFactory.createQueryCriteria(em,classe);
		filtro.forEach(criteria::andEquals);
		return criteria.getResultList();
	}

	@Override
	public List<T> findBy(String namedQuery, Map<String, Object> filtro) {
		TypedQuery<T> query = em.createNamedQuery(namedQuery, classe);
		filtro.forEach(query::setParameter);
		return query.getResultList();
	}

	@Override
	public T load(T t){
		return em.merge(t);
	}

	
	protected EntityManager getEntityManager() {
		return em;
	}

}