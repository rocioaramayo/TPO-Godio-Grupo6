package connections;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import main.java.com.example.demo.exceptions.ErrorConectionMongoException;

public class MongoPool {

    private static MongoPool instancia;
    private String url;
    private MongoClient mongoClient;
    
    private MongoPool() {
        url = "mongodb://127.0.0.1:27017";
        mongoClient = MongoClients.create(url);
    }
    
    public static MongoPool getInstancia() {
        if (instancia == null) {
            instancia = new MongoPool();
        }
        return instancia;
    }
    
    public MongoDatabase getConnection(String database) throws ErrorConectionMongoException {
        try {
            MongoDatabase db = mongoClient.getDatabase(database);
            return db;
        } catch (Exception e) {
            throw new ErrorConectionMongoException("Error al conectar con MongoDB: " + e.getMessage());
        }
    }
}