package com.example.demo.services;

import com.example.demo.modelo.Rol;
import com.example.demo.modelo.Usuario;
import com.example.demo.repositories.mysql.RolMySQLRepository;
import com.example.demo.repositories.mysql.UsuarioMySQLRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Servicio para gestión de roles y permisos según los requerimientos del TP
 * Implementa el sistema de roles diferenciados: usuario, técnico, administrador
 */
public class RolePermissionService {
    
    private static RolePermissionService instance;
    private RolMySQLRepository rolRepository;
    private UsuarioMySQLRepository usuarioRepository;
    
    private RolePermissionService() {
        this.rolRepository = RolMySQLRepository.getInstance();
        this.usuarioRepository = UsuarioMySQLRepository.getInstance();
    }
    
    public static RolePermissionService getInstance() {
        if (instance == null) {
            instance = new RolePermissionService();
        }
        return instance;
    }
    
    /**
     * Inicializa los roles por defecto del sistema
     */
    public void inicializarRolesPorDefecto() {
        try {
            List<Rol> rolesExistentes = rolRepository.findAll();
            if (!rolesExistentes.isEmpty()) {
                System.out.println("Los roles ya están inicializados");
                return;
            }
            
            // Crear roles por defecto
            Rol admin = new Rol("administrador");
            Rol tecnico = new Rol("técnico");
            Rol usuario = new Rol("usuario");
            
            rolRepository.crear(admin);
            rolRepository.crear(tecnico);
            rolRepository.crear(usuario);
            
            System.out.println("Roles por defecto inicializados: administrador, técnico, usuario");
            
        } catch (Exception e) {
            System.err.println("Error inicializando roles por defecto: " + e.getMessage());
        }
    }
    
