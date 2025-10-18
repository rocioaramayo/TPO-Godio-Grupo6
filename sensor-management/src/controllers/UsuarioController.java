package controllers;

import main.java.com.example.demo.modelo.Usuario;
import services.UsuarioService;

public class UsuarioController {
    
    private static UsuarioController instance;
    
    private UsuarioController() {}
    
    public static UsuarioController getInstance() {
        if (instance == null) {
            instance = new UsuarioController();
        }
        return instance;
    }
    
    // REGISTRAR USUARIO
    public void registrarUsuario(String nombreCompleto, String email, String password) {
        // Validaciones básicas
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            System.out.println("El nombre completo es requerido");
            return;
        }
        
        if (email == null || !email.contains("@")) {
            System.out.println("Email inválido");
            return;
        }
        
        if (password == null || password.length() < 6) {
            System.out.println("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        
        UsuarioService.getInstance().registrarUsuario(nombreCompleto, email, password);
    }
    
    // LOGIN
    public Usuario login(String email, String password) {
        if (email == null || password == null) {
            System.out.println("Email y contraseña son requeridos");
            return null;
        }
        
        Usuario usuario = UsuarioService.getInstance().validarLogin(email, password);
        
        if (usuario != null) {
            System.out.println("Login exitoso: " + usuario.getNombreCompleto());
        }
        
        return usuario;
    }
}