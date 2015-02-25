// @wolfram77
package org.data;

// required modules
import java.util.*;



public class Coll {
    
    // Map (arr)
    // - converts an array of key, value pairs to map
    public static Map map(Object[] arr) {
        Map map = new HashMap();
        for(int i=0; i<arr.length; i+=2)
            map.put(arr[i], arr[i+1]);
        return map;
    }

    
    // List (arr)
    // - converts an array of values to list
    public static List list(Object[] arr) {
        return Arrays.asList(arr);
    }


    // AddToListMap (map, key, val)
    // - add a value to a list-map (values with similar keys exist)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addToListMap(Map map, Object key, Object val) {
        if (map.get(key) == null) map.put(key, new ArrayList());
        ((List)map.get(key)).add(val);
    }


    // ToString (coll, format, separator)
    // - converts a collection to a string format (use \\(item) for each item)
    @SuppressWarnings("rawtypes")
    public static String toString(Collection coll, String format, String separator) {
        StringBuilder ans = new StringBuilder();
        for (Object item : coll) {
            ans.append(format.replaceAll("\\\\\\(item\\)", item.toString())).append(separator);
            ans.append(separator);
        }
        if (ans.length() > separator.length())
            ans.delete(ans.length() - separator.length(), ans.length());
        return ans.toString();
    }
    
    
    // add some more basic functionality here
}
