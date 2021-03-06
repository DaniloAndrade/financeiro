package br.com.andradesolucoes.beans;

import br.com.andradesolucoes.entitys.Conta;
import br.com.andradesolucoes.entitys.Usuario;
import br.com.andradesolucoes.entitys.enums.Permissao;
import br.com.andradesolucoes.exceptions.NegocioException;
import br.com.andradesolucoes.infra.Transactional;
import br.com.andradesolucoes.negocio.ContaNegocio;
import br.com.andradesolucoes.negocio.UsuarioNegocio;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;


@Named
@RequestScoped
public class UsuarioBean implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private UsuarioNegocio usuarioNegocio;
	@Inject
	private ContaNegocio contaNegocio;

	private Usuario usuario = new Usuario();
	private String confirmaSenha;
	private String destinoSalvar;

	private List<Usuario> lista;
	
	private Conta conta = new Conta();

	public String novo(){
		this.destinoSalvar = "usuarioSucesso";
		this.usuario = new Usuario();
		this.usuario.setAtivo(true);
		return "usuario";
	}
	
	
	@Transactional
	public String salvar(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		if (!usuario.getSenha().equalsIgnoreCase(confirmaSenha)) {
			context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Senha confirmada incorretamente",""));
			return null;
		}
		
		Usuario usuarioSalvo = usuarioNegocio.salvar(usuario);
		

		if(this.conta.getDescricao() != null){
			this.conta.setUsuario(usuarioSalvo);
			this.conta.setFavorita(true);
			this.contaNegocio.salvar(this.conta);
			
		}
		
		if(this.destinoSalvar.equals("usuarioSucesso")){
			try{
				usuarioNegocio.enviarEmailPosCadastramento(usuarioSalvo);
				
			}catch(NegocioException e){
				FacesMessage message = new FacesMessage("Não foi possivel enviar o e-mail de cadastro do usuário. Erro: " +e.getMessage() );
				context.addMessage(null, message);
				throw new RuntimeException(e);
			}
		}
		
		return this.destinoSalvar;
	}
	
	
	public String editar(){
		this.confirmaSenha = this.usuario.getSenha();
		return "/publico/usuario";
	}
	
	@Transactional
	public String excluir(){
		
		usuarioNegocio.excluir(usuario);
		this.lista = null;
		return null;
	}
	
	@Transactional
	public String ativar(){
		if(this.usuario.isAtivo()){
			this.usuario.setAtivo(false);
		}else{
			this.usuario.setAtivo(true);
		}
		
		usuarioNegocio.salvar(usuario);
		return null;
	}
	
	@Transactional
	public void atribuirPermissao(Usuario usuario, String permissao){
		if(usuario.getPermissao().contains(permissao)){
			usuario.removePermissao(Permissao.valueOf(permissao));
		}else{
			usuario.addPermissao(Permissao.valueOf(permissao));
		}
		usuarioNegocio.salvar(usuario);
	}
	
	
	@Transactional
	public List<Usuario> getlista() {
		if(this.lista == null){
			this.lista = usuarioNegocio.listar();
		}
		return this.lista;
	}
	
	
	
	public String getConfirmaSenha() {
		return confirmaSenha;
	}
	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}
	
	
	
	public Usuario getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public String getDestinoSalvar() {
		return destinoSalvar;
	}
	
	public void setDestinoSalvar(String destinoSalvar) {
		this.destinoSalvar = destinoSalvar;
	}
	
	public Conta getConta() {
		return conta;
	}
}
