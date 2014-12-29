package br.com.andradesolucoes.negocio;

import br.com.andradesolucoes.entitys.Categoria;
import br.com.andradesolucoes.entitys.Usuario;
import br.com.andradesolucoes.qualify.Criar;
import br.com.andradesolucoes.qualify.Excluir;
import br.com.andradesolucoes.repository.Repository;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class CategoriaNegocio implements Serializable{

	@Inject
	private Repository<Categoria> repository;
	
	public List<Categoria> listar(Usuario usuario){
		Map<String,Object> filtro = new HashMap<>();
		filtro.put("usuario", usuario);
		return repository.findBy(Categoria.BUSCAR_POR_USUARIO, filtro);
	}
	
	
	public Categoria salvar(Categoria categoria) {
		Categoria pai = categoria.getPai();
		
		if(pai == null){
			String msg = "A Categoria "+ categoria.getDescricao() + " deve ter um pai definido";
			throw new IllegalArgumentException(msg);
		}
		
		boolean mudouFator = pai.getFator() != categoria.getFator();
		
		categoria.setFator(pai.getFator());
		categoria = repository.update(categoria);
		
		if(mudouFator){
			categoria = this.carregar(categoria.getCodigo());
			this.replicarFator(categoria, categoria.getFator());
		}
		
		return categoria;
	}


	public Categoria carregar(Integer codigo) {
		return repository.findBy(codigo);
	}


	private void replicarFator(Categoria categoria, int fator) {
		if (categoria.getFilhos()!=null) {
			for (Categoria filho : categoria.getFilhos()) {
				filho.setFator(fator);
				this.repository.add(filho);
				this.replicarFator(filho, fator);
			}
		}
	}
	
	public void  excluir(Categoria categoria) {
		
		if(categoria.getPai() == null){
			this.repository.remove(categoria);
		}else{
			
			Categoria pai = categoria.getPai();
			pai.removeCategoria(categoria);
			
			categoria.setPai(null);
			
			this.repository.update(pai);
			this.repository.remove(categoria);
		}
		
	}
	
	
	public void  excluir(@Observes @Excluir Usuario usuario) {
		List<Categoria> categorias = listar(usuario);
		for (Categoria categoria : categorias) {
			this.repository.remove(categoria);
		}
		
	}
	
	
	public void salvarEstruturaPadrao(@Observes @Criar Usuario usuario){
		Categoria despesas = new Categoria(null, usuario, "DESPESAS", -1);

		despesas.addCategoria(new Categoria(null, usuario, "Moradia", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Alimetação", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Vestuário", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Deslocamento", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Cuidados Pessoais", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Educação", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Saúde", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Lazer", -1));
		despesas.addCategoria(new Categoria(null, usuario, "Despesas Financeiras", -1));
		repository.add(despesas);
		
		Categoria receitas = new Categoria(null, usuario, "RECEITAS", 1);
		receitas.addCategoria(new Categoria(null, usuario, "Salário", 1));
		receitas.addCategoria(new Categoria(null, usuario, "Restituições", 1));
		receitas.addCategoria(new Categoria(null, usuario, "Rendimento", 1));
		
		repository.add(receitas);

	}
	
	
	
	
}
