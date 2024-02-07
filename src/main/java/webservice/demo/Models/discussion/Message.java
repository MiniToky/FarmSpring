package webservice.demo.Models.discussion;
import java.sql.*;
import java.util.Vector;
import java.util.Date;
import webservice.demo.Models.tools.Connect;
import java.text.SimpleDateFormat;
import java.time.Instant;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class Message {
    String envoyeur;
    String receveur;
    String contenu;
    Timestamp envoi;

    public String getEnvoyeur()
    {
        return this.envoyeur;
    }

    public void setEnvoyeur(String e) throws Exception
    {
        if (e != null) {
            this.envoyeur = e;
        }
        else{
            throw new Exception("Envoyeur-non-valide");
        }
        return;
    }

    public String getReceveur()
    {
        return this.receveur;
    }

    public void setReceveur(String e) throws Exception
    {
        if (e != null) {
            this.receveur = e;
        }
        else{
            throw new Exception("Receveur-non-valide");
        }
        return;
    }

    public String getContenu()
    {
        return this.contenu;
    }

    public void setContenu(String e) throws Exception
    {
        if (e != null) {
            this.contenu = e;
        }
        else{
            throw new Exception("Contenu-non-valide");
        }
        return;
    }

    public Timestamp getEnvoi()
    {
        return this.envoi;
    }

    public void setEnvoi(Timestamp e) throws Exception
    {
        if (e != null) {
            this.envoi = e;
        }
        else{
            throw new Exception("Envoi-non-valide");
        }
        return;
    }

    public Message() throws Exception
    {

    }

    public Message(String e,String r,String c,Timestamp t) throws Exception
    {
        this.setEnvoyeur(e);
        this.setReceveur(r);
        this.setContenu(c);
        this.setEnvoi(t);
    }

    public void insertMessage(MongoDatabase d, Message m) {
        MongoCollection<Document> messagesCollection = d.getCollection("farmgame_message");
        Document messageDocument = new Document()
                .append("envoyeur", m.getEnvoyeur())
                .append("receveur", m.getReceveur())
                .append("contenu", m.getContenu())
                .append("envoi", m.getEnvoi());

        messagesCollection.insertOne(messageDocument);
    }

    public Timestamp getNow() throws Exception
    {
        Instant currentInstant = Instant.now();
        Timestamp t = Timestamp.from(currentInstant);
        return t;
    }

    public Message parseDocument(Document d) throws Exception
    {
        String e = d.getString("envoyeur");
        String r = d.getString("receveur");
        String c = d.getString("contenu");
        Date date = d.getDate("envoi");
        Timestamp t = new Timestamp(date.getTime());

        Message m = new Message(e, r, c, t);
        return m;
    }

    public Message[] findConservationUser(MongoDatabase d,String e,String r) throws Exception
    {
        MongoCollection<Document> messagesCollection = d.getCollection("farmgame_message");
        Document query = new Document("receveur", r)
                .append("envoyeur", e);
        FindIterable<Document> documents = messagesCollection.find(query);
        List<Message> messages = new ArrayList<>();
        for (Document document : documents) {
            Message message = parseDocument(document);
            messages.add(message);
        }

        return messages.toArray(new Message[0]);
    }
}

