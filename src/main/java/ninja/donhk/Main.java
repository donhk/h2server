package ninja.donhk;

import ninja.donhk.database.DatabaseServer;
import ninja.donhk.helpers.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    public static void main(String[] args0) throws Exception {
        DatabaseServer server = new DatabaseServer("myuser", "mypass", "mydb");
        server.startServer();
        Connection conn = server.getConnection();

        Statement stmt = conn.createStatement();
        stmt.execute(Utils.resource2txt("schema.sql"));

        String sql1 = "insert into mytable (name) values (?)";
        PreparedStatement ps1 = conn.prepareStatement(sql1);
        ps1.setString(1, "fred");
        ps1.executeUpdate();

        String sql2 = "select name from mytable";
        PreparedStatement ps2 = conn.prepareStatement(sql2);
        ResultSet rs = ps2.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }

        server.stopServer();
    }
}
