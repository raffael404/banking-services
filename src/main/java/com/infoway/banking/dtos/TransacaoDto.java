package com.infoway.banking.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.infoway.banking.entities.Transacao;

public class TransacaoDto {
	
	private Double valor;
	private String bancoOrigem;
	private String contaOrigem;
	private String bancoDestino;
	private String contaDestino;
	
	public TransacaoDto() {}
	
	public TransacaoDto(Transacao transacao) {
		this.valor = transacao.getValor();
		if (transacao.getOrigem() == null) {
			this.bancoOrigem = "";
			this.contaOrigem = "";
		} else {
			this.bancoOrigem = transacao.getOrigem().getBanco().getCodigo();
			this.contaOrigem = transacao.getOrigem().getNumero();
		}
		if (transacao.getDestino() == null) {
			this.bancoDestino = "";
			this.contaDestino = "";
		} else {
			this.bancoDestino = transacao.getDestino().getBanco().getCodigo();
			this.contaDestino = transacao.getDestino().getNumero();
		}
	}
	
	@NotNull(message = "Valor não pode ser vazio.")
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	@NotEmpty(message = "Código do banco de origem não pode ser vazio.")
	public String getBancoOrigem() {
		return bancoOrigem;
	}
	public void setBancoOrigem(String bancoOrigem) {
		this.bancoOrigem = bancoOrigem;
	}
	
	@NotEmpty(message = "Número da conta de origem não pode ser vazio.")
	public String getContaOrigem() {
		return contaOrigem;
	}
	public void setContaOrigem(String contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	
	@NotEmpty(message = "Código do banco de destino não pode ser vazio.")
	public String getBancoDestino() {
		return bancoDestino;
	}
	public void setBancoDestino(String bancoDestino) {
		this.bancoDestino = bancoDestino;
	}
	
	@NotEmpty(message = "Número da conta de destino não pode ser vazio.")
	public String getContaDestino() {
		return contaDestino;
	}
	public void setContaDestino(String contaDestino) {
		this.contaDestino = contaDestino;
	}
	
	@Override
	public String toString() {
		return "TransacaoDto [valor=" + valor + ", bancoOrigem=" + bancoOrigem + ", contaOrigem=" + contaOrigem
				+ ", bancoDestino=" + bancoDestino + ", contaDestino=" + contaDestino + "]";
	}
	
}
