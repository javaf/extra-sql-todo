// @wolfram77
package org.db;

// required modules
import java.sql.*;



public class Db {
    
    // constants
    static String driver = "oracle.jdbc.driver.OracleDriver";
    static String conn = "jdbc:oracle:thin:@localhost:1521/XE";
    static String user = "scott";
    static String pass = "tiger";
    
    
    // Conn ()
    // - get database connection
    public static Connection conn() throws SQLException {
        try { Class.forName(driver); }
        catch(Exception e) { throw new SQLException(e); }
        return DriverManager.getConnection(conn, user, pass);
    }
    
    
    // can add conneciton pooling for speed enhancement
}
