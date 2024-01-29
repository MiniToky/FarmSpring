package webservice.demo.Models.information;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;

public class Saison {
    String id;
    String nom;
    Date debut;
    Date fin;

    public String getId()
    {
        return this.id;
    }

    public void setId(String s) throws Exception
    {
        if(s != null){
            this.id = s;
        }
        else{
            throw new Exception("Id-non-valide");
        }
        return;
    }

    public String getNom()
    {
        return this.nom;
    }

    public void setNom(String s) throws Exception
    {
        if(s != null){
            this.nom = s;
        }
        else{
            throw new Exception("Nom-non-valide");
        }
        return;
    }

    public Date getDebut()
    {
        return this.debut;
    }

    public void setDebut(Date s) throws Exception
    {
        if(s != null){
            this.debut = s;
        }
        else{
            throw new Exception("Debut-non-valide");
        }
        return;
    }

    public Date getFin()
    {
        return this.fin;
    }

    public void setFin(Date s) throws Exception
    {
        if(s != null){
            this.fin = s;
        }
        else{
            throw new Exception("Fin-non-valide");
        }
        return;
    }

    public Saison() throws Exception
    {

    }

    public Saison(String n, Date d, Date f) throws Exception
    {
        this.setNom(n);
        this.setDebut(d);
        this.setFin(f);
    }

    public Saison(String nId, String n, Date d, Date f) throws Exception
    {
        this.setId(nId);
        this.setNom(n);
        this.setDebut(d);
        this.setFin(f);
    }

    public Saison[] findSaison(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Saison");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Saison(r.getString(1),r.getString(2),r.getDate(3),r.getDate(4)));
        }
        Saison[] allSaison = new Saison[v.size()];
        v.copyInto(allSaison);
        s.close();
        return allSaison;
    }
}
