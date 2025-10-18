package com.example.demo.modelo;
import java.util.Date;

public class Mensaje {
    
    private String idMensaje;
    private Integer idRemitente;
    private String nombreRemitente;
    private Integer idDestinatario; // Puede ser null si es grupal
    private String idGrupo; // Puede ser null si es privado
    private Date fechaHora;
    private String contenido;
    private String tipo; // privado/grupal
    
    // Constructor vacío
    public Mensaje() {}
    
    // Constructor para mensaje privado
    public Mensaje(Integer idRemitente, String nombreRemitente, 
                Integer idDestinatario, String contenido) {
        this.idRemitente = idRemitente;
        this.nombreRemitente = nombreRemitente;
        this.idDestinatario = idDestinatario;
        this.contenido = contenido;
        this.tipo = "privado";
        this.fechaHora = new Date();
    }
    
    // Constructor para mensaje grupal
    public Mensaje(Integer idRemitente, String nombreRemitente, 
                String idGrupo, String contenido, boolean esGrupal) {
        this.idRemitente = idRemitente;
        this.nombreRemitente = nombreRemitente;
        this.idGrupo = idGrupo;
        this.contenido = contenido;
        this.tipo = "grupal";
        this.fechaHora = new Date();
    }
    
    // Getters y Setters
    public String getIdMensaje() {
        return idMensaje;
    }
    
    public void setIdMensaje(String idMensaje) {
        this.idMensaje = idMensaje;
    }
    
    public Integer getIdRemitente() {
        return idRemitente;
    }
    
    public void setIdRemitente(Integer idRemitente) {
        this.idRemitente = idRemitente;
    }
    
    public String getNombreRemitente() {
        return nombreRemitente;
    }
    
    public void setNombreRemitente(String nombreRemitente) {
        this.nombreRemitente = nombreRemitente;
    }
    
    public Integer getIdDestinatario() {
        return idDestinatario;
    }
    
    public void setIdDestinatario(Integer idDestinatario) {
        this.idDestinatario = idDestinatario;
    }
    
    public String getIdGrupo() {
        return idGrupo;
    }
    
    public void setIdGrupo(String idGrupo) {
        this.idGrupo = idGrupo;
    }
    
    public Date getFechaHora() {
        return fechaHora;
    }
    
    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }
    
    public String getContenido() {
        return contenido;
    }
    
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    @Override
    public String toString() {
        return "Mensaje{" +
                "idMensaje='" + idMensaje + '\'' +
                ", nombreRemitente='" + nombreRemitente + '\'' +
                ", tipo='" + tipo + '\'' +
                ", fechaHora=" + fechaHora +
                ", contenido='" + contenido + '\'' +
                '}';
    }
}