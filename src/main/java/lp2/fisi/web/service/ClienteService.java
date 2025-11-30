package lp2.fisi.web.service;

import lp2.fisi.web.model.Cliente;
import lp2.fisi.web.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente registrarCliente(String dni, String nombres, String apellidos,
                                    String telefono, String email, String direccion,
                                    LocalDate fechaNacimiento, String password) {

        // Verificar si ya existe el email o DNI
        if (clienteRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (clienteRepository.existsByDni(dni)) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        // Crear nuevo cliente
        Cliente cliente = new Cliente(dni, nombres, apellidos, telefono, email,
                direccion, fechaNacimiento, password);

        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> login(String email, String password) {
        return clienteRepository.findByEmailAndPassword(email, password);
    }

    public Optional<Cliente> obtenerClientePorId(Integer id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerClientePorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    public boolean existeEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    public boolean existeDni(String dni) {
        return clienteRepository.existsByDni(dni);
    }
}