// @wolfram77
package com.db;

//required modules
import java.sql.*;
import java.math.*;
import java.util.*;
import java.util.regex.*;
import org.data.*;



public class Sql {

    // data
    private Connection conn;
    private String cmdStr;
    private PreparedStatement cmd;
    private Map<String, Object> fieldVal;
    public Map<String, List<Integer>> fieldId;


    // Find And Replace (text, patternStr, replaceStr)
    // - find the positions of given pattern in a list-map and replace it with given string
    public static Map<String, List<Integer>> findAndReplace(StringBuilder text, String patternStr, String replaceStr) {
        Map<String, List<Integer>> ans = new HashMap<String, List<Integer>>();
        Pattern pattern = Pattern.compile(patternStr);
        Matcher match = pattern.matcher(text.toString());
        for (int id = 1, del = 0; match.find(); id++) {
            int start = del + match.start(), end = del + match.end();
            Coll.addToListMap(ans, text.substring(start + 1, end), id);
            text.replace(start, end, replaceStr);
            del += replaceStr.length() - (end - start);
        }
        return ans;
    }


    // Prep Cmd (cmdStr)
    // - prepare sql command from given command string
    private void prepCmd(String cmdStr) throws SQLException {
        StringBuilder cmdPrep = new StringBuilder(cmdStr);
        fieldId = findAndReplace(cmdPrep, "(@)\\w+", "?");
        cmd = conn.prepareStatement(cmdPrep.toString());
    }


    // Cmd Field (key, val)
    // - set a @named_parameter with a value (use "\\(null)" for null value) (ignores unavailable value)
    private void cmdField(String key, Object val) throws SQLException {
        List<Integer> ids = fieldId.get(key);
        if (ids == null) {
            return;
        }
        for (Integer id : ids) {
            if (val == null || val.equals("\\(null)")) {
                cmd.setNull(id, Types.NULL);
            } else if (val instanceof BigDecimal) {
                cmd.setBigDecimal(id, (BigDecimal) val);
            } else if (val instanceof java.util.Date) {
                cmd.setDate(id, new java.sql.Date(((java.util.Date) val).getTime()));
            } else {
                cmd.setObject(id, val);
            }
        }
    }


    // Cmd Clear Fields (key, val)
    // - clears all command fields that were set
    private void cmdClearFields() throws SQLException {
        cmd.clearParameters();
    }


    // Constructor (conn)
    // - set sql connection for command
    public Sql(Connection conn) {
        this.conn = conn;
        fieldVal = new HashMap<String, Object>();
    }


    // Cmd Str ()
    // - get the set command string
    public String cmdStr() {
        return cmdStr;
    }


    // Cmd (cmdStr)
    // - prepare command from command string
    public Sql cmd(String cmdStr) throws SQLException {
        if (cmdStr.equals(this.cmdStr)) {
            return this;
        }
        this.cmdStr = cmdStr;
        prepCmd(cmdStr);
        return this;
    }


    // Cmd ()
    // - get the prepared statement (to execute)
    public PreparedStatement cmd() {
        return cmd;
    }


    // Set (key, val)
    // - set @named_parameter with given value
    public Sql set(String key, Object val) throws SQLException {
        fieldVal.put(key, val);
        if (cmd != null) {
            cmdField(key, val);
        }
        return this;
    }


    // Set (map)
    // - set named parameters with given values
    public Sql set(Map<String, Object> map) throws SQLException {
        if (map == null) {
            return this;
        }
        for (String key : map.keySet()) {
            set(key, map.get(key));
        }
        return this;
    }


    // Get (key)
    // - get field value from field name
    public Object get(String key) {
        return fieldVal.get(key);
    }


    // Get ()
    // - get all fields
    public Map<String, Object> get() {
        return fieldVal;
    }


    // Clear ()
    // - clear all fields
    public Sql clear() throws SQLException {
        fieldVal.clear();
        if (cmd != null) {
            cmdClearFields();
        }
        return this;
    }


    // Select Str (table, select fields, where fields)
    // - get select command for given table and set fields
    public static String selectStr(String table, Collection<String> selectFields, Collection<String> whereFields) {
        String selectPart = Coll.toString(selectFields, "\\(item)", ",");
        String wherePart = Coll.toString(whereFields, "\\(item)=@\\(item)", " AND ");
        return "SELECT " + selectPart + " FROM " + table + " WHERE " + wherePart;
    }


    // Update Str (table, update fields, where fields)
    // - get select command for given table and set fields
    public static String updateStr(String table, Collection<String> updateFields, Collection<String> whereFields) {
        String setPart = Coll.toString(updateFields, "\\(item)=@\\(item)", ",");
        String wherePart = Coll.toString(whereFields, "\\(item)=@\\(item)", " AND ");
        return "UPDATE " + table + " SET " + setPart + " WHERE " + wherePart;
    }


    // Insert Str (table)
    // - get insert command for given table and set fields
    public static String insertStr(String table, Collection<String> fields) {
        String intoPart = Coll.toString(fields, "\\(item)", ",");
        String valuesPart = Coll.toString(fields, "@\\(item)", ",");
        return "INSERT INTO " + table + "(" + intoPart + ") VALUES (" + valuesPart + ")";
    }
}
