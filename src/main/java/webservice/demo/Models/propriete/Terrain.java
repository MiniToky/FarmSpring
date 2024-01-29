package webservice.demo.Models.propriete;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;
import java.text.SimpleDateFormat;

public class Terrain {
    String id;
    String proprietaire;
    String description;
    String localisation;
    String photo;
    Date creation;
    int etat;

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

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String s) throws Exception
    {
        if(s != null){
            this.description = s;
        }
        else{
            throw new Exception("Description-non-valide");
        }
        return;
    }
    
    public String getLocalisation()
    {
        return this.localisation;
    }

    public void setLocalisation(String s) throws Exception
    {
        if(s != null){
            this.localisation = s;
        }
        else{
            throw new Exception("Localisation-non-valide");
        }
        return;
    }

    public String getPhoto()
    {
        return this.photo;
    }

    public void setPhoto(String s) throws Exception
    {
        if(s != null){
            this.photo = s;
        }
        else{
            throw new Exception("Photo-non-valide");
        }
        return;
    }

    public Date getCreation()
    {
        return this.creation;
    }

    public void setCreation(Date s) throws Exception
    {
        if(s != null){
            this.creation = s;
        }
        else{
            throw new Exception("Creation-non-valide");
        }
        return;
    }

    public int getEtat()
    {
        return this.etat;
    }

    public void setEtat(int p) throws Exception
    {
        if (p >= 0) {
            this.etat = p;
        }
        else{
            throw new Exception("Etat-non-valide");
        }
        return;
    }

    public Terrain() throws Exception
    {

    }

    public Terrain(String nId,String d,String p) throws Exception
    {
        this.setId(nId);
        this.setDescription(d);
        this.setPhoto(p);
    }

    public Terrain(String pr, String d, String l, String p, Date dC, int e) throws Exception
    {
        this.setProprietaire(pr);
        this.setDescription(d);
        this.setLocalisation(l);
        this.setPhoto(p);
        this.setCreation(dC);
        this.setEtat(e);
    }

    public Terrain(String nId, String pr, String d, String l, String p, Date dC, int e) throws Exception
    {
        this.setId(nId);
        this.setProprietaire(pr);
        this.setDescription(d);
        this.setLocalisation(l);
        this.setPhoto(p);
        this.setCreation(dC);
        this.setEtat(e);
    }

    public void insertTerrain(Connection c, Terrain t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into Terrain values ('T' || nextval('terrainSeq'),'"+t.getProprietaire()+"','"+t.getDescription()+"','"+t.getLocalisation()+"','"+t.getPhoto()+"',TO_DATE('"+t.getCreation()+"','YYYY-MM-DD'),"+t.getEtat()+")");
        s.close();
    }

    public void validerTerrain(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("update Terrain set etat = 1 where idTerrain = '"+t+"'");
        s.close();
    }

    public void updateTerrain(Connection c,Terrain t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("update Terrain set description = '"+t.getDescription()+"', photo = '"+t.getPhoto()+"' where idTerrain = '"+t.getId()+"'");
        s.close();
    }

    public Terrain[] findAllTerrain(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from TerrainDetails");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Terrain(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getDate(6),r.getInt(7)));
        }
        Terrain[] allTerrain = new Terrain[v.size()];
        v.copyInto(allTerrain);
        s.close();
        return allTerrain;
    }

    public Terrain findSpecifiedTerrain(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from TerrainDetails where idTerrain = '"+nId+"'");
        Terrain nC = new Terrain();
        if (r.next()) {
            Terrain nC2 = new Terrain(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getDate(6),r.getInt(7));
            s.close();
            return nC2;
        }
        s.close();
        return nC;
    }

    public Terrain[] findAllUserTerrain(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Terrain where proprietaire = '"+nId+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Terrain(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getDate(6),r.getInt(7)));
        }
        Terrain[] allTerrain = new Terrain[v.size()];
        v.copyInto(allTerrain);
        s.close();
        return allTerrain;
    }

    public Terrain[] findAllFirstUserTerrain(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Terrain where proprietaire = '"+nId+"' limit 3");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Terrain(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getDate(6),r.getInt(7)));
        }
        Terrain[] allTerrain = new Terrain[v.size()];
        v.copyInto(allTerrain);
        s.close();
        return allTerrain;
    }

    public Terrain[] findAllUserTerrainNonValide(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Terrain where proprietaire = '"+nId+"' and etat = 0");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Terrain(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getDate(6),r.getInt(7)));
        }
        Terrain[] allTerrain = new Terrain[v.size()];
        v.copyInto(allTerrain);
        s.close();
        return allTerrain;
    }

    public Date getSqlDate(String d) throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = dateFormat.parse(d);
        Date nD = new Date(utilDate.getTime());
        return nD;
    }

    public double getTerrainNb(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select count (idTerrain) from Terrain where proprietaire = '"+nId+"'");
        double nC = 0; 
        if (r.next()) {
            nC = r.getInt(1);
        }
        s.close();
        return nC;
    }
}
