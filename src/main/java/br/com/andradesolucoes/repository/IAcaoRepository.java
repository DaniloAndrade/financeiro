package br.com.andradesolucoes.repository;

import java.util.List;

import br.com.andradesolucoes.entitys.Acao;
import br.com.andradesolucoes.entitys.Usuario;

public interface IAcaoRepository {
	Acao salvar(Acao acao);
	void excluir(Acao acao);
	Acao carregar(String codigo);
	List<Acao> listar(Usuario usuario);
}
