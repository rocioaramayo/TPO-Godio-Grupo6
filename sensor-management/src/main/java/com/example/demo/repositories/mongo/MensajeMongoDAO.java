package com.example.demo.repositories.mongo;

import com.example.demo.connections.MongoPool;
import com.example.demo.exceptions.ErrorConectionMongoException;
import com.example.demo.modelo.Mensaje;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MensajeMongoDAO {
    private static MensajeMongoDAO instance;
    private MongoCollection<Document> collection;
    
    private MensajeMongoDAO() {
        try {
            MongoDatabase database = MongoPool.getInstancia().getConnection("sensor_management");
            this.collection = database.getCollection("mensajes");
        } catch (ErrorConectionMongoException e) {
            System.err.println("Error inicializando MensajeMongoDAO: " + e.getMessage());
        }
    }
    
    public static MensajeMongoDAO getInstance() {
        if (instance == null) instance = new MensajeMongoDAO();
        return instance;
    }
    
    public void crear(Mensaje mensaje) throws ErrorConectionMongoException {
        try {
            Document doc = new Document()
                    .append("id_mensaje", mensaje.getIdMensaje())
                    .append("id_remitente", mensaje.getIdRemitente())
                    .append("id_destinatario", mensaje.getIdDestinatario())
                    .append("fecha_hora", mensaje.getFechaHora().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .append("contenido", mensaje.getContenido())
                    .append("tipo", mensaje.getTipo())
                    .append("leido", mensaje.isLeido());
            
            collection.insertOne(doc);
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error creando mensaje: " + e.getMessage());
        }
    }
    
    public Mensaje findById(String idMensaje) throws ErrorConectionMongoException {
        try {
            Document doc = collection.find(Filters.eq("id_mensaje", idMensaje)).first();
            return doc != null ? mapDocumentToMensaje(doc) : null;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando mensaje por ID: " + e.getMessage());
        }
    }
    
    public List<Mensaje> findByRemitente(Integer idRemitente, int limite) throws ErrorConectionMongoException {
        try {
            List<Mensaje> mensajes = new ArrayList<>();
            
            collection.find(Filters.eq("id_remitente", idRemitente))
                    .sort(Sorts.descending("fecha_hora"))
                    .limit(limite)
                    .forEach(doc -> mensajes.add(mapDocumentToMensaje(doc)));
            
            return mensajes;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando mensajes por remitente: " + e.getMessage());
        }
    }
    
    public List<Mensaje> findByDestinatario(Integer idDestinatario, int limite) throws ErrorConectionMongoException {
        try {
            List<Mensaje> mensajes = new ArrayList<>();
            
            collection.find(Filters.eq("id_destinatario", idDestinatario))
                    .sort(Sorts.descending("fecha_hora"))
                    .limit(limite)
                    .forEach(doc -> mensajes.add(mapDocumentToMensaje(doc)));
            
            return mensajes;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando mensajes por destinatario: " + e.getMessage());
        }
    }
    
    public List<Mensaje> findByDestinatarioYTipo(String idDestinatario, String tipo, int limite) throws ErrorConectionMongoException {
        try {
            List<Mensaje> mensajes = new ArrayList<>();
            
            collection.find(Filters.and(
                    Filters.eq("id_destinatario", idDestinatario),
                    Filters.eq("tipo", tipo)
            ))
            .sort(Sorts.descending("fecha_hora"))
            .limit(limite)
            .forEach(doc -> mensajes.add(mapDocumentToMensaje(doc)));
            
            return mensajes;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando mensajes por destinatario y tipo: " + e.getMessage());
        }
    }
    
    public List<Mensaje> findConversacionPrivada(Integer idUsuario1, Integer idUsuario2, int limite) throws ErrorConectionMongoException {
        try {
            List<Mensaje> mensajes = new ArrayList<>();
            
            collection.find(Filters.and(
                    Filters.eq("tipo", "privado"),
                    Filters.or(
                            Filters.and(Filters.eq("id_remitente", idUsuario1), Filters.eq("id_destinatario", idUsuario2)),
                            Filters.and(Filters.eq("id_remitente", idUsuario2), Filters.eq("id_destinatario", idUsuario1))
                    )
            ))
            .sort(Sorts.descending("fecha_hora"))
            .limit(limite)
            .forEach(doc -> mensajes.add(mapDocumentToMensaje(doc)));
            
            return mensajes;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando conversación privada: " + e.getMessage());
        }
    }
    
    public List<Mensaje> findNoLeidosByDestinatario(Integer idDestinatario) throws ErrorConectionMongoException {
        try {
            List<Mensaje> mensajes = new ArrayList<>();
            
            collection.find(Filters.and(
                    Filters.eq("id_destinatario", idDestinatario),
                    Filters.eq("leido", false)
            ))
            .sort(Sorts.descending("fecha_hora"))
            .forEach(doc -> mensajes.add(mapDocumentToMensaje(doc)));
            
            return mensajes;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando mensajes no leídos: " + e.getMessage());
        }
    }
    
    public void marcarComoLeido(String idMensaje) throws ErrorConectionMongoException {
        try {
            collection.updateOne(
                    Filters.eq("id_mensaje", idMensaje),
                    new Document("$set", new Document("leido", true))
            );
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error marcando mensaje como leído: " + e.getMessage());
        }
    }
    
    public int countAll() throws ErrorConectionMongoException {
        try {
            return (int) collection.countDocuments();
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando mensajes: " + e.getMessage());
        }
    }
    
    public int countByTipo(String tipo) throws ErrorConectionMongoException {
        try {
            return (int) collection.countDocuments(Filters.eq("tipo", tipo));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando mensajes por tipo: " + e.getMessage());
        }
    }
    
    public int countByRemitente(Integer idRemitente) throws ErrorConectionMongoException {
        try {
            return (int) collection.countDocuments(Filters.eq("id_remitente", idRemitente));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando mensajes por remitente: " + e.getMessage());
        }
    }
    
    public int countByDestinatario(Integer idDestinatario) throws ErrorConectionMongoException {
        try {
            return (int) collection.countDocuments(Filters.eq("id_destinatario", idDestinatario));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando mensajes por destinatario: " + e.getMessage());
        }
    }
    
    private Mensaje mapDocumentToMensaje(Document doc) {
        Mensaje mensaje = new Mensaje();
        mensaje.setIdMensaje(doc.getString("id_mensaje"));
        mensaje.setIdRemitente(doc.getInteger("id_remitente"));
        mensaje.setIdDestinatario(doc.getInteger("id_destinatario"));
        mensaje.setContenido(doc.getString("contenido"));
        mensaje.setTipo(doc.getString("tipo"));
        mensaje.setLeido(doc.getBoolean("leido", false));
        
        String fechaHoraStr = doc.getString("fecha_hora");
        if (fechaHoraStr != null) {
            mensaje.setFechaHora(LocalDateTime.parse(fechaHoraStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        return mensaje;
    }
}
