package main.java.com.example.demo.services;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import main.java.com.example.demo.exceptions.ErrorConectionMySQLException;
import main.java.com.example.demo.modelo.Usuario;
import repositories.UsuarioRepository;

public class UsuarioService {
    
    private static UsuarioService instance;
    
    private UsuarioService() {}
    
    public static UsuarioService getInstance() {
        if (instance == null) {
            instance = new UsuarioService();
        }
        return instance;
    }
    
    // REGISTRAR USUARIO
    public void registrarUsuario(String nombreCompleto, String email, String password) {
        try {
            // Verificar si el email ya existe
            Usuario existente = UsuarioRepository.getInstance().buscarPorEmail(email);
            if (existente != null) {
                System.out.println("El email ya está registrado");
                return;
            }
            
            // Encriptar password
            String passwordHash = encriptarPassword(password);
            
            // Crear usuario
            Usuario usuario = new Usuario(nombreCompleto, email, passwordHash);
            UsuarioRepository.getInstance().guardarUsuario(usuario);
            
            // Asignar rol por defecto (1 = usuario)
            UsuarioRepository.getInstance().asignarRol(usuario.getIdUsuario(), 1);
            
            System.out.println("Usuario registrado exitosamente con ID: " + usuario.getIdUsuario());
            
        } catch (ErrorConectionMySQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
        }
    }
    
    // VALIDAR LOGIN
    public Usuario validarLogin(String email, String password) {
        try {
            Usuario usuario = UsuarioRepository.getInstance().buscarPorEmail(email);
            
            if (usuario == null) {
                System.out.println("Usuario no encontrado");
                return null;
            }
            
            // Verificar password
            String passwordHash = encriptarPassword(password);
            if (!passwordHash.equals(usuario.getPasswordHash())) {
                System.out.println("Contraseña incorrecta");
                return null;
            }
            
            // Verificar estado activo
            if (!"activo".equals(usuario.getEstado())) {
                System.out.println("Usuario inactivo");
                return null;
            }
            
            return usuario;
            
        } catch (ErrorConectionMySQLException e) {
            System.err.println("Error al validar login: " + e.getMessage());
            return null;
        }
    }
    
    // ENCRIPTAR PASSWORD CON SHA-256
    private String encriptarPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar password", e);
        }
    }
}