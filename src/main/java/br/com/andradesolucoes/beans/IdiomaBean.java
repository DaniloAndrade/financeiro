package br.com.andradesolucoes.beans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.andradesolucoes.entitys.Idioma;
import br.com.andradesolucoes.infra.Transactional;
import br.com.andradesolucoes.repository.Repository;

@Named
@RequestScoped @Transactional
public class IdiomaBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("cdi-ambiguous-dependency")
	@Inject
	private Repository<Idioma> daoIdioma;
	
	private List<Idioma> idiomas;
	
	
	public List<Idioma> idiomas(){
		if(idiomas == null){
			idiomas = daoIdioma.listAll();
		}
		return idiomas;
	}


	public Idioma carregar(Idioma idioma) {
		return daoIdioma.findBy(idioma.getCodigo());
	}
	
	
}
