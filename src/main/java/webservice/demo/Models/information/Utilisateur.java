package webservice.demo.Models.information;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;
import java.text.SimpleDateFormat;

public class Utilisateur {
    String id;
    String nom;
    String prenom;
    Date naissance;
    String mail;
    String motDePasse;
    String pseudo;

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

    public String getPrenom()
    {
        return this.prenom;
    }

    public void setPrenom(String s) throws Exception
    {
        if(s != null){
            this.prenom = s;
        }
        else{
            throw new Exception("Prenom-non-valide");
        }
        return;
    }

    public Date getNaissance()
    {
        return this.naissance;
    }

    public void setNaissance(Date s) throws Exception
    {
        if(s != null){
            this.naissance = s;
        }
        else{
            throw new Exception("Date-non-valide");
        }
        return;
    }


    public String getMail(){
        return this.mail;
    }

    public void setMail(String s) throws Exception
    {
        if(s != null){
            this.mail = s;
        }
        else{
            throw new Exception("Mail-non-valide");
        }
        return;
    }

    public String getMotDePasse(){
        return this.motDePasse;
    }

    public void setMotDePasse(String s) throws Exception
    {
        if(s != null){
            this.motDePasse = s;
        }
        else{
            throw new Exception("MotDePasse-non-valide");
        }
        return;
    }

    public String getPseudo(){
        return this.pseudo;
    }

    public void setPseudo(String s) throws Exception
    {
        if(s != null){
            this.pseudo = s;
        }
        else{
            throw new Exception("Pseudo-non-valide");
        }
        return;
    }

    public Utilisateur() throws Exception
    {

    }

    public Utilisateur(String p) throws Exception
    {
        this.setPseudo(p);
    }

    public Utilisateur(String n, String p, Date d, String m, String pwd, String ps) throws Exception
    {
        this.setNom(n);
        this.setPrenom(p);
        this.setNaissance(d);
        this.setMail(m);
        this.setMotDePasse(pwd);
        this.setPseudo(ps);
    }

    public Utilisateur(String nId, String n, String p, Date d, String m, String pwd, String ps) throws Exception
    {
        this.setId(nId);
        this.setNom(n);
        this.setPrenom(p);
        this.setNaissance(d);
        this.setMail(m);
        this.setMotDePasse(pwd);
        this.setPseudo(ps);
    }

    public void insertUtilisateur(Connection c, Utilisateur u) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        int n = s.executeUpdate("insert into Utilisateur values ('USER0' || nextval('utilisateurSeq'),'"+u.getNom()+"','"+u.getPrenom()+"',TO_DATE('"+u.getNaissance()+"','YYYY-MM-DD'),'"+u.getMail()+"','"+u.getMotDePasse()+"','"+u.getPseudo()+"')");
        s.close();
    }

    public Utilisateur[] findUtilisateur(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Utilisateur");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Utilisateur(r.getString(1),r.getString(2),r.getString(3),r.getDate(4),r.getString(5),r.getString(6),r.getString(7)));
        }
        Utilisateur[] allUtilisateur = new Utilisateur[v.size()];
        v.copyInto(allUtilisateur);
        s.close();
        return allUtilisateur;
    }

    public Utilisateur[] findProprietaire(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select pseudo from Utilisateur");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new Utilisateur(r.getString(1)));
        }
        Utilisateur[] allUtilisateur = new Utilisateur[v.size()];
        v.copyInto(allUtilisateur);
        s.close();
        return allUtilisateur;
    }

    public Utilisateur findSpecifiedUtilisateur(Connection c, String m, String pwd) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Utilisateur where mail = '" +m+"' and motDePasse = '"+pwd+"'");
        Utilisateur p = new Utilisateur();
        if (r.next()) {     
            Utilisateur nP = new Utilisateur(r.getString(1),r.getString(2),r.getString(3),r.getDate(4),r.getString(5),r.getString(6),r.getString(7));
            return nP;
        }
        s.close();
        return p;
    }

    public int checkLogin(Connection c, String m, String pwd) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Utilisateur where mail = '" +m+"' and motDePasse = '"+pwd+"'");
        int n = 0;
        if (r.next()) {     
            n = 1;
        }
        s.close();
        return n;
    }

    public int checkMail(Connection c, String m) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from Utilisateur where mail = '" +m+"'");
        int n = 0;
        if (r.next()) {     
            n = 1;
        }
        s.close();
        return n;
    } 

    public Date getSqlDate(String d) throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate = dateFormat.parse(d);
        Date nD = new Date(utilDate.getTime());
        return nD;
    }   
}