package br.com.andradesolucoes.produces;

import org.apache.log4j.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * Created by danilo on 27/12/14.
 */
public class LoggerProduces {
	
	@Produces
	public Logger createLogger(final InjectionPoint point){
		final Class<?> clazz = point.getBean().getBeanClass();
		return Logger.getLogger(clazz);
	}
}
