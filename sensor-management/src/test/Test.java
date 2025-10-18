package test;

import controllers.UsuarioController;
import main.java.com.example.demo.modelo.Usuario;

public class Test {

    public static void main(String[] args) {
        
        System.out.println("=== SISTEMA DE GESTIÓN DE SENSORES ===\n");
        
        // TEST 1: REGISTRAR USUARIO
        System.out.println("--- TEST 1: Registrar Usuario ---");
        UsuarioController.getInstance().registrarUsuario(
            "Juan Pérez", 
            "juan@test.com", 
            "123456"
        );
        System.out.println();
        
        // TEST 2: INTENTAR REGISTRAR CON MISMO EMAIL
        System.out.println("--- TEST 2: Intentar registrar email duplicado ---");
        UsuarioController.getInstance().registrarUsuario(
            "Pedro García", 
            "juan@test.com", 
            "654321"
        );
        System.out.println();
        
        // TEST 3: LOGIN EXITOSO
        System.out.println("--- TEST 3: Login Exitoso ---");
        Usuario usuario = UsuarioController.getInstance().login("juan@test.com", "123456");
        if (usuario != null) {
            System.out.println("Usuario logueado: " + usuario);
        }
        System.out.println();
        
        // TEST 4: LOGIN FALLIDO (contraseña incorrecta)
        System.out.println("--- TEST 4: Login Fallido (contraseña incorrecta) ---");
        Usuario usuarioFallido = UsuarioController.getInstance().login("juan@test.com", "incorrecta");
        if (usuarioFallido == null) {
            System.out.println("Login rechazado correctamente");
        }
        System.out.println();
        
        // TEST 5: REGISTRAR OTRO USUARIO
        System.out.println("--- TEST 5: Registrar Técnico ---");
        UsuarioController.getInstance().registrarUsuario(
            "Ana Martínez", 
            "ana@test.com", 
            "tecnico123"
        );
        System.out.println();
        
        System.out.println("=== TESTS COMPLETADOS ===");
    }
}