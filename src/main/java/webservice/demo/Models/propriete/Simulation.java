package webservice.demo.Models.propriete;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;
import java.text.SimpleDateFormat;

public class Simulation {
    String id;
    String parcelle;
    String culture;
    double quantite;
    Date simulation;
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

    public String getParcelle()
    {
        return this.parcelle;
    }

    public void setParcelle(String s) throws Exception
    {
        if(s != null){
            this.parcelle = s;
        }
        else{
            throw new Exception("Parcelle-non-valide");
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

    public Date getSimulation()
    {
        return this.simulation;
    }

    public void setSimulation(Date s) throws Exception
    {
        if(s != null){
            this.simulation = s;
        }
        else{
            throw new Exception("Simulation-non-valide");
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

    public Simulation() throws Exception
    {

    }

    public Simulation(String p,String c,double q,Date s) throws Exception
    {
        this.setParcelle(p);
        this.setCulture(c);
        this.setQuantite(q);
        this.setSimulation(s);
    }

    public Simulation(String nId,String p,String c,double q,Date s) throws Exception
    {   
        this.setId(nId);
        this.setParcelle(p);
        this.setCulture(c);
        this.setQuantite(q);
        this.setSimulation(s);
    }

    public Simulation(String nId,String p,String c,double q,double cR,Date s) throws Exception
    {   
        this.setId(nId);
        this.setParcelle(p);
        this.setCulture(c);
        this.setQuantite(q);
        this.setCoutRevient(cR);
        this.setSimulation(s);
    }

    public void insertSimulation(Connection c, Simulation p) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into Simulation values ('SIM' || nextval('simulationSeq'),'"+p.getParcelle()+"','"+p.getCulture()+"',"+p.getQuantite()+",TO_DATE('"+p.getSimulation()+"','YYYY-MM-DD))");
        s.close();
    }

    public Simulation[] findSpecifiedParcelleHistoriqueSimulation(Connection c, String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from SimulationCulture where idParcelle = '"+t+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Simulation(r.getString(1),r.getString(2),r.getString(3),r.getDouble(4),r.getDouble(5),r.getDate(6)));
        }
        Simulation[] allSimulation = new Simulation[v.size()];
        v.copyInto(allSimulation);
        s.close();
        return allSimulation;
    }

    public Simulation[] findHistoriqueSimulation(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from SimulationCulture");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Simulation(r.getString(1),r.getString(2),r.getString(3),r.getDouble(4),r.getDouble(5),r.getDate(6)));
        }
        Simulation[] allSimulation = new Simulation[v.size()];
        v.copyInto(allSimulation);
        s.close();
        return allSimulation;
    }


    public double getTauxRendement(double c,double d)
    {
        double r = (c/d) * 100;
        return r;
    }
}
