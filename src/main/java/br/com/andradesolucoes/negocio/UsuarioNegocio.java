package br.com.andradesolucoes.negocio;

import br.com.andradesolucoes.entitys.Usuario;
import br.com.andradesolucoes.entitys.enums.Permissao;
import br.com.andradesolucoes.exceptions.NegocioException;
import br.com.andradesolucoes.exceptions.UtilException;
import br.com.andradesolucoes.qualify.Criar;
import br.com.andradesolucoes.qualify.Excluir;
import br.com.andradesolucoes.repository.Repository;
import br.com.andradesolucoes.util.EmailUtil;
import br.com.andradesolucoes.util.MensagemUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequestScoped
public class UsuarioNegocio implements Serializable {
	

	@Inject
	private Repository<Usuario> usuarios;
	
	@Inject
	private Event<Usuario> event;
	

	public Usuario buscarPorId(Long id){
		return usuarios.findBy(id);
	}
	
	public Usuario buscarPorLogin(String login){
		Map<String,Object> filtro = new HashMap<>();
		filtro.put("login",login);
		return usuarios.findBy(filtro).get(0);
	}
	
	public Usuario salvar(Usuario usuario){
		if(usuario.getCodigo() ==null || usuario.getCodigo() ==0){
			usuario.addPermissao(Permissao.ROLE_USUARIO);
			usuario =  usuarios.update(usuario);
			event.select(new AnnotationLiteral<Criar>() {}).fire(usuario);
			return usuario;
		}else {
			return usuarios.update(usuario);
		}
	}
	
	public void excluir(Usuario usuario){
		event.select(new AnnotationLiteral<Excluir>() {}).fire(usuario);
		usuario.removeTodasPermissao();
		usuarios.remove(usuario);
	}
	
	public List<Usuario> listar() {
		return usuarios.listAll();
	}
	
	public void enviarEmailPosCadastramento(Usuario usuario) throws NegocioException{
		String [] info = usuario.getIdioma().getCodigoISO().split("_");
		Locale locale = new Locale(info[0],info[1]);
		String titulo = MensagemUtil.getMensagem(locale, "email_titulo");
		String mensagem = MensagemUtil.getMensagem(locale, "email_mensagem",usuario.getNome(),usuario.getLogin(),usuario.getSenha());
		try{
			EmailUtil emailUtil = new EmailUtil();
			emailUtil.enviarEmail("financeiro@andradesolucoes.com.br", usuario.getEmail(), titulo, mensagem);
			
		}catch(UtilException e){
			throw new NegocioException(e);
		}
	}
}
