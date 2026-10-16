package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.pedido.ItemPedidoRequest;
import com.umc.pediupatrao.dto.pedido.PedidoRequest;
import com.umc.pediupatrao.service.ClienteService;
import com.umc.pediupatrao.service.PedidoService;
import com.umc.pediupatrao.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;


@Controller
public class PedidoViewController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProdutoService produtoService;

    public PedidoViewController(PedidoService pedidoService, ClienteService clienteService, ProdutoService produtoService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.produtoService = produtoService;
    }

    @GetMapping("/pedidos/novo")
    public String novoPedidoForm(Model model) {
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("produtos", produtoService.listarProdutos());
        model.addAttribute("content", "pedidos/form :: content");
        return "layout";
    }

    @PostMapping("/pedidos/salvar")
    public String salvarPedido(@RequestParam String clienteId,
                               @RequestParam(required = false) List<String> produtoId,
                               @RequestParam(required = false) List<Integer> quantidade,
                               RedirectAttributes redirectAttributes) {
        try {
            pedidoService.criarPedido(new PedidoRequest(clienteId, montarItens(produtoId, quantidade)));
            redirectAttributes.addFlashAttribute("sucesso", "Pedido registrado com sucesso!");
        } catch (ResponseStatusException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getReason());
        }
        return "redirect:/pedidos";
    }

    private List<ItemPedidoRequest> montarItens(List<String> produtoId, List<Integer> quantidade) {
        List<ItemPedidoRequest> itens = new ArrayList<>();
        if (produtoId == null || quantidade == null) {
            return itens;
        }
        for (int i = 0; i < produtoId.size() && i < quantidade.size(); i++) {
            String produto = produtoId.get(i);
            Integer qtd = quantidade.get(i);
            if (produto != null && !produto.isBlank() && qtd != null && qtd > 0) {
                itens.add(new ItemPedidoRequest(produto, qtd));
            }
        }
        return itens;
    }
}