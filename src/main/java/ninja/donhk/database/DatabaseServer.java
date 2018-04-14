package ninja.donhk.database;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseServer {

    private final String user;
    private final String password;
    private final String database;
    private Connection conn = null;
    private Server webServer = null;
    private Server tcpServer = null;

    public DatabaseServer(String user, String password, String database) {
        this.user = user;
        this.password = password;
        this.database = database;

    }

    public void startServer() throws Exception {
        new Thread(() -> {
            try {
                webServer = Server.createWebServer("-webAllowOthers", "-webPort", "8082");
                webServer.start();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                tcpServer = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9094");
                tcpServer.start();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
        //wait until the servers are created
        while (tcpServer == null || webServer == null) {
            Thread.sleep(40);
        }
    }

    public Connection getConnection() throws SQLException {
        String url = "jdbc:h2:" + tcpServer.getURL() + "/~/" + database;
        System.out.println("url: " + url);
        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }

    public void stopServer() {
        if (tcpServer != null) {
            System.out.println("stopping tcpServer");
            System.out.println("tcpServer: " + tcpServer.getStatus());
            tcpServer.stop();
            System.out.println("tcpServer: " + tcpServer.getStatus());
        }

        if (webServer != null) {
            System.out.println("stopping webServer");
            System.out.println("webServer: " + webServer.getStatus());
            webServer.stop();
            System.out.println("webServer: " + webServer.getStatus());
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                //ignored
            }
        }

    }
}