    /**
     * Asigna un rol a un usuario
     */
    public boolean asignarRolAUsuario(Integer idUsuario, String descripcionRol) {
        try {
            Rol rol = rolRepository.findByDescripcion(descripcionRol);
            if (rol == null) {
                System.err.println("Rol no encontrado: " + descripcionRol);
                return false;
            }
            
            usuarioRepository.asignarRol(idUsuario, rol.getIdRol());
            System.out.println("Rol " + descripcionRol + " asignado al usuario " + idUsuario);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error asignando rol: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene el rol de un usuario
     */
    public Rol obtenerRolDeUsuario(Integer idUsuario) {
        try {
            return rolRepository.findByUsuario(idUsuario);
        } catch (Exception e) {
            System.err.println("Error obteniendo rol de usuario: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica si un usuario tiene un permiso específico
     */
    public boolean tienePermiso(Integer idUsuario, String permiso) {
        try {
            Rol rol = obtenerRolDeUsuario(idUsuario);
            if (rol == null) {
                return false;
            }
            
            return tienePermisoEnRol(rol, permiso);
            
        } catch (Exception e) {
            System.err.println("Error verificando permiso: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un rol tiene un permiso específico
     */
    public boolean tienePermisoEnRol(Rol rol, String permiso) {
        if (rol == null || rol.getPermisos() == null) {
            return false;
        }
        
        // Parsear JSON de permisos (simplificado)
        String permisos = rol.getPermisos().toLowerCase();
        return permisos.contains("\"" + permiso.toLowerCase() + "\":true");
    }
    
    /**
     * Obtiene todos los permisos de un usuario
     */
    public List<String> obtenerPermisosDeUsuario(Integer idUsuario) {
        List<String> permisos = new ArrayList<>();
        
        try {
            Rol rol = obtenerRolDeUsuario(idUsuario);
            if (rol != null) {
                permisos = parsearPermisos(rol.getPermisos());
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo permisos de usuario: " + e.getMessage());
        }
        
        return permisos;
    }
    
    /**
     * Verifica si un usuario es administrador
     */
    public boolean esAdministrador(Integer idUsuario) {
        Rol rol = obtenerRolDeUsuario(idUsuario);
        return rol != null && "administrador".equals(rol.getDescripcion());
    }
    
    /**
     * Verifica si un usuario es técnico
     */
    public boolean esTecnico(Integer idUsuario) {
        Rol rol = obtenerRolDeUsuario(idUsuario);
        return rol != null && "técnico".equals(rol.getDescripcion());
    }
    
    /**
     * Verifica si un usuario es usuario regular
     */
    public boolean esUsuarioRegular(Integer idUsuario) {
        Rol rol = obtenerRolDeUsuario(idUsuario);
        return rol != null && "usuario".equals(rol.getDescripcion());
    }
    
    /**
     * Obtiene usuarios por rol
     */
    public List<Usuario> obtenerUsuariosPorRol(String descripcionRol) {
        try {
            return usuarioRepository.findByRol(descripcionRol);
        } catch (Exception e) {
            System.err.println("Error obteniendo usuarios por rol: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene estadísticas de roles
     */
    public RoleStats obtenerEstadisticasRoles() {
        try {
            List<Rol> todosRoles = rolRepository.findAll();
            Map<String, Integer> conteoPorRol = new HashMap<>();
            
            for (Rol rol : todosRoles) {
                List<Usuario> usuarios = obtenerUsuariosPorRol(rol.getDescripcion());
                conteoPorRol.put(rol.getDescripcion(), usuarios.size());
            }
            
            return new RoleStats(todosRoles.size(), conteoPorRol);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo estadísticas de roles: " + e.getMessage());
            return new RoleStats(0, new HashMap<>());
        }
    }
    
    /**
     * Valida acceso a funcionalidad según rol
     */
    public boolean validarAccesoFuncionalidad(Integer idUsuario, String funcionalidad) {
        switch (funcionalidad.toLowerCase()) {
            case "gestion_usuarios":
                return esAdministrador(idUsuario);
                
            case "gestion_sensores":
                return esAdministrador(idUsuario) || esTecnico(idUsuario);
                
            case "gestion_procesos":
                return esAdministrador(idUsuario) || esTecnico(idUsuario);
                
            case "facturacion":
                return esAdministrador(idUsuario);
                
            case "reportes":
                return esAdministrador(idUsuario) || esTecnico(idUsuario);
                
            case "consultas":
                return true; // Todos los usuarios pueden consultar
                
            case "solicitar_procesos":
                return true; // Todos los usuarios pueden solicitar procesos
                
            default:
                return false;
        }
    }
    
    /**
     * Obtiene el nivel de acceso de un usuario (1=usuario, 2=técnico, 3=administrador)
     */
    public int obtenerNivelAcceso(Integer idUsuario) {
        if (esAdministrador(idUsuario)) return 3;
        if (esTecnico(idUsuario)) return 2;
        if (esUsuarioRegular(idUsuario)) return 1;
        return 0;
    }
    
    /**
     * Verifica si un usuario puede acceder a datos de otro usuario
     */
    public boolean puedeAccederADatosUsuario(Integer idUsuarioActual, Integer idUsuarioObjetivo) {
        int nivelActual = obtenerNivelAcceso(idUsuarioActual);
        int nivelObjetivo = obtenerNivelAcceso(idUsuarioObjetivo);
        
        // Solo puede acceder si tiene mayor o igual nivel de acceso
        return nivelActual >= nivelObjetivo;
    }
    
    /**
     * Parsear permisos desde JSON string
     */
    private List<String> parsearPermisos(String permisosJson) {
        List<String> permisos = new ArrayList<>();
        
        if (permisosJson == null || permisosJson.trim().isEmpty()) {
            return permisos;
        }
        
        // Parseo simplificado de JSON
        String json = permisosJson.toLowerCase().replaceAll("[{}\"]", "");
        String[] pares = json.split(",");
        
        for (String par : pares) {
            if (par.contains(":true")) {
                String permiso = par.split(":")[0].trim();
                permisos.add(permiso);
            }
        }
        
        return permisos;
    }
    
    /**
     * Clase para estadísticas de roles
     */
    public static class RoleStats {
        private int totalRoles;
        private Map<String, Integer> usuariosPorRol;
        
        public RoleStats(int totalRoles, Map<String, Integer> usuariosPorRol) {
            this.totalRoles = totalRoles;
            this.usuariosPorRol = usuariosPorRol;
        }
        
        public int getTotalRoles() { return totalRoles; }
        public Map<String, Integer> getUsuariosPorRol() { return usuariosPorRol; }
        
        public int getTotalUsuarios() {
            return usuariosPorRol.values().stream().mapToInt(Integer::intValue).sum();
        }
        
        @Override
        public String toString() {
            return "RoleStats{" +
                    "totalRoles=" + totalRoles +
                    ", usuariosPorRol=" + usuariosPorRol +
                    ", totalUsuarios=" + getTotalUsuarios() +
                    '}';
        }
    }
}
