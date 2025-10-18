package com.example.demo.modelo;

/**
 * Modelo para representar roles de usuario en el sistema
 * Define los diferentes tipos de usuarios y sus permisos
 */
public class Rol {
    
    private Integer idRol;
    private String descripcion; // usuario – técnico – administrador
    private String permisos; // JSON string con permisos específicos
    private String estado; // activo/inactivo
    
    // Constructor vacío
    public Rol() {}
    
    // Constructor completo
    public Rol(String descripcion, String permisos) {
        this.descripcion = descripcion;
        this.permisos = permisos;
        this.estado = "activo";
    }
    
    // Constructor con roles predefinidos
    public Rol(String descripcion) {
        this.descripcion = descripcion;
        this.estado = "activo";
        switch (descripcion.toLowerCase()) {
            case "administrador":
                this.permisos = "{\"gestion_usuarios\":true,\"gestion_sensores\":true,\"gestion_procesos\":true,\"facturacion\":true,\"reportes\":true}";
                break;
            case "técnico":
            case "tecnico":
                this.permisos = "{\"gestion_sensores\":true,\"gestion_procesos\":true,\"reportes\":true}";
                break;
            case "usuario":
            default:
                this.permisos = "{\"consultas\":true,\"solicitar_procesos\":true}";
                break;
        }
    }
    
    // Getters y Setters
    public Integer getIdRol() {
        return idRol;
    }
    
    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getPermisos() {
        return permisos;
    }
    
    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return "Rol{" +
                "idRol=" + idRol +
                ", descripcion='" + descripcion + '\'' +
                ", permisos='" + permisos + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
