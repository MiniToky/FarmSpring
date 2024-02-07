package webservice.demo.Models.information;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;
import java.text.SimpleDateFormat;

public class Culture {
    String proprietaire;
    String id;
    String nom;
    String type;
    double prixAchat;
    double prixVente;
    String saison;
    String photo;
    
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
    
    public String getType()
    {
        return this.type;
    }

    public void setType(String s) throws Exception
    {
        if(s != null){
            this.type = s;
        }
        else{
            throw new Exception("Type-non-valide");
        }
        return;
    }
    
    public double getPrixAchat()
    {
        return this.prixAchat;
    }

    public void setPrixAchat(double p) throws Exception
    {
        if (p > 0) {
            this.prixAchat = p;
        }
        else{
            throw new Exception("PrixAchat-non-valide");
        }
        return;
    }

    public double getPrixVente()
    {
        return this.prixVente;
    }

    public void setPrixVente(double p) throws Exception
    {
        if (p > 0) {
            this.prixVente = p;
        }
        else{
            throw new Exception("PrixVente-non-valide");
        }
        return;
    }

    public String getSaison()
    {
        return this.saison;
    }

    public void setSaison(String s) throws Exception
    {
        if(s != null){
            this.saison = s;
        }
        else{
            throw new Exception("Saison-non-valide");
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

    public Culture() throws Exception
    {

    }

    public Culture(String nId, String pr) throws Exception
    {
        this.setId(nId);
        this.setProprietaire(pr);
    }

    public Culture(String n, String t, double pA, double pV, String s, String p) throws Exception
    {
        this.setNom(n);
        this.setType(t);
        this.setPrixAchat(pA);
        this.setPrixVente(pV);
        this.setSaison(s);
        this.setPhoto(p);
    }

    public Culture(String nId, String n, String t, double pA, double pV, String s, String p) throws Exception
    {
        this.setId(nId);
        this.setNom(n);
        this.setType(t);
        this.setPrixAchat(pA);
        this.setPrixVente(pV);
        this.setSaison(s);
        this.setPhoto(p);
    }

    public Culture(String pr, String nId, String n, String t, double pA, double pV, String s) throws Exception
    {
        this.setProprietaire(pr);
        this.setId(nId);
        this.setNom(n);
        this.setType(t);
        this.setPrixAchat(pA);
        this.setPrixVente(pV);
        this.setSaison(s);
    }

    public void insertCulture(Connection c, Culture u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into Culture values ('CU' || nextval('cultureSeq'),'"+u.getNom()+"','"+u.getType()+"',"+u.getPrixAchat()+","+u.getPrixVente()+",'"+u.getSaison()+"','"+u.getPhoto()+"')");
        s.close();
    }

    public String getIdLastRecord(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select idCulture from Culture order by idCulture desc");
        String n = "";
        if (r.next()) {     
            n = r.getString(1);
        }
        s.close();
        return n;   
    }

    public void insertCultureUtilisateur(Connection c, Culture u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into CultureUtilisateur values ('"+u.getId()+"','"+u.getProprietaire()+"')");
        s.close();
    }   

    public Culture[] findCulture(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from CultureDetails");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Culture(r.getString(1),r.getString(2),r.getString(3),r.getDouble(4),r.getDouble(5),r.getString(6),r.getString(7)));
        }
        Culture[] allCulture = new Culture[v.size()];
        v.copyInto(allCulture);
        s.close();
        return allCulture;
    }

    public Culture[] findSpecifiedUserCulture(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from AllUtilisateurCulture where idUtilisateur = '"+nId+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Culture(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getDouble(5),r.getDouble(6),r.getString(7)));
        }
        Culture[] allCulture = new Culture[v.size()];
        v.copyInto(allCulture);
        s.close();
        return allCulture;
    }

    public Culture[] findSpecifiedUserCulture(Connection c,String nId,String t) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from AllUtilisateurCulture where idUtilisateur = '"+nId+"' and type = '"+t+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Culture(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getDouble(5),r.getDouble(6),r.getString(7)));
        }
        Culture[] allCulture = new Culture[v.size()];
        v.copyInto(allCulture);
        s.close();
        return allCulture;
    } 

    public Culture[] findAllUserCulture(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from AllUtilisateurCultureDetails");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Culture(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getDouble(5),r.getDouble(6),r.getString(7)));
        }
        Culture[] allCulture = new Culture[v.size()];
        v.copyInto(allCulture);
        s.close();
        return allCulture;
    } 

    public Culture findSpecifiedCulture(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from AllUtilisateurCultureDetails where idCulture = '"+nId+"'");
        Culture nC = new Culture();
        if (r.next()) {
            Culture nC2 = new Culture(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getDouble(5),r.getDouble(6),r.getString(7));
            s.close();
            return nC2;
        }
        s.close();
        return nC;
    }

    public double getCultureNb(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select count (idCulture) from AllUtilisateurCulture where idUtilisateur = '"+nId+"'");
        double nC = 0; 
        if (r.next()) {
            nC = r.getInt(1);
        }
        s.close();
        return nC;
    }

    public Culture[] findSpecifiedAllUserTypeCulture(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from AllUtilisateurCultureDetails where type = '"+nId+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Culture(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getDouble(5),r.getDouble(6),r.getString(7)));
        }
        Culture[] allCulture = new Culture[v.size()];
        v.copyInto(allCulture);
        s.close();
        return allCulture;
    }

    public Culture[] findSpecifiedAllUserCulture(Connection c,String nId) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from AllUtilisateurCultureDetails where proprietaire = '"+nId+"'");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Culture(r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getDouble(5),r.getDouble(6),r.getString(7)));
        }
        Culture[] allCulture = new Culture[v.size()];
        v.copyInto(allCulture);
        s.close();
        return allCulture;
    }
}
