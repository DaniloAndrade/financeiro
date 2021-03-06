package br.com.andradesolucoes.beans;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.andradesolucoes.entitys.Conta;
import br.com.andradesolucoes.infra.Transactional;
import br.com.andradesolucoes.negocio.ContaNegocio;

@Named
@RequestScoped
public class ContaBean {
	@Inject
	private ContextoBean contextoBean;
	@Inject
	private ContaNegocio negocio;
	private Conta selecionada = new Conta();
	private List<Conta> contas = null;
	
	@Transactional
	public void salvar(){
		this.selecionada.setUsuario(contextoBean.getUsuarioLogado());
		negocio.salvar(selecionada);
		this.selecionada = new Conta();
		this.contas = null;
		
	}
	public void editar(){
		
	}
	
	@Transactional
	public void excluir(){
		negocio.excluir(selecionada);
		this.selecionada = new Conta();
		this.contas = null;
	}
	
	@Transactional
	public void tornarFavorita(){
		negocio.tornarFavorita(selecionada);
		this.selecionada = new Conta();
	}
	
	
	public Conta getSelecionada() {
		return selecionada;
	}
	
	public void setSelecionada(Conta selecionada) {
		this.selecionada = selecionada;
	}
	
	@Transactional
	public List<Conta> getContas() {
		if (this.contas == null) {
			this.contas = negocio.listar(contextoBean.getUsuarioLogado());
		}
		return contas;
	}
	
}
