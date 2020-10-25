package com.example.algamoney.api.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.exception.PessoaInexistenteOuInativaException;
import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.repository.filter.LancamentoFilter;
import com.example.algamoney.api.repository.projection.ResumoLancamento;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public Lancamento salvar(Lancamento lancamento) {
		Pessoa pessoaSalva = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		
		if(pessoaSalva == null || pessoaSalva.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
		return lancamentoRepository.save(lancamento);
	}
	
	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}

		//Copia as propriedades de um objeto para outro, exceto o código
		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

		return lancamentoRepository.save(lancamentoSalvo);
	}
	
	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;
		if (lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		}

		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
	}
	
	private Lancamento buscarLancamentoExistente(Long codigo) {
		Lancamento lancamentoSalvo = lancamentoRepository.findOne(codigo);
		if (lancamentoSalvo == null) {
			throw new IllegalArgumentException();
		}
		return lancamentoSalvo;
	}
	public List<Lancamento> listar(){
		return lancamentoRepository.findAll();
	}
	
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
	}
	
	public Lancamento buscarLancamentoPeloCodigo(Long codigo) {
		Lancamento lancamentoSalvo = lancamentoRepository.findOne(codigo);
		
		if(lancamentoSalvo == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return lancamentoSalvo;
	}
	
	public void deletar(Long codigo) {
		lancamentoRepository.delete(codigo);
	}

	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		return lancamentoRepository.resumir(lancamentoFilter, pageable);
	}
}