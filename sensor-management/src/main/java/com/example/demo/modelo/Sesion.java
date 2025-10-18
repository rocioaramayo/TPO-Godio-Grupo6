package com.example.demo.modelo;
import java.util.Date;

public class Sesion {
    
    private String idSesion;
    private Integer idUsuario;
    private String rol;
    private Date fechaHoraInicio;
    private Date fechaHoraCierre;
    private String estadoActual; // activa/inactiva
    
    // Constructor vacío
    public Sesion() {}
    
    // Constructor
    public Sesion(String idSesion, Integer idUsuario, String rol) {
        this.idSesion = idSesion;
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.fechaHoraInicio = new Date();
        this.estadoActual = "activa";
    }
    
    // Getters y Setters
    public String getIdSesion() {
        return idSesion;
    }
    
    public void setIdSesion(String idSesion) {
        this.idSesion = idSesion;
    }
    
    public Integer getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public Date getFechaHoraInicio() {
        return fechaHoraInicio;
    }
    
    public void setFechaHoraInicio(Date fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }
    
    public Date getFechaHoraCierre() {
        return fechaHoraCierre;
    }
    
    public void setFechaHoraCierre(Date fechaHoraCierre) {
        this.fechaHoraCierre = fechaHoraCierre;
    }
    
    public String getEstadoActual() {
        return estadoActual;
    }
    
    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }
    
    @Override
    public String toString() {
        return "Sesion{" +
                "idSesion='" + idSesion + '\'' +
                ", idUsuario=" + idUsuario +
                ", rol='" + rol + '\'' +
                ", estadoActual='" + estadoActual + '\'' +
                ", fechaHoraInicio=" + fechaHoraInicio +
                '}';
    }
}