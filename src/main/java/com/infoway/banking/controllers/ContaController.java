package com.infoway.banking.controllers;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infoway.banking.dtos.ContaDto;
import com.infoway.banking.dtos.TransacaoDto;
import com.infoway.banking.entities.Banco;
import com.infoway.banking.entities.Cliente;
import com.infoway.banking.entities.Conta;
import com.infoway.banking.entities.Transacao;
import com.infoway.banking.enums.TipoTransacao;
import com.infoway.banking.responses.Response;
import com.infoway.banking.services.BancoService;
import com.infoway.banking.services.ClienteService;
import com.infoway.banking.services.ContaService;
import com.infoway.banking.services.TransacaoService;

@RestController
@RequestMapping("/banking/conta")
public class ContaController {
	
	private static final Logger log = LoggerFactory.getLogger(ContaController.class);
	
	@Autowired
	private ContaService contaService;
	
	@Autowired
	private BancoService bancoService;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private TransacaoService transacaoService;
	
	private ContaController() {}
	
	@PostMapping(value = "/abrir")
	public ResponseEntity<Response<ContaDto>> abrir(@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Abrindo conta: {}", contaDto.toString());
		
		Optional<Cliente> cliente = clienteService.buscar(contaDto.getCpfCliente());
		if(!cliente.isPresent())
			result.addError(new ObjectError("cliente", "Cliente inexistente."));
		
		Optional<Banco> banco = bancoService.buscar(contaDto.getCodigoBanco());
		if (!banco.isPresent())
			result.addError(new ObjectError("banco", "Banco inexistente."));
		else if (contaService.buscar(banco.get(), contaDto.getNumero()).isPresent())
			result.addError(new ObjectError("conta", "Número de conta já existente."));
		
		Response<ContaDto> response = new Response<ContaDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados de cadastro da conta: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Conta conta = new Conta();
		conta.setNumero(contaDto.getNumero());
		conta.setSenha(contaDto.getSenha());
		conta.setBanco(banco.get());
		conta.setCliente(cliente.get());
		contaService.persistir(conta);
		
		contaDto.setId(conta.getId());
		contaDto.setSaldo(0);
		response.setData(contaDto);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(value = "/fechar")
	public ResponseEntity<Response<ContaDto>> fechar(@Valid @RequestBody ContaDto contaDto, BindingResult result) {
		log.info("Fechando conta: {}", contaDto.toString());
		
		Optional<Banco> banco = bancoService.buscar(contaDto.getCodigoBanco());
		if (!banco.isPresent())
			result.addError(new ObjectError("banco", "Banco inexistente."));
		else if (!contaService.buscar(banco.get(), contaDto.getNumero()).isPresent())
			result.addError(new ObjectError("conta", "Número de conta inexistente."));
		
		Response<ContaDto> response = new Response<ContaDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da conta: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		contaService.remover(banco.get(), contaDto.getNumero());
		response.setData(contaDto);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping(value = "/depositar")
	public ResponseEntity<Response<TransacaoDto>> depositar(@Valid @RequestBody TransacaoDto transacaoDto, BindingResult result) {
		log.info("Depositando R$ {} na conta {} no banco {}", transacaoDto.getValor(),
				transacaoDto.getContaDestino(), transacaoDto.getBancoDestino());
				
		Optional<Banco> banco = bancoService.buscar(transacaoDto.getBancoDestino());
		Optional<Conta> conta = null;
		if (!banco.isPresent())
			result.addError(new ObjectError("banco", "Banco de destino inexistente."));
		else {
			conta = contaService.buscar(banco.get(), transacaoDto.getContaDestino());
			if (!conta.isPresent())
				result.addError(new ObjectError("conta", "Conta de destino inexistente."));
			
		}
		
		Response<TransacaoDto> response = new Response<TransacaoDto>();
		if (result.hasErrors()) {
			log.error("Erro validando dados da conta de destino: {}", result.getAllErrors());
			result.getAllErrors().forEach(error -> response.getErrors().add(error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Transacao transacao = new Transacao();
		transacao.setDestino(conta.get());
		transacao.setTipo(TipoTransacao.DEPOSITO);
		transacao.setValor(transacaoDto.getValor());
		transacaoService.persistir(transacao);
		
		conta.get().depositar(transacaoDto.getValor());
		contaService.persistir(conta.get());
		System.out.println(conta.get().getSaldo());
		
		response.setData(transacaoDto);
		return ResponseEntity.ok(response);
	}
	
}
