package com.example.demo.modelo;

import java.util.List;
import java.util.ArrayList;

/**
 * Modelo para representar grupos de usuarios en el sistema
 * Permite mensajería grupal y gestión de equipos
 */
public class Grupo {
    
    private String idGrupo;
    private String nombreGrupo;
    private String descripcion;
    private List<Integer> usuariosMiembros;
    private String tipoGrupo; // equipo_trabajo, departamento, proyecto
    private String estado; // activo/inactivo
    
    // Constructor vacío
    public Grupo() {
        this.usuariosMiembros = new ArrayList<>();
    }
    
    // Constructor básico
    public Grupo(String nombreGrupo, String descripcion, String tipoGrupo) {
        this.nombreGrupo = nombreGrupo;
        this.descripcion = descripcion;
        this.tipoGrupo = tipoGrupo;
        this.estado = "activo";
        this.usuariosMiembros = new ArrayList<>();
    }
    
    // Constructor completo
    public Grupo(String nombreGrupo, String descripcion, String tipoGrupo, List<Integer> usuariosMiembros) {
        this.nombreGrupo = nombreGrupo;
        this.descripcion = descripcion;
        this.tipoGrupo = tipoGrupo;
        this.usuariosMiembros = usuariosMiembros != null ? usuariosMiembros : new ArrayList<>();
        this.estado = "activo";
    }
    
    // Métodos de gestión de miembros
    public void agregarMiembro(Integer idUsuario) {
        if (!usuariosMiembros.contains(idUsuario)) {
            usuariosMiembros.add(idUsuario);
        }
    }
    
    public void removerMiembro(Integer idUsuario) {
        usuariosMiembros.remove(idUsuario);
    }
    
    public boolean esMiembro(Integer idUsuario) {
        return usuariosMiembros.contains(idUsuario);
    }
    
    public int getCantidadMiembros() {
        return usuariosMiembros.size();
    }
    
    // Getters y Setters
    public String getIdGrupo() {
        return idGrupo;
    }
    
    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }
    
    public String getNombreGrupo() {
        return nombreGrupo;
    }
    
    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public List<Integer> getUsuariosMiembros() {
        return usuariosMiembros;
    }
    
    public void setUsuariosMiembros(List<Integer> usuariosMiembros) {
        this.usuariosMiembros = usuariosMiembros;
    }
    
    public String getTipoGrupo() {
        return tipoGrupo;
    }
    
    public void setTipoGrupo(String tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    @Override
    public String toString() {
        return "Grupo{" +
                "idGrupo='" + idGrupo + '\'' +
                ", nombreGrupo='" + nombreGrupo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", tipoGrupo='" + tipoGrupo + '\'' +
                ", cantidadMiembros=" + getCantidadMiembros() +
                ", estado='" + estado + '\'' +
                '}';
    }
}
