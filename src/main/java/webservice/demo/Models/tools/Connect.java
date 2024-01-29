package webservice.demo.Models.tools;
import java.sql.*;

public class Connect {
    public Connection makeConnection() throws Exception
	{
		Class.forName("org.postgresql.Driver");
		// Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/farmgame","farmgame","farmgame");
		Connection c = DriverManager.getConnection("jdbc:postgresql://pg-17c27b3-farmgame-base.a.aivencloud.com:10904/farmgame?sslmode=require","avnadmin","AVNS_b7FTvSRLYy2hwoMjnsl");
		return c;
	}
}
