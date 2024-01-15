package com.abe.util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class jsonStringToAccessTreeUtil {
    public static Map<String, TreeNode> jsonStringToAccessTree(String accessTreeString) {
        JSONObject jsonObject=new JSONObject(accessTreeString);
        Map<String, TreeNode> map = new HashMap<>();
        jsonToTreeUtil.jsonToTree(jsonObject, map);
        return  map;
    }

    public static void main(String[] args) throws IOException {

//        String FileName = "C:\\Users\\57802\\Desktop\\测试实验文件夹\\config (4).json";
//        File policfile = new File(FileName);
        String accessTreeString= "{\n" +
                "  \"name\": 0,\n" +
                "  \"value\": \"(4,4)\",\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"name\": 3943,\n" +
                "      \"value\": \"1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": 3866,\n" +
                "      \"value\": \"2\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": 4880,\n" +
                "      \"value\": \"3\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": 4364,\n" +
                "      \"value\": \"4\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
                //FileUtils.readFileToString(policfile,"UTF-8");
        Map<String, TreeNode> accessTree = jsonStringToAccessTreeUtil.jsonStringToAccessTree(accessTreeString);
    }
}
