package br.com.andradesolucoes.negocio;

import br.com.andradesolucoes.entitys.Cheque;
import br.com.andradesolucoes.entitys.ChequeID;
import br.com.andradesolucoes.entitys.Conta;
import br.com.andradesolucoes.entitys.Lancamento;
import br.com.andradesolucoes.exceptions.NegocioException;
import br.com.andradesolucoes.repository.Repository;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChequeNegocio implements Serializable{
	
	@Inject
	private Repository<Cheque> repository;
	
	public void salvar(Cheque cheque){
		this.repository.update(cheque);
	}
	
	
	public long salvarSequencia(Conta conta, Long chequeInicial, Long chequeFinal){
		Cheque cheque = null;
		Cheque chequeVerifica = null;
		ChequeID chequeID = null;
		long contaTotal = 0;
		
		for (long i = chequeInicial; i < chequeFinal; i++) {
			chequeID = new ChequeID();
			chequeID.setNumero(i);
			chequeID.setCodigoConta(conta.getCodigo());
			chequeVerifica = this.carregar(chequeID);
			
			if(chequeVerifica == null){
				cheque = new Cheque();
				cheque.setChequeID(chequeID);
				cheque.setSituacao(Cheque.SITUACAO_CHEQUE_NAO_EMITIDO);
				cheque.setDataCadastro(Calendar.getInstance().getTime());
				this.salvar(cheque);
				contaTotal++;
			}
		}
		return contaTotal;
	}


	public Cheque carregar(ChequeID chequeID) {
		return this.repository.findBy(chequeID);
	}
	
	public void excluir(Cheque cheque) throws NegocioException{
		if(cheque.getSituacao() == Cheque.SITUACAO_CHEQUE_NAO_EMITIDO){
			this.repository.remove(cheque);
		}else{
			throw new NegocioException("Não é possivel remove cheque, situação não permite esta operação.");
		}
	}
	
	
	public List<Cheque> listar(Conta conta){
		Map<String,Object> filtro = new HashMap<>();
		filtro.put("conta", conta);
		return repository.findBy(Cheque.BUSCAR_POR_CONTA, filtro);
	}
	
	
	public void cancelarCheque(Cheque cheque) throws NegocioException{
		if(cheque.isSituacao(Cheque.SITUACAO_CHEQUE_NAO_EMITIDO) 
				|| cheque.isSituacao(Cheque.SITUACAO_CHEQUE_CANCELADO)){
			cheque.setSituacao(Cheque.SITUACAO_CHEQUE_CANCELADO);
			this.repository.update(cheque);
		}else{
			throw new NegocioException("Não é possivel cancelar cheque, situação não permite esta operação.");
		}
	}
	
	public void baixarCheque(ChequeID chequeID, Lancamento lancamento){
		Cheque cheque = this.repository.findBy(chequeID);
		if(cheque != null){
			cheque.setSituacao(Cheque.SITUACAO_CHEQUE_BAIXADO);
			cheque.setLancamento(lancamento);
			this.repository.update(cheque);
		}
	}
	
	
	public void desvinculaLancamento(Conta conta, Long numeroCheque)throws NegocioException{
		ChequeID chequeID = new ChequeID();
		chequeID.setCodigoConta(conta.getCodigo());
		chequeID.setNumero(numeroCheque);
		Cheque  cheque = this.carregar(chequeID);
		
		if(cheque.isSituacao(Cheque.SITUACAO_CHEQUE_CANCELADO)){
			throw new NegocioException("Não é possível usar cheque cancelado.");
		}else{
			cheque.setSituacao(Cheque.SITUACAO_CHEQUE_NAO_EMITIDO);
			Lancamento lancamento = cheque.getLancamento();
			lancamento.setCheque(null);
			cheque.setLancamento(null);
			this.salvar(cheque);
		}
	}
}
