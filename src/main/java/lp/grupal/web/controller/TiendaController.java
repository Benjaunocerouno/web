package lp.grupal.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lp.grupal.web.model.Categoria;
import lp.grupal.web.model.Producto;
import lp.grupal.web.model.dao.ICategoriaDAO;
import lp.grupal.web.model.dao.IProductoDAO;

@Controller
public class TiendaController {
    @Autowired
    private IProductoDAO productoDAO;

    @Autowired
    private ICategoriaDAO categoriaDAO;

    @GetMapping("/tienda")
    public String tienda(Model model) {
        List<Categoria> categorias = categoriaDAO.findByActivoTrue();
        List<Producto> productos = productoDAO.findAll();

        model.addAttribute("listaCategorias", categorias);
        model.addAttribute("listaProductos", productos);

        return "tienda"; // Retorna tienda.html
    }
}
