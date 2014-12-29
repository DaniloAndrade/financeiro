package br.com.andradesolucoes.negocio;

import br.com.andradesolucoes.entitys.Categoria;
import br.com.andradesolucoes.entitys.Conta;
import br.com.andradesolucoes.entitys.Lancamento;
import br.com.andradesolucoes.entitys.Usuario;
import br.com.andradesolucoes.repository.Repository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestScoped
public class LancamentoNegocio implements Serializable{
	
	@Inject
	private Repository<Lancamento> lancamentoRepository;
	@Inject
	private Repository<Conta> contas;
	@Inject
	private Repository<Usuario> usarios;
	@Inject
	private Repository<Categoria> categorias;
	
	
	
	public Lancamento salvar(Lancamento  lancamento){
		Categoria categoria = categorias.load(lancamento.getCategoria());
		Conta conta = contas.load(lancamento.getConta());
		Usuario usuario = usarios.load(lancamento.getUsuario());
		lancamento.setCategoria(categoria);
		lancamento.setConta(conta);
		lancamento.setUsuario(usuario);
		return lancamentoRepository.update(lancamento);
	}
	
	
	
	public void excluir(Lancamento lancamento){
		lancamentoRepository.remove(lancamento);
	}
	
	
	public Lancamento carregar(Long id){
		return lancamentoRepository.findBy(id);
	}
	
	
	public BigDecimal saldo(Conta conta, Date data){
		BigDecimal saldoInicial = conta.getSaldoInicial();
		Map<String,Object> filtro = new HashMap<>();
		filtro.put("conta",conta);
		filtro.put("data",data);

		List<Lancamento> lancamentos = lancamentoRepository.findBy("buscarPorContaEMenorIgualData", filtro);

		BigDecimal saldo = lancamentos.stream()
				.map(l -> l.getValor().multiply(new BigDecimal(l.getCategoria().getFator())))
				.reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
		return saldoInicial.add(saldo);
	}
	
	
	public List<Lancamento> listar(Conta conta, Date inicio, Date fim){
		Map<String,Object> filtro = new HashMap<>();
		String named = null;
		filtro.put("conta", conta);
		if(inicio!=null && fim != null ){
			filtro.put("dataInicio", inicio);
			filtro.put("dataFim", fim);
			named = Lancamento.BUSCAR_POR_PERIODO_E_CONTA;
		}else if(inicio != null){
			named = Lancamento.BUSCAR_POR_DATA_MAIOR_IGUAL_E_CONTA;
			filtro.put("data", inicio);
		}else if(fim !=null){
			named = Lancamento.BUSCAR_POR_DATA_MENOR_IGUAL_E_CONTA;
			filtro.put("data", fim);
		}
		return lancamentoRepository.findBy(named,filtro);
	}
}
