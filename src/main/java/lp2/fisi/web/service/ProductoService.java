package lp2.fisi.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lp2.fisi.web.model.Producto;
import lp2.fisi.web.repository.ProductoRepository;
import java.util.List;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> obtenerTodosProductos() {
        return productoRepository.findAll();
    }
    
    public List<String> obtenerCategorias() {
        return List.of("Cargadores", "Audífonos", "Protectores", "Cases", "Baterías");
    }
}