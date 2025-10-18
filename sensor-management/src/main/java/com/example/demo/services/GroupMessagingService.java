package com.example.demo.services;

import com.example.demo.modelo.Grupo;
import com.example.demo.modelo.Mensaje;
import com.example.demo.repositories.mongo.GrupoMongoDAO;
import com.example.demo.repositories.mongo.MensajeMongoDAO;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Servicio para mensajería grupal según los requerimientos del TP
 * Implementa mensajería entre usuarios (privada y/o grupal)
 */
public class GroupMessagingService {
    
    private static GroupMessagingService instance;
    private GrupoMongoDAO grupoDAO;
    private MensajeMongoDAO mensajeDAO;
    
    private GroupMessagingService() {
        this.grupoDAO = GrupoMongoDAO.getInstance();
        this.mensajeDAO = MensajeMongoDAO.getInstance();
    }
    
    public static GroupMessagingService getInstance() {
        if (instance == null) {
            instance = new GroupMessagingService();
        }
        return instance;
    }
    
    /**
     * Crear un nuevo grupo
     */
    public Grupo crearGrupo(String nombreGrupo, String descripcion, String tipoGrupo, Integer idUsuarioCreador) {
        try {
            String idGrupo = "GRP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Grupo grupo = new Grupo(nombreGrupo, descripcion, tipoGrupo);
            grupo.setIdGrupo(idGrupo);
            
            // Agregar el creador como miembro
            grupo.agregarMiembro(idUsuarioCreador);
            
            grupoDAO.crear(grupo);
            
            System.out.println("Grupo creado: " + idGrupo + " - " + nombreGrupo);
            return grupo;
            
        } catch (Exception e) {
            System.err.println("Error creando grupo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Agregar usuario a un grupo
     */
    public boolean agregarUsuarioAGrupo(String idGrupo, Integer idUsuario) {
        try {
            Grupo grupo = grupoDAO.findById(idGrupo);
            if (grupo == null) {
                System.err.println("Grupo no encontrado: " + idGrupo);
                return false;
            }
            
            grupo.agregarMiembro(idUsuario);
            grupoDAO.actualizar(grupo);
            
            System.out.println("Usuario " + idUsuario + " agregado al grupo " + idGrupo);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error agregando usuario al grupo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Remover usuario de un grupo
     */
    public boolean removerUsuarioDeGrupo(String idGrupo, Integer idUsuario) {
        try {
            Grupo grupo = grupoDAO.findById(idGrupo);
            if (grupo == null) {
                System.err.println("Grupo no encontrado: " + idGrupo);
                return false;
            }
            
            grupo.removerMiembro(idUsuario);
            grupoDAO.actualizar(grupo);
            
            System.out.println("Usuario " + idUsuario + " removido del grupo " + idGrupo);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error removiendo usuario del grupo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Enviar mensaje grupal
     */
    public Mensaje enviarMensajeGrupal(Integer idRemitente, String idGrupo, String contenido) {
        try {
            Grupo grupo = grupoDAO.findById(idGrupo);
            if (grupo == null) {
                System.err.println("Grupo no encontrado: " + idGrupo);
                return null;
            }
            
            if (!grupo.esMiembro(idRemitente)) {
                System.err.println("El usuario " + idRemitente + " no es miembro del grupo " + idGrupo);
                return null;
            }
            
            String idMensaje = "MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Mensaje mensaje = new Mensaje();
            mensaje.setIdMensaje(idMensaje);
            mensaje.setIdRemitente(idRemitente);
            mensaje.setIdDestinatario(Integer.parseInt(idGrupo)); // Para mensajes grupales, usar el ID del grupo como Integer
            mensaje.setContenido(contenido);
            mensaje.setTipo("grupal");
            mensaje.setFechaHora(new java.util.Date()); // Convertir LocalDateTime a Date
            
            mensajeDAO.crear(mensaje);
            
            System.out.println("Mensaje grupal enviado: " + idMensaje + " al grupo " + idGrupo);
            return mensaje;
            
        } catch (Exception e) {
            System.err.println("Error enviando mensaje grupal: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Enviar mensaje privado
     */
    public Mensaje enviarMensajePrivado(Integer idRemitente, Integer idDestinatario, String contenido) {
        try {
            String idMensaje = "MSG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Mensaje mensaje = new Mensaje();
            mensaje.setIdMensaje(idMensaje);
            mensaje.setIdRemitente(idRemitente);
            mensaje.setIdDestinatario(idDestinatario);
            mensaje.setContenido(contenido);
            mensaje.setTipo("privado");
            mensaje.setFechaHora(new java.util.Date()); // Convertir LocalDateTime a Date
            
            mensajeDAO.crear(mensaje);
            
            System.out.println("Mensaje privado enviado: " + idMensaje + " de " + idRemitente + " a " + idDestinatario);
            return mensaje;
            
        } catch (Exception e) {
            System.err.println("Error enviando mensaje privado: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtener mensajes de un grupo
     */
    public List<Mensaje> obtenerMensajesGrupo(String idGrupo, int limite) {
        try {
            return mensajeDAO.findByDestinatarioYTipo(idGrupo, "grupal", limite);
        } catch (Exception e) {
            System.err.println("Error obteniendo mensajes del grupo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener mensajes privados entre dos usuarios
     */
    public List<Mensaje> obtenerMensajesPrivados(Integer idUsuario1, Integer idUsuario2, int limite) {
        try {
            return mensajeDAO.findConversacionPrivada(idUsuario1, idUsuario2, limite);
        } catch (Exception e) {
            System.err.println("Error obteniendo mensajes privados: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener todos los mensajes de un usuario (recibidos)
     */
    public List<Mensaje> obtenerMensajesRecibidos(Integer idUsuario, int limite) {
        try {
            return mensajeDAO.findByDestinatario(idUsuario, limite);
        } catch (Exception e) {
            System.err.println("Error obteniendo mensajes recibidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener todos los mensajes enviados por un usuario
     */
    public List<Mensaje> obtenerMensajesEnviados(Integer idUsuario, int limite) {
        try {
            return mensajeDAO.findByRemitente(idUsuario, limite);
        } catch (Exception e) {
            System.err.println("Error obteniendo mensajes enviados: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener grupos de un usuario
     */
    public List<Grupo> obtenerGruposDeUsuario(Integer idUsuario) {
        try {
            return grupoDAO.findByUsuario(idUsuario);
        } catch (Exception e) {
            System.err.println("Error obteniendo grupos de usuario: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener todos los grupos disponibles
     */
    public List<Grupo> obtenerTodosLosGrupos() {
        try {
            return grupoDAO.findAll();
        } catch (Exception e) {
            System.err.println("Error obteniendo todos los grupos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtener grupos por tipo
     */
    public List<Grupo> obtenerGruposPorTipo(String tipoGrupo) {
        try {
            return grupoDAO.findByTipo(tipoGrupo);
        } catch (Exception e) {
            System.err.println("Error obteniendo grupos por tipo: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Verificar si un usuario es miembro de un grupo
     */
    public boolean esMiembroDelGrupo(Integer idUsuario, String idGrupo) {
        try {
            Grupo grupo = grupoDAO.findById(idGrupo);
            return grupo != null && grupo.esMiembro(idUsuario);
        } catch (Exception e) {
            System.err.println("Error verificando membresía: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener estadísticas de mensajería
     */
    public MessagingStats obtenerEstadisticas() {
        try {
            int totalGrupos = grupoDAO.countAll();
            int totalMensajes = mensajeDAO.countAll();
            int mensajesGrupales = mensajeDAO.countByTipo("grupal");
            int mensajesPrivados = mensajeDAO.countByTipo("privado");
            
            return new MessagingStats(totalGrupos, totalMensajes, mensajesGrupales, mensajesPrivados);
            
        } catch (Exception e) {
            System.err.println("Error obteniendo estadísticas de mensajería: " + e.getMessage());
            return new MessagingStats(0, 0, 0, 0);
        }
    }
    
    /**
     * Marcar mensaje como leído
     */
    public boolean marcarMensajeComoLeido(String idMensaje) {
        try {
            mensajeDAO.marcarComoLeido(idMensaje);
            return true;
        } catch (Exception e) {
            System.err.println("Error marcando mensaje como leído: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener mensajes no leídos de un usuario
     */
    public List<Mensaje> obtenerMensajesNoLeidos(Integer idUsuario) {
        try {
            return mensajeDAO.findNoLeidosByDestinatario(idUsuario);
        } catch (Exception e) {
            System.err.println("Error obteniendo mensajes no leídos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Clase para estadísticas de mensajería
     */
    public static class MessagingStats {
        private int totalGrupos;
        private int totalMensajes;
        private int mensajesGrupales;
        private int mensajesPrivados;
        
        public MessagingStats(int totalGrupos, int totalMensajes, int mensajesGrupales, int mensajesPrivados) {
            this.totalGrupos = totalGrupos;
            this.totalMensajes = totalMensajes;
            this.mensajesGrupales = mensajesGrupales;
            this.mensajesPrivados = mensajesPrivados;
        }
        
        public int getTotalGrupos() { return totalGrupos; }
        public int getTotalMensajes() { return totalMensajes; }
        public int getMensajesGrupales() { return mensajesGrupales; }
        public int getMensajesPrivados() { return mensajesPrivados; }
        
        public double getPorcentajeMensajesGrupales() {
            if (totalMensajes == 0) return 0.0;
            return (double) mensajesGrupales / totalMensajes * 100;
        }
        
        public double getPorcentajeMensajesPrivados() {
            if (totalMensajes == 0) return 0.0;
            return (double) mensajesPrivados / totalMensajes * 100;
        }
        
        @Override
        public String toString() {
            return "MessagingStats{" +
                    "totalGrupos=" + totalGrupos +
                    ", totalMensajes=" + totalMensajes +
                    ", mensajesGrupales=" + mensajesGrupales +
                    ", mensajesPrivados=" + mensajesPrivados +
                    '}';
        }
    }
}
