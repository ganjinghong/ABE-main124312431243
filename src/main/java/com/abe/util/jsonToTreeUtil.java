package com.abe.util;

import org.json.JSONObject;

import java.util.Map;

public class jsonToTreeUtil {
    public static void jsonToTree(JSONObject obj, Map<String, TreeNode> map) {
        String id = Integer.toString(obj.getInt("name"));
        if (obj.has("children") && obj.getJSONArray("children").length()>0 ) {
            int len = obj.getJSONArray("children").length();
            int[] children = new int[len];
            for (int i =0; i<len; i++) {
                JSONObject o = obj.getJSONArray("children").getJSONObject(i);
                jsonToTree(o, map);
                children[i] = o.getInt("name");
            }
            String gateString = obj.getString("value");
            int t = Integer.parseInt(gateString.split(",")[0].substring(1));
            int n = Integer.parseInt(gateString.split(",")[1].substring(0, gateString.split(",")[1].length()-1));
            int[] gate = new int[]{t, n};
            map.put(id, new TreeNode(gate, children));
        }
        else {
            String att = obj.getString("value");
            map.put(id, new TreeNode(att));
        }
        System.out.println(map);
    }
}
