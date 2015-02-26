// @wolfram77
package main;

// required modules
import java.sql.*;
import java.util.*;
import org.db.*;



public class Main {
    
    public static void main(String[] args) throws Exception {
        Sql sql = new Sql(Db.conn());
        sql.str("SELECT * FROM demo_users WHERE user_name=@user_name");
        Map<String, Object> fields = new HashMap<>();
        fields.put("user_name", "ADMIN");
        fields.put("user_id", 2);
        sql.putAll(fields);
        ResultSet ans = sql.cmd().executeQuery();
        while(ans.next()) {
            System.out.println(ans.getString("user_name"));
        }
    }
}
