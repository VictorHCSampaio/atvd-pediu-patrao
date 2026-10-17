package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.model.AcaoAuditoria;
import com.umc.pediupatrao.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private static final String ENTIDADE = "Cliente";

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(ClienteRepository clienteRepository, AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    public Cliente novoCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public void excluir(String id) {
        clienteRepository.deleteById(id);
    }

    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }


    public Cliente salvar(Cliente cliente) {
        if (cliente.getId() == null) {
            Cliente salvo = clienteRepository.save(cliente);
            auditoriaService.registrar(AcaoAuditoria.CLIENTE_CRIADO, ENTIDADE, salvo.getId(), List.of());
            return salvo;
        }

        Cliente anterior = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para atualização."));

        List<AlteracaoCampo> alteracoes = diferencas(anterior, cliente);
        Cliente salvo = clienteRepository.save(cliente);
        if (!alteracoes.isEmpty()) {
            auditoriaService.registrar(AcaoAuditoria.CLIENTE_ALTERADO, ENTIDADE, salvo.getId(), alteracoes);
        }
        return salvo;
    }

    private List<AlteracaoCampo> diferencas(Cliente anterior, Cliente novo) {
        List<AlteracaoCampo> alteracoes = new ArrayList<>();
        comparar(alteracoes, "nome", anterior.getNome(), novo.getNome());
        comparar(alteracoes, "telefone", anterior.getTelefone(), novo.getTelefone());
        comparar(alteracoes, "cep", anterior.getCep(), novo.getCep());
        comparar(alteracoes, "logradouro", anterior.getLogradouro(), novo.getLogradouro());
        comparar(alteracoes, "numero", anterior.getNumero(), novo.getNumero());
        comparar(alteracoes, "complemento", anterior.getComplemento(), novo.getComplemento());
        comparar(alteracoes, "bairro", anterior.getBairro(), novo.getBairro());
        comparar(alteracoes, "cidade", anterior.getCidade(), novo.getCidade());
        comparar(alteracoes, "estado", anterior.getEstado(), novo.getEstado());
        return alteracoes;
    }

    private void comparar(List<AlteracaoCampo> alteracoes, String campo, String valorAnterior, String valorNovo) {
        if (valorAnterior == null ? valorNovo != null : !valorAnterior.equals(valorNovo)) {
            alteracoes.add(new AlteracaoCampo(campo, valorAnterior, valorNovo));
        }
    }
}
