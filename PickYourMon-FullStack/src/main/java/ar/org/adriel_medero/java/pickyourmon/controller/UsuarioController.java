package ar.org.adriel_medero.java.pickyourmon.controller;

import ar.org.adriel_medero.java.pickyourmon.dto.UsuarioDTO;
import ar.org.adriel_medero.java.pickyourmon.model.Usuario;
import ar.org.adriel_medero.java.pickyourmon.model.enums.Rol;
import ar.org.adriel_medero.java.pickyourmon.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.org.adriel_medero.java.pickyourmon.dto.LoginDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios") // URL base: http://localhost:8080/api/usuarios
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // -------------------------------- CRUD BÁSICO --------------------------------

    // obtener todos los usuarios
    // GET: /api/usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();

        // convertimos la lista de entidades a DTOs para no mostrar passwords
        List<UsuarioDTO> dtos = usuarios.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // buscar usuario por ID
    // GET: /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);

        // si existe convertimos a DTO y devolvemos OK, si no, 404
        return usuario.map(value -> new ResponseEntity<>(convertirADTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // crear un nuevo usuario (Registro)
    // POST: /api/usuarios
    @PostMapping
    public ResponseEntity<UsuarioDTO> guardarUsuario(@RequestBody Usuario usuario) {
        // Regla de negocio: Si no trae rol, le asignamos USER por defecto
        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USER);
        }

        usuarioService.guardar(usuario);

        // Devolvemos Created y el DTO (sin password)
        return new ResponseEntity<>(convertirADTO(usuario), HttpStatus.CREATED);
    }

    // actualizar usuario (usamos el mismo guardar pero verificando ID)
    // PUT: /api/usuarios
    @PutMapping
    public ResponseEntity<?> actualizarUsuario(@RequestBody Usuario usuario) {
        // Validamos primero que envíen un ID (es obligatorio para actualizar)
        if (usuario.getId() == null) {
            return new ResponseEntity<>("Error: Debes enviar el ID del usuario a modificar", HttpStatus.BAD_REQUEST);
        }
        // buscamos el usuario original en la base de datos
        Optional<Usuario> usuarioExistenteOpt = usuarioService.buscarPorId(usuario.getId());
        if (usuarioExistenteOpt.isPresent()) {
            Usuario usuarioExistente = usuarioExistenteOpt.get();

            // Validamos que el email no se repita
            // Si el email que viene es diferente al que ya tenía...
            if (!usuario.getEmail().equals(usuarioExistente.getEmail())) {
                // ... verificamos si ese NUEVO email ya lo tiene otra persona
                if (usuarioService.buscarPorEmail(usuario.getEmail()).isPresent()) {
                    return new ResponseEntity<>(
                            "Error: El email " + usuario.getEmail() + " ya está en uso por otro usuario.",
                            HttpStatus.BAD_REQUEST);
                }
            }

            usuarioService.guardar(usuario);
            // CAMBIO CLAVE: Devolvemos el DTO para ver los cambios en Postman
            // inmediatamente
            return new ResponseEntity<>(convertirADTO(usuario), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No se encontró el usuario para actualizar", HttpStatus.NOT_FOUND);
        }
    }

    // eliminar usuario
    // DELETE: /api/usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        if (usuarioService.buscarPorId(id).isPresent()) {
            usuarioService.eliminar(id);
            return new ResponseEntity<>("Usuario eliminado", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // -------------------------------- AUTENTICACIÓN (LOGIN) --------------------------------

    // LOGIN
    // POST: /api/usuarios/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(loginRequest.getEmail());
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            // Comparamos contraseña
            if (usuario.getPassword().equals(loginRequest.getPassword())) {
                // Si coincide, devolvemos el usuario (sin contraseña) para guardarlo en el front
                return new ResponseEntity<>(convertirADTO(usuario), HttpStatus.OK);
            }
        }
        
        return new ResponseEntity<>("Email o contraseña incorrectos", HttpStatus.UNAUTHORIZED);
    }

    // -------------------------------- BÚSQUEDAS ESPECÍFICAS --------------------------------

    // buscar por Email
    // GET: /api/usuarios/buscar-email?email=ejemplo@correo.com
    @GetMapping("/buscar-email")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@RequestParam String email) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        return usuario.map(value -> new ResponseEntity<>(convertirADTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // buscar por DNI
    // GET: /api/usuarios/buscar-dni/{dni}
    @GetMapping("/buscar-dni/{dni}")
    public ResponseEntity<UsuarioDTO> buscarPorDni(@PathVariable String dni) {
        Optional<Usuario> usuario = usuarioService.buscarPorDni(dni);
        return usuario.map(value -> new ResponseEntity<>(convertirADTO(value), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // buscar por nombre parcial
    // GET: /api/usuarios/buscar-nombre?nombre=Juan
    @GetMapping("/buscar-nombre")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<Usuario> usuarios = usuarioService.buscarPorNombreParcial(nombre);
        List<UsuarioDTO> dtos = usuarios.stream().map(this::convertirADTO).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // filtrar por Apellido
    // GET: /api/usuarios/filtrar-apellido?apellido=Perez
    @GetMapping("/filtrar-apellido")
    public ResponseEntity<List<UsuarioDTO>> filtrarPorApellido(@RequestParam String apellido) {
        List<Usuario> usuarios = usuarioService.filtrarPorApellido(apellido);
        List<UsuarioDTO> dtos = usuarios.stream().map(this::convertirADTO).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // filtrar por Rol
    // GET: /api/usuarios/filtrar-rol?rol=ADMIN
    @GetMapping("/filtrar-rol")
    public ResponseEntity<List<UsuarioDTO>> filtrarPorRol(@RequestParam Rol rol) {
        List<Usuario> usuarios = usuarioService.filtrarPorRol(rol);
        List<UsuarioDTO> dtos = usuarios.stream().map(this::convertirADTO).collect(Collectors.toList());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    // -------------------------------- MÉTODOS PRIVADOS --------------------------------

    // convertimos de Entidad (con password) a DTO (sin password)
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setDireccion(usuario.getDireccion());
        dto.setDni(usuario.getDni());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        // IMPORTANTE: No setear la contraseña acá. Seguridad básica.
        return dto;
    }
}