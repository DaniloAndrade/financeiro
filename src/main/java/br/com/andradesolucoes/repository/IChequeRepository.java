package br.com.andradesolucoes.repository;

import java.util.List;

import br.com.andradesolucoes.entitys.Cheque;
import br.com.andradesolucoes.entitys.ChequeID;
import br.com.andradesolucoes.entitys.Conta;

public interface IChequeRepository {
	
	void salvar(Cheque cheque);
	void excluir(Cheque cheque);
	Cheque carregar(ChequeID id);
	List<Cheque> listar(Conta conta);
}
