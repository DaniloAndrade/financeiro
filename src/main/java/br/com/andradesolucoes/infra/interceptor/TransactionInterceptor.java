package br.com.andradesolucoes.infra.interceptor;

import java.io.Serializable;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import br.com.andradesolucoes.exceptions.DAOException;
import br.com.andradesolucoes.infra.Transactional;
import org.apache.log4j.Logger;

@Transactional
@Interceptor
public class TransactionInterceptor implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;
	@Inject
	private Logger logger;
	
	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws Exception{
		Object resultado = null;
		em.getTransaction().begin();
		logger.info("iniciando transação");
		try{
			resultado = ctx.proceed();
			em.getTransaction().commit();
			em.clear();
			logger.info("commitando transação");
			return resultado;
		
		}catch(PersistenceException e){
			logger.info("realizando rollback transação",e);
			em.getTransaction().rollback();
			throw new DAOException("transação não efetivada",e);
		}
			
	}
}
