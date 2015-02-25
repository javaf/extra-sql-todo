// @wolfram77
package com.db;

//required modules
import java.sql.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;
import org.data.*;



public class Sql extends HashMap<String, Object> {

    // data
    String str;
    Connection conn;
    PreparedStatement cmd;
    Map<String, List<Integer>> pos;


    // FindAndReplace (text, patternStr, replaceStr)
    // - find the positions of given pattern in a list-map and replace it with given string
    static Map<String, List<Integer>> posMap(StringBuilder text, String patternStr, String replaceStr) {
        Map<String, List<Integer>> ans = new HashMap<>();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher match = pattern.matcher(text.toString());
        for (int id=1, del=0; match.find(); id++) {
            int start = del+match.start(), end = del+match.end();
            Coll.addToListMap(ans, text.substring(start+1, end), id);
            text.replace(start, end, replaceStr);
            del += replaceStr.length() - (end-start);
        }
        return ans;
    }


    // Prepare (cmdStr)
    // - prepare sql command from given command string
    void prepare(String cmdStr) throws SQLException {
        StringBuilder cmdPrep = new StringBuilder(cmdStr);
        pos = posMap(cmdPrep, "(@)\\w+", "?");
        cmd = conn.prepareStatement(cmdPrep.toString());
    }


    // SetField (key, val)
    // - set a @named_parameter with a value (use "\\(null)" for null value) (ignores unavailable value)
    private void setField(String key, Object val) throws SQLException {
        List<Integer> ids = pos.get(key);
        if (ids == null) return;
        for (Integer id : ids) {
            if (val == null || val.equals("\\(null)")) cmd.setNull(id, Types.NULL);
            else if (val instanceof BigDecimal) cmd.setBigDecimal(id, (BigDecimal) val);
            else if (val instanceof java.util.Date) cmd.setTimestamp(id, new Timestamp(((java.util.Date)val).getTime()));
            else cmd.setObject(id, val);
        }
    }


    // ClearFields (key, val)
    // - clears all command fields that were set
    private void clearFields() throws SQLException {
        cmd.clearParameters();
    }


    // Sql (conn)
    // - set sql connection for command
    public Sql(Connection conn) {
        this.conn = conn;
    }


    // Str ()
    // - get sql string
    public String str() {
        return str;
    }


    // Str (str)
    // - set sql string and prepare
    public Sql str(String str) throws SQLException {
        if (str.equals(this.str)) return this;
        this.str = str;
        prepare(str);
        return this;
    }


    // Cmd ()
    // - get the prepared statement (to execute)
    public PreparedStatement cmd() {
        return cmd;
    }


    // Put (key, val)
    // - set @named_parameter with given value
    @Override
    public Sql put(String key, Object val) {
        System.out.println("put("+key+", "+val+")");
        super.put(key, val);
        if (cmd == null) return this;
        try { setField(key, val); }
        catch(Exception e) {}
        return this;
    }


    // Clear ()
    // - clear all fields
    @Override
    public void clear() {
        super.clear();
        if (cmd != null) {
            try { clearFields(); }
            catch (Exception e) {}
        }
    }


    // SelectStr (table, select fields, where fields)
    // - get select command for given table
    public static String selectStr(String table, Collection<String> selectFields, Collection<String> whereFields) {
        String selectPart = Coll.toString(selectFields, "\\(item)", ",");
        String wherePart = Coll.toString(whereFields, "\\(item)=@\\(item)", " AND ");
        return "SELECT " + selectPart + " FROM " + table + " WHERE " + wherePart;
    }


    // UpdateStr (table, update fields, where fields)
    // - get update command for given table
    public static String updateStr(String table, Collection<String> updateFields, Collection<String> whereFields) {
        String setPart = Coll.toString(updateFields, "\\(item)=@\\(item)", ",");
        String wherePart = Coll.toString(whereFields, "\\(item)=@\\(item)", " AND ");
        return "UPDATE " + table + " SET " + setPart + " WHERE " + wherePart;
    }


    // InsertStr (table, fields)
    // - get insert command for given table
    public static String insertStr(String table, Collection<String> fields) {
        String intoPart = Coll.toString(fields, "\\(item)", ",");
        String valuesPart = Coll.toString(fields, "@\\(item)", ",");
        return "INSERT INTO " + table + "(" + intoPart + ") VALUES (" + valuesPart + ")";
    }
}
