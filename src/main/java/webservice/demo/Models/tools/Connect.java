package webservice.demo.Models.tools;
import java.sql.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class Connect {
    public Connection makeConnection() throws Exception
	{
		Class.forName("org.postgresql.Driver");
		// Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/farmgame","farmgame","farmgame");
		Connection c = DriverManager.getConnection("jdbc:postgresql://pg-17c27b3-farmgame-base.a.aivencloud.com:10904/farmgame?sslmode=require","avnadmin","AVNS_b7FTvSRLYy2hwoMjnsl");
		return c;
	}

	public MongoDatabase makeMongoConnection() {
        String connectionString = "mongodb+srv://farmgame:CPbFKADgYuJpD1UC@cluster0.mjvdbvy.mongodb.net/";
        String databaseName = "farmgame";
        MongoClientURI uri = new MongoClientURI(connectionString);
        MongoClient mongoClient = new MongoClient(uri);
        return mongoClient.getDatabase(databaseName);
    }
}
