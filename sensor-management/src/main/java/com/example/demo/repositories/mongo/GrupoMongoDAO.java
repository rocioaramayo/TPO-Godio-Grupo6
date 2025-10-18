package com.example.demo.repositories.mongo;

import com.example.demo.connections.MongoPool;
import com.example.demo.exceptions.ErrorConectionMongoException;
import com.example.demo.modelo.Grupo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class GrupoMongoDAO {
    private static GrupoMongoDAO instance;
    private MongoCollection<Document> collection;
    
    private GrupoMongoDAO() {
        try {
            MongoDatabase database = MongoPool.getInstancia().getConnection("sensor_management");
            this.collection = database.getCollection("grupos");
        } catch (ErrorConectionMongoException e) {
            System.err.println("Error inicializando GrupoMongoDAO: " + e.getMessage());
        }
    }
    
    public static GrupoMongoDAO getInstance() {
        if (instance == null) instance = new GrupoMongoDAO();
        return instance;
    }
    
    public void crear(Grupo grupo) throws ErrorConectionMongoException {
        try {
            Document doc = new Document()
                    .append("id_grupo", grupo.getIdGrupo())
                    .append("nombre_grupo", grupo.getNombreGrupo())
                    .append("descripcion", grupo.getDescripcion())
                    .append("tipo_grupo", grupo.getTipoGrupo())
                    .append("estado", grupo.getEstado())
                    .append("usuarios_miembros", grupo.getUsuariosMiembros());
            
            collection.insertOne(doc);
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error creando grupo: " + e.getMessage());
        }
    }
    
    public Grupo findById(String idGrupo) throws ErrorConectionMongoException {
        try {
            Document doc = collection.find(Filters.eq("id_grupo", idGrupo)).first();
            return doc != null ? mapDocumentToGrupo(doc) : null;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando grupo por ID: " + e.getMessage());
        }
    }
    
    public List<Grupo> findAll() throws ErrorConectionMongoException {
        try {
            List<Grupo> grupos = new ArrayList<>();
            
            collection.find()
                    .sort(Sorts.ascending("nombre_grupo"))
                    .forEach(doc -> grupos.add(mapDocumentToGrupo(doc)));
            
            return grupos;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error listando grupos: " + e.getMessage());
        }
    }
    
    public List<Grupo> findByTipo(String tipoGrupo) throws ErrorConectionMongoException {
        try {
            List<Grupo> grupos = new ArrayList<>();
            
            collection.find(Filters.eq("tipo_grupo", tipoGrupo))
                    .sort(Sorts.ascending("nombre_grupo"))
                    .forEach(doc -> grupos.add(mapDocumentToGrupo(doc)));
            
            return grupos;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando grupos por tipo: " + e.getMessage());
        }
    }
    
    public List<Grupo> findByEstado(String estado) throws ErrorConectionMongoException {
        try {
            List<Grupo> grupos = new ArrayList<>();
            
            collection.find(Filters.eq("estado", estado))
                    .sort(Sorts.ascending("nombre_grupo"))
                    .forEach(doc -> grupos.add(mapDocumentToGrupo(doc)));
            
            return grupos;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando grupos por estado: " + e.getMessage());
        }
    }
    
    public List<Grupo> findByUsuario(Integer idUsuario) throws ErrorConectionMongoException {
        try {
            List<Grupo> grupos = new ArrayList<>();
            
            collection.find(Filters.eq("usuarios_miembros", idUsuario))
                    .sort(Sorts.ascending("nombre_grupo"))
                    .forEach(doc -> grupos.add(mapDocumentToGrupo(doc)));
            
            return grupos;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error buscando grupos por usuario: " + e.getMessage());
        }
    }
    
    public void actualizar(Grupo grupo) throws ErrorConectionMongoException {
        try {
            Document doc = new Document()
                    .append("id_grupo", grupo.getIdGrupo())
                    .append("nombre_grupo", grupo.getNombreGrupo())
                    .append("descripcion", grupo.getDescripcion())
                    .append("tipo_grupo", grupo.getTipoGrupo())
                    .append("estado", grupo.getEstado())
                    .append("usuarios_miembros", grupo.getUsuariosMiembros());
            
            collection.replaceOne(Filters.eq("id_grupo", grupo.getIdGrupo()), doc);
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error actualizando grupo: " + e.getMessage());
        }
    }
    
    public void actualizarEstado(String idGrupo, String estado) throws ErrorConectionMongoException {
        try {
            collection.updateOne(
                    Filters.eq("id_grupo", idGrupo),
                    new Document("$set", new Document("estado", estado))
            );
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error actualizando estado de grupo: " + e.getMessage());
        }
    }
    
    public void agregarMiembro(String idGrupo, Integer idUsuario) throws ErrorConectionMongoException {
        try {
            collection.updateOne(
                    Filters.eq("id_grupo", idGrupo),
                    new Document("$addToSet", new Document("usuarios_miembros", idUsuario))
            );
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error agregando miembro al grupo: " + e.getMessage());
        }
    }
    
    public void removerMiembro(String idGrupo, Integer idUsuario) throws ErrorConectionMongoException {
        try {
            collection.updateOne(
                    Filters.eq("id_grupo", idGrupo),
                    new Document("$pull", new Document("usuarios_miembros", idUsuario))
            );
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error removiendo miembro del grupo: " + e.getMessage());
        }
    }
    
    public int countAll() throws ErrorConectionMongoException {
        try {
            return (int) collection.countDocuments();
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando grupos: " + e.getMessage());
        }
    }
    
    public int countByTipo(String tipoGrupo) throws ErrorConectionMongoException {
        try {
            return (int) collection.countDocuments(Filters.eq("tipo_grupo", tipoGrupo));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando grupos por tipo: " + e.getMessage());
        }
    }
    
    public int countByEstado(String estado) throws ErrorConectionMongoException {
        try {
            return (int) collection.countDocuments(Filters.eq("estado", estado));
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando grupos por estado: " + e.getMessage());
        }
    }
    
    public int countMiembrosGrupo(String idGrupo) throws ErrorConectionMongoException {
        try {
            Document doc = collection.find(Filters.eq("id_grupo", idGrupo)).first();
            if (doc != null) {
                List<?> miembros = doc.getList("usuarios_miembros", Object.class);
                return miembros != null ? miembros.size() : 0;
            }
            return 0;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error contando miembros del grupo: " + e.getMessage());
        }
    }
    
    public boolean esMiembroDelGrupo(String idGrupo, Integer idUsuario) throws ErrorConectionMongoException {
        try {
            Document doc = collection.find(Filters.and(
                    Filters.eq("id_grupo", idGrupo),
                    Filters.eq("usuarios_miembros", idUsuario)
            )).first();
            
            return doc != null;
            
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error verificando membresía: " + e.getMessage());
        }
    }
    
    private Grupo mapDocumentToGrupo(Document doc) {
        Grupo grupo = new Grupo();
        grupo.setIdGrupo(doc.getString("id_grupo"));
        grupo.setNombreGrupo(doc.getString("nombre_grupo"));
        grupo.setDescripcion(doc.getString("descripcion"));
        grupo.setTipoGrupo(doc.getString("tipo_grupo"));
        grupo.setEstado(doc.getString("estado"));
        
        List<?> miembrosList = doc.getList("usuarios_miembros", Object.class);
        if (miembrosList != null) {
            List<Integer> miembros = new ArrayList<>();
            for (Object miembro : miembrosList) {
                if (miembro instanceof Integer) {
                    miembros.add((Integer) miembro);
                }
            }
            grupo.setUsuariosMiembros(miembros);
        }
        
        return grupo;
    }
}
