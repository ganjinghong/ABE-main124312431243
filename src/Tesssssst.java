import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Tesssssst {

//    public static Map<String, TreeNode> jsonStringToAccessTree(String accessTreeString) {
//        JSONObject jsonObject=new JSONObject(accessTreeString);
//        Map<String, TreeNode> map = new HashMap<>();
//        // 使用For-Each迭代entries，通过Map.entrySet遍历key和value
////        for (Map.Entry<String, TreeNode> entry : map.entrySet()) {
////            System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue().toString());
////        }
////        System.out.println(jsonObject);
//        jsonToTree(jsonObject, map);
//        return  map;
//    }
//
//    public static void jsonToTree(JSONObject obj, Map<String, TreeNode> map) {
//        String id = Integer.toString(obj.getInt("name"));
//
//
//        //判断当前json对象有没有孩子
//        if (obj.has("children") && obj.getJSONArray("children").length()>0 ) {
//            int len = obj.getJSONArray("children").length();
//            int[] children = new int[len];
//            for (int i =0; i<len; i++) {
//                JSONObject o = obj.getJSONArray("children").getJSONObject(i);
//
//                jsonToTree(o, map);
//                children[i] = o.getInt("name");
//
//            }
//            String gateString = obj.getString("value");
//
//            int t = Integer.parseInt(gateString.split(",")[0].substring(1));
//
//            int n = Integer.parseInt(gateString.split(",")[1].substring(0, gateString.split(",")[1].length()-1));
//
//            int[] gate = new int[]{t, n};
//            map.put(id, new TreeNode(gate, children));
//
//        }
//        else {
//            String att = obj.getString("value");
//            map.put(id, new TreeNode(att));
//        }
//
//    }
    //17正常 19不正常
    public static void main(String[] args) throws Exception {
//        String pairingParametersFileName = "a.properties";
////        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);
//        File messageFile = new File(pairingParametersFileName);
////        MultipartFile messagemultipartFile = getMultipartFileUtil.getMultipartFile(messageFile);
////        System.out.println(messageFile.getPath());
////        String temp = pairingParametersFileName.substring(0, pairingParametersFileName.lastIndexOf("\\"));
//
////        System.out.println(messageFile.getAbsolutePath());
////        System.out.println(messageFile.getAbsolutePath().lastIndexOf("\\"));
////        Path path = Paths.get("C:\\Users\\57802\\Desktop\\44.doc");
////        Files.delete(path);
////        File file=new File(pairingParametersFileName);
////        String accessTreeString= FileUtils.readFileToString(file,"UTF-8");
////        Map<String, TreeNode> accessTree = jsonStringToAccessTree(accessTreeString);
////        int childID = 14723343;
////        Element element = bp.getZr().newElement(childID);
////        System.out.println(element);
//        System.out.println(messageFile.exists());
        String[] userAttList = {"1", "2","3", "4"};
        if (userAttList == null) {
            throw new Exception("属性集不能为空");
        }else {
            for (int i = 0; i < userAttList.length; i++) {
                String tempString = userAttList[i];
                for (int j = 0 ; j < userAttList.length; j++) {
                    if (i != j){
                        if (tempString.equals(userAttList[j])){
                            throw new Exception("属性集合的元素不能重复");
                        }
                    }

                }
            }
            //空操作,代表属性集合不重复，且不为空
        }


    }
}
