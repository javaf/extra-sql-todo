// @wolfram77
package main;

// required modules
import org.db.*;
import java.sql.*;



public class Main {
    
    public static void main(String[] args) throws Exception {
        Sql sql = new Sql(Db.conn());
        sql.str("SELECT * FROM tab");
        ResultSet ans = sql.cmd().executeQuery();
        while(ans.next()) {
            System.out.println(ans.getString(1));
        }
    }
}
