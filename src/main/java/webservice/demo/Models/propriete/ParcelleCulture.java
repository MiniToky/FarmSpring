package webservice.demo.Models.propriete;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;
import java.text.SimpleDateFormat;

public class ParcelleCulture {
    String terrain;
    String id;
    String culture;
    double quantite;
    Date dateCulture;
    double coutRevient;

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
    
    public double getQuantite()
    {
        return this.quantite;
    }

    public void setQuantite(double p) throws Exception
    {
        if (p > 0) {
            this.quantite = p;
        }
        else{
            throw new Exception("Quantite-non-valide");
        }
        return;
    }

    public Date getDateCulture()
    {
        return this.dateCulture;
    }

    public void setDateCulture(Date s) throws Exception
    {
        if(s != null){
            this.dateCulture = s;
        }
        else{
            throw new Exception("DateCulture-non-valide");
        }
        return;
    }

    public double getCoutRevient()
    {
        return this.coutRevient;
    }

    public void setCoutRevient(double p) throws Exception
    {
        if (p > 0) {
            this.coutRevient = p;
        }
        else{
            throw new Exception("CoutRevient-non-valide");
        }
        return;
    }

    public ParcelleCulture() throws Exception
    {

    }

    public ParcelleCulture(String c,double q,Date dC) throws Exception
    {
        this.setCulture(c);
        this.setQuantite(q);
        this.setDateCulture(dC);
    }

    public ParcelleCulture(String nId,String c,double q,Date dC) throws Exception
    {
        this.setId(nId);
        this.setCulture(c);
        this.setQuantite(q);
        this.setDateCulture(dC);
    }

    public ParcelleCulture(String nId,String c,double q,double cR,Date dC) throws Exception
    {
        this.setId(nId);
        this.setCulture(c);
        this.setQuantite(q);
        this.setCoutRevient(cR);
        this.setDateCulture(dC);
    }

    public ParcelleCulture(String t,String nId,String c,double q,double cR,Date dC) throws Exception
    {
        this.setTerrain(t);
        this.setId(nId);
        this.setCulture(c);
        this.setQuantite(q);
        this.setCoutRevient(cR);
        this.setDateCulture(dC);
    }

    public void insertParcelleCulture(Connection c, ParcelleCulture p) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into ParcelleCulture values ('"+p.getId()+"','"+p.getCulture()+"',"+p.getQuantite()+",TO_DATE('"+p.getDateCulture()+"','YYYY-MM-DD'))");
        s.close();
    }

    public ParcelleCulture[] findSpecifiedCultureHistoriqueCulture(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from HistoriqueCulture where culture = '"+t+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new ParcelleCulture(r.getString(1),r.getString(2),r.getDouble(3),r.getDouble(4),r.getDate(5)));
        }
        ParcelleCulture[] allParcelle = new ParcelleCulture[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public ParcelleCulture[] findSpecifiedProprietaireHistoriqueCulture(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from HistoriqueCulture where proprietaire = '"+t+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new ParcelleCulture(r.getString(1),r.getString(2),r.getDouble(3),r.getDouble(4),r.getDate(5)));
        }
        ParcelleCulture[] allParcelle = new ParcelleCulture[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public ParcelleCulture[] findSpecifiedParcelleHistoriqueCulture(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from HistoriqueCulture where idParcelle = '"+t+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new ParcelleCulture(r.getString(1),r.getString(2),r.getString(3),r.getDouble(4),r.getDouble(5),r.getDate(6)));
        }
        ParcelleCulture[] allParcelle = new ParcelleCulture[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public ParcelleCulture[] findHistoriqueCulture(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from HistoriqueCulture");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new ParcelleCulture(r.getString(1),r.getString(2),r.getString(3),r.getDouble(4),r.getDouble(5),r.getDate(6)));
        }
        ParcelleCulture[] allParcelle = new ParcelleCulture[v.size()];
        v.copyInto(allParcelle);
        s.close();
        return allParcelle;
    }

    public double getQuantiteCulture(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select sum(quantite) from HistoriqueCultureProprietaire WHERE proprietaire = '"+nId+"'");
        double nC = 0; 
        if (r.next()) {
            nC = r.getDouble(1);
        }
        s.close();
        return nC;
    }
}
