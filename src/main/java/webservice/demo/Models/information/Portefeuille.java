package webservice.demo.Models.information;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;

public class Portefeuille {
    String id;
    String proprietaire;
    double valeur;

    public String getProprietaire()
    {
        return this.proprietaire;
    }

    public void setProprietaire(String s) throws Exception
    {
        if(s != null){
            this.proprietaire = s;
        }
        else{
            throw new Exception("Proprietaire-non-valide");
        }
        return;
    }

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

    public double getValeur()
    {
        return this.valeur;
    }

    public void setValeur(double v)
    {
        this.valeur = v;
    }

    public Portefeuille() throws Exception
    {

    }

    public Portefeuille(String u) throws Exception
    {
        this.setProprietaire(u);
    }

    public Portefeuille(String nId,String u) throws Exception
    {
        this.setId(nId);
        this.setProprietaire(u);
    }

    public Portefeuille(String nId,double v) throws Exception
    {
        this.setId(nId);
        this.setValeur(v);
    }

    public void insertPortefeuille(Connection c, Portefeuille u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into Portefeuille values ('PO' || nextval('portefeuilleSeq'),'"+u.getProprietaire()+"')");
        s.close();
    }

    public void insertPortefeuilleActivite(Connection c, Portefeuille u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into PortefeuilleActivite values ('"+u.getId()+"',"+u.getValeur()+")");
        s.close();
    }

    public String insertPortefeuilleActivite(Portefeuille u) throws Exception
    {
        String q = "insert into PortefeuilleActivite values ('"+u.getId()+"',"+u.getValeur()+")";
        return q;
    }

    public double getUserPortefeuille(Connection c, String u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select sum from PortefeuilleUser where idUtilisateur = '"+u+"'");
        double nC = 0; 
        if (r.next()) {
            nC = r.getInt(1);
        }
        s.close();
        return nC;
    }

    public int checkPortefeuille(Connection c, String u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Portefeuille where idUtilisateur = '" +u+"'");
        int n = 0;
        if (r.next()) {     
            n = 1;
        }
        s.close();
        return n;
    }

    public String getIdLastRecord(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select idPortefeuille from Portefeuille order by idPortefeuille desc limit 1");
        String n = "";
        if (r.next()) {     
            n = r.getString(1);
        }
        s.close();
        return n;   
    }

    public String getUserPortefeuilleId(Connection c,String u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select idPortefeuille from Portefeuille where idUtilisateur = '"+u+"'");
        String n = "";
        if (r.next()) {     
            n = r.getString(1);
        }
        s.close();
        return n;   
    }
}
