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

@Transactional
@Interceptor
public class TransactionInterceptor implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager em;
	
	@AroundInvoke
	public Object intercept(InvocationContext ctx) throws Exception{
		Object resultado = null;
		em.getTransaction().begin();
		System.out.println("iniciando transa��o...");
		try{
			resultado = ctx.proceed();
			em.getTransaction().commit();
			em.clear();
			System.out.println("fechando transa��o...");
			return resultado;
		
		}catch(PersistenceException e){
			System.out.println("transa��o n�o efetivada: "+ e.getMessage());
			em.getTransaction().rollback();
			throw new DAOException("transa��o n�o efetivada",e);
		}
			
	}
}
