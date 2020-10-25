package com.example.algamoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.PessoaRepository;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public Pessoa atualizar(Long codigo, Pessoa pessoa){
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		
		/*Aqui estou dizendo que quero copiar os dados da pessoa, colocar
		 * em pessoaSalva, porém sem alterar o código da pessoa,
		 * pois é código correto.*/
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		return pessoaRepository.save(pessoaSalva);
	}

	public Pessoa buscarPessoaPeloCodigo(Long codigo) {
		Pessoa pessoaSalva = pessoaRepository.findOne(codigo);

		if(pessoaSalva == null) {
			//O valor 1, quer dizer que era esperado pelo menos 1 resultado.
			throw new EmptyResultDataAccessException(1);
		}
		
		return pessoaSalva;
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}

	public Page<Pessoa> buscarPorNome(String nome, Pageable pageable) {
		return pessoaRepository.findByNomeContaining(nome, pageable);
	}
}