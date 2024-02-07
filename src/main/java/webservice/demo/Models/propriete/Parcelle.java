package webservice.demo.Models.propriete;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;
import java.text.SimpleDateFormat;

public class Parcelle {
    String id;
    double longueur;
    double largeur;
    String terrain;
    double superficie;
    String culture;
    String proprietaire;

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
    
    public double getLongueur()
    {
        return this.longueur;
    }

    public void setLongueur(double p) throws Exception
    {
        if (p > 0) {
            this.longueur = p;
        }
        else{
            throw new Exception("Longueur-non-valide");
        }
        return;
    }

    public double getLargeur()
    {
        return this.largeur;
    }

    public void setLargeur(double p) throws Exception
    {
        if (p > 0) {
            this.largeur = p;
        }
        else{
            throw new Exception("Largeur-non-valide");
        }
        return;
    }

    public String getTerrain()
    {
        return this.terrain;
    }

    public void setTerrain(String s) throws Exception
    {
        if(s != null){
            this.terrain = s;
        }
        else{
            throw new Exception("Terrain-non-valide");
        }
        return;
    }

    public double getSuperficie()
    {
        return this.superficie;
    }

    public void setSuperficie(double p) throws Exception
    {
        if (p > 0) {
            this.superficie = p;
        }
        else{
            throw new Exception("Superficie-non-valide");
        }
        return;
    }

    public String getCulture()
    {
        return this.culture;
    }

    public void setCulture(String s) throws Exception
    {
        if(s != null){
            this.culture = s;
        }
        else{
            throw new Exception("Culture-non-valide");
        }
        return;
    }

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

    public Parcelle() throws Exception
    {

    }

    public Parcelle(String nId,String c) throws Exception
    {
        this.setId(nId);
        this.setCulture(c);
    }

    public Parcelle(double lo, double larg, String t) throws Exception
    {
        this.setLongueur(lo);
        this.setLargeur(larg);
        this.setTerrain(t);
    }

    public Parcelle(String nId, double s, String t) throws Exception
    {
        this.setId(nId);
        this.setSuperficie(s);
        this.setTerrain(t);
    }

    public Parcelle(String nId, double lo, double larg, String t) throws Exception
    {
        this.setId(nId);
        this.setLongueur(lo);
        this.setLargeur(larg);
        this.setTerrain(t);
    }

    public Parcelle(String nId,double s,String t,String p) throws Exception
    {
        this.setId(nId);
        this.setSuperficie(s);
        this.setTerrain(t);
        this.setProprietaire(p);
    }

    public void insertParcelle(Connection c, Parcelle p) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into Parcelle values ('P' || nextval('parcelleSeq'),"+p.getLongueur()+","+p.getLargeur()+",'"+p.getTerrain()+"')");
        s.close();
    }

    public void insertParcelleCulturePossible(Connection c, Parcelle p) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into ParcelleCulturePossible values ('"+p.getId()+"','"+p.getCulture()+"')");
        s.close();
    }

    public Parcelle[] findSpecifiedTerrainParcelle(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from ParcelleDetails where terrain = '"+t+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Parcelle(r.getString(1),r.getDouble(2),r.getString(3)));
        }
        Parcelle[] allParcelle = new Parcelle[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public Parcelle[] findSpecifiedUserParcelle(Connection c, String u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from ParcelleDetailsTerrainProprietaire where proprietaire = '"+u+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Parcelle(r.getString(1),r.getDouble(2),r.getString(3),r.getString(4)));
        }
        Parcelle[] allParcelle = new Parcelle[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public Parcelle[] findSpecifiedParcelleCulturePossible(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from ParcelleCulturePossibleDetails where idParcelle = '"+t+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Parcelle(r.getString(1),r.getString(2)));
        }
        Parcelle[] allParcelle = new Parcelle[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public Parcelle[] findParcelle(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from ParcelleDetails");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Parcelle(r.getString(1),r.getDouble(2),r.getString(3)));
        }
        Parcelle[] allParcelle = new Parcelle[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public double getNbParcelleMoyen(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("SELECT AVG(nbParcelle) as nbMoyen FROM (SELECT COUNT(idParcelle) as nbParcelle FROM ParcelleDetailsTerrainProprietaire WHERE proprietaire = '"+nId+"' GROUP BY terrain) AS getNbMoyen");
        double nC = 0; 
        if (r.next()) {
            nC = r.getDouble(1);
        }
        s.close();
        return nC;
    }

    public double getSurfaceMoyenne(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("SELECT avg(superficie) from ParcelleDetailsTerrainProprietaire where proprietaire = '"+nId+"'");
        double nC = 0; 
        if (r.next()) {
            nC = r.getDouble(1);
        }
        s.close();
        return nC;
    }
}
