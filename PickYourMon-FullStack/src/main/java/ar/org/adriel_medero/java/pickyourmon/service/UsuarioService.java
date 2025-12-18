package ar.org.adriel_medero.java.pickyourmon.service;

import ar.org.adriel_medero.java.pickyourmon.model.Usuario;
import ar.org.adriel_medero.java.pickyourmon.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Vital para el Login
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public void guardar(Usuario usuario) {
        usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<Usuario> buscarPorNombreParcial(String nombreParcial) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombreParcial);
    }

    public List<Usuario> filtrarPorApellido(String apellidoParcial) {
        return usuarioRepository.findByApellidoContainingIgnoreCase(apellidoParcial);
    }

    public Optional<Usuario> buscarPorDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    public List<Usuario> filtrarPorRol(ar.org.adriel_medero.java.pickyourmon.model.enums.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
}