package webservice.demo.Models.information;
import java.sql.*;
import java.util.Vector;
import webservice.demo.Models.tools.Connect;

public class TypeCulture {
    String id;
    String nom;

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

    public TypeCulture() throws Exception
    {

    }

    public TypeCulture(String n) throws Exception
    {
        this.setNom(n);
    }

    public TypeCulture(String nId, String n) throws Exception
    {
        this.setId(nId);
        this.setNom(n);
    }

    public TypeCulture[] findType(Connection c) throws Exception
    {
        if (c == null) {
            Connect con = new Connect();
            c = con.makeConnection();
        }
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("select * from TypeCulture");
        Vector v = new Vector();
        while (r.next()) {
            v.add(new TypeCulture(r.getString(1),r.getString(2)));
        }
        TypeCulture[] allType = new TypeCulture[v.size()];
        v.copyInto(allType);
        s.close();
        return allType;
    }
}
