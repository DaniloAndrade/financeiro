package br.com.andradesolucoes.negocio;

import br.com.andradesolucoes.entitys.Conta;
import br.com.andradesolucoes.entitys.Usuario;
import br.com.andradesolucoes.qualify.Excluir;
import br.com.andradesolucoes.repository.Repository;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class ContaNegocio  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Repository<Conta> repository;
	
	public List<Conta> listar(Usuario usuario){
		Map<String, Object> filtro = new HashMap<>();
		filtro.put("usuario", usuario);
		return repository.findBy(Conta.LISTAR_POR_USUARIO, filtro);
	}
	
	public Conta carregar(Long codigo){
		return repository.findBy(codigo);
	}
	
	public void salvar(Conta conta) {
		conta.setDataCadastro(new Date());
		repository.update(conta);
	}
	
	
	public void  excluir(Conta conta) {
		repository.remove(conta);
	}
	
	public void tornarFavorita(Conta contaFavorita){
		Conta conta = buscarFavorita(contaFavorita.getUsuario());
		if(conta!= null){
			conta.setFavorita(false);
			repository.update(conta);
		}else{
			contaFavorita.setFavorita(true);
			repository.update(contaFavorita);
		}
		
	}
	
	public Conta buscarFavorita(Usuario usuario){

		Map<String, Object> filtro = new HashMap<>();
		filtro.put("usuario", usuario);
		List<Conta> contas = repository.findBy(Conta.BUSCAR_POR_FAVORITA_USUARIO, filtro);

		if( !contas.isEmpty()){
			return contas.get(0);
		}
		return null;
	}


	public void removePorUsuario(@Observes @Excluir Usuario usuario) {
		List<Conta> contas = listar(usuario);
		for (Conta conta : contas) {
			excluir(conta);
		}
	}
}
