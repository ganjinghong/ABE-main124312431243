package com.abe;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.abe.util.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class cpabeUtil {

    public static void setup(String pairingParametersFileName, String pkFileName, String mskFileName) throws Exception {


        //判断入参是否存在，首先判断pairingParametersFileName，这个文件得真真试试存在的
        //得判断是不是空，不是空的情况下,得判断存不存在，并且符不符合指定的格式类型
        if (pairingParametersFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        }else {
            //判断文件存不存在
            File pairingParametersFileName_File = new File(pairingParametersFileName);
            if (!pairingParametersFileName_File.exists()) {
                throw new Exception("文件或目录不存在");
            } else {
                if (pairingParametersFileName.substring(pairingParametersFileName.lastIndexOf(".") + 1).equals("properties" ) || pairingParametersFileName.substring(pairingParametersFileName.lastIndexOf(".") + 1).equals("txt")) {
                    //空操作,代表该文件路径合法，但是里面的内容合不合法就不能判断了
                }else {
                    throw new Exception("请输入文件后缀为.properties或.txt的文件路径");
                }
            }
        }

        //接下来判断pkFileName，这个文件得指定类型.properties或.txt，且不能事先建立好，只能传入一个可以创建该文件的路径
        if (pkFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        }else {
            //判断文件存不存在
            File pkFileName_File = new File(pkFileName);
            if (pkFileName_File.exists()) {
                throw new Exception("所填入的文件或目录已经存在了或者不存在，为了保证不影响原来已经存在的公钥文件数据，请:填入正确的路径并且保证目标文件不能事先建立");
            } else {
                if (pkFileName.substring(pkFileName.lastIndexOf(".") + 1).equals("properties") || pkFileName.substring(pkFileName.lastIndexOf(".") + 1).equals("txt")) {
                    //空操作,代表该文件路径合法，但是里面的内容合不合法就不能判断了
                }else {
                    throw new Exception("请输入文件后缀为.properties或.txt的文件路径");
                }
            }
        }

        //接下来判断mskFileName，这个文件得指定类型.properties或.txt，且不能实现建立好，只能传入一个可以创建该文件的路径
        if (mskFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        }else {
            //判断文件存不存在
            File mskFileName_File = new File(mskFileName);
            if (mskFileName_File.exists()) {
                throw new Exception("所填入的文件或目录已经存在了或者不存在，为了保证不影响原来已经存在的主私钥文件数据，请:填入正确的路径且保证文件不能事先存在");
            } else {
                if (mskFileName.substring(mskFileName.lastIndexOf(".") + 1).equals("properties") || mskFileName.substring(mskFileName.lastIndexOf(".") + 1).equals("txt")) {
                    //
                }else {
                    throw new Exception("请输入文件后缀为.properties或.txt的文件路径");
                }
            }
        }



        Pairing bp = PairingFactory.getPairing(pairingParametersFileName);
        Element g = bp.getG1().newRandomElement().getImmutable();
        Element alpha = bp.getZr().newRandomElement().getImmutable();
        Element beta = bp.getZr().newRandomElement().getImmutable();

        Element g_alpha = g.powZn(alpha).getImmutable();
        Element g_beta = g.powZn(beta).getImmutable();
        Element egg_alpha = bp.pairing(g,g).powZn(alpha).getImmutable();

        Properties mskProp = new Properties();
        mskProp.setProperty("g_alpha", Base64.getEncoder().withoutPadding().encodeToString(g_alpha.toBytes()));

        Properties pkProp = new Properties();
        pkProp.setProperty("g", Base64.getEncoder().withoutPadding().encodeToString(g.toBytes()));
        pkProp.setProperty("g_beta", Base64.getEncoder().withoutPadding().encodeToString(g_beta.toBytes()));
        pkProp.setProperty("ppFile", pairingParametersFileName);
        pkProp.setProperty("egg_alpha", Base64.getEncoder().withoutPadding().encodeToString(egg_alpha.toBytes()));



        storePropToFileUtil.storePropToFile(mskProp, mskFileName);
        storePropToFileUtil.storePropToFile(pkProp, pkFileName);


    }

    //产生的String类型的密钥是一个位 一个字符都不能变的。
    public static String keygen(String[] userAttList, String pkFileName, String mskFileName) throws Exception {

//        判断属性集为不为空，
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

//        接下来判断pkFileName，这个文件得指定类型.properties或.txt，这个文件得已经存在
        if (pkFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        }else {
            //判断文件存不存在,得保证文件实现存在，因为这个算法是为某一个系统的用户配置密钥
            File pkFileName_File = new File(pkFileName);
            if (pkFileName_File.exists()) {
                if (pkFileName.substring(pkFileName.lastIndexOf(".") + 1).equals("properties") || pkFileName.substring(pkFileName.lastIndexOf(".") + 1).equals("txt")) {
                    //
                }else {
                    throw new Exception("请输入已有的公钥文件路径，其后缀应为.properties或.txt的文件路径");
                }
            } else {
                throw new Exception("请填入一个已经存在文件的文件路径!");
            }
        }

        //接下来判断mskFileName，这个文件得指定类型.properties或.txt，且得实现建立好
        if (mskFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        }else {
            //判断文件存不存在
            File mskFileName_File = new File(mskFileName);
            if (mskFileName_File.exists()) {
                if (mskFileName.substring(mskFileName.lastIndexOf(".") + 1).equals("properties") || mskFileName.substring(mskFileName.lastIndexOf(".") + 1).equals("txt")) {
                    //
                }else {
                    throw new Exception("请输入已有的系统公钥文件路径，其后缀应为.properties或.txt的文件路径");
                }
            } else {
                throw new Exception("请填入一个已经存在文件的文件路径！");

            }
        }

        Properties pkProp = loadPropFromFileUtil.loadPropFromFile(pkFileName);
        Pairing bp = PairingFactory.getPairing(pkProp.getProperty("ppFile"));


        String gString = pkProp.getProperty("g");
        Element g = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(gString)).getImmutable();
        String g_betaString = pkProp.getProperty("g_beta");
        Element g_beta = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(g_betaString)).getImmutable();

        Properties mskProp = loadPropFromFileUtil.loadPropFromFile(mskFileName);
        String g_alphaString = mskProp.getProperty("g_alpha");
        Element g_alpha = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(g_alphaString)).getImmutable();

//        Properties skProp = new Properties();

        Element t = bp.getZr().newRandomElement().getImmutable();
        Element D = g_alpha.mul(g_beta.powZn(t)).getImmutable();
        Element D0 = g.powZn(t);
//        skProp.setProperty("userAttList", Arrays.toString(userAttList));
//        skProp.setProperty("D", Base64.getEncoder().withoutPadding().encodeToString(D.toBytes()));
//        skProp.setProperty("D0", Base64.getEncoder().withoutPadding().encodeToString(D0.toBytes()));


//        String DString = new String(D.toBytes());
        String[] userSk = new String[3 + userAttList.length];

        userSk[0] = "userAttList=" + Arrays.toString(userAttList);
        userSk[1] = "D=" + Base64.getEncoder().withoutPadding().encodeToString(D.toBytes());
        userSk[2] = "D0=" + Base64.getEncoder().withoutPadding().encodeToString(D0.toBytes());

        int i = 3;

        for (String att : userAttList) {
            byte[] idHash = HashUtil.sha1(att);
            Element H = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();
            Element Datt = H.powZn(t).getImmutable();
//            skProp.setProperty("D-"+att, Base64.getEncoder().withoutPadding().encodeToString(Datt.toBytes()));
            userSk[i] = "D-" + att + "=" + Base64.getEncoder().withoutPadding().encodeToString(Datt.toBytes());
            if (i != userSk.length - 1) {
                i++;
            }
        }


        String total = "userSk=userStaticSk=";
        for (int j = 0; j < userSk.length; j++) {
            total = total.concat(userSk[j]);
        }

//        skProp.setProperty("total=", total);
//        System.out.println(total);

//        storePropToFileUtil.storePropToFile(skProp, skFileName);


//        String str2 = total.substring(total.indexOf("D-1=") + 4, total.indexOf("D-2="));
//        System.out.println(str2);

        return total;
    }


    public static String dynamicKeygen(String[] userAttList, String pkFileName, String mskFileName, long datePeriods) throws Exception {

//        判断属性集为不为空，
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

//        接下来判断pkFileName，这个文件得指定类型.properties或.txt，这个文件得已经存在
        if (pkFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        }else {
            //判断文件存不存在,得保证文件实现存在，因为这个算法是为某一个系统的用户配置密钥
            File pkFileName_File = new File(pkFileName);
            if (pkFileName_File.exists()) {
                if (pkFileName.substring(pkFileName.lastIndexOf(".") + 1).equals("properties") || pkFileName.substring(pkFileName.lastIndexOf(".") + 1).equals("txt")) {
                    //
                }else {
                    throw new Exception("请输入已有的公钥文件路径，其后缀应为.properties或.txt的文件路径");
                }
            } else {
                throw new Exception("请填入一个已经存在文件的文件路径!");
            }
        }

        //接下来判断mskFileName，这个文件得指定类型.properties或.txt，且得实现建立好
        if (mskFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        }else {
            //判断文件存不存在
            File mskFileName_File = new File(mskFileName);
            if (mskFileName_File.exists()) {
                if (mskFileName.substring(mskFileName.lastIndexOf(".") + 1).equals("properties") || mskFileName.substring(mskFileName.lastIndexOf(".") + 1).equals("txt")) {
                    //
                }else {
                    throw new Exception("请输入已有的系统公钥文件路径，其后缀应为.properties或.txt的文件路径");
                }
            } else {
                throw new Exception("请填入一个已经存在文件的文件路径！");

            }
        }

        Properties pkProp = loadPropFromFileUtil.loadPropFromFile(pkFileName);
        Pairing bp = PairingFactory.getPairing(pkProp.getProperty("ppFile"));


        String gString = pkProp.getProperty("g");
        Element g = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(gString)).getImmutable();
        String g_betaString = pkProp.getProperty("g_beta");
        Element g_beta = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(g_betaString)).getImmutable();

        Properties mskProp = loadPropFromFileUtil.loadPropFromFile(mskFileName);
        String g_alphaString = mskProp.getProperty("g_alpha");
        Element g_alpha = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(g_alphaString)).getImmutable();

//        Properties skProp = new Properties();

        Element t = bp.getZr().newRandomElement().getImmutable();
        Element D = g_alpha.mul(g_beta.powZn(t)).getImmutable();
        Element D0 = g.powZn(t);
//        skProp.setProperty("userAttList", Arrays.toString(userAttList));
//        skProp.setProperty("D", Base64.getEncoder().withoutPadding().encodeToString(D.toBytes()));
//        skProp.setProperty("D0", Base64.getEncoder().withoutPadding().encodeToString(D0.toBytes()));


//        String DString = new String(D.toBytes());
        String[] userSk = new String[3 + userAttList.length];

        userSk[0] = "userAttList=" + Arrays.toString(userAttList);
        userSk[1] = "D=" + Base64.getEncoder().withoutPadding().encodeToString(D.toBytes());
        userSk[2] = "D0=" + Base64.getEncoder().withoutPadding().encodeToString(D0.toBytes());

        int i = 3;

        for (String att : userAttList) {
            byte[] idHash = HashUtil.sha1(att);
            Element H = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();
            Element Datt = H.powZn(t).getImmutable();
//            skProp.setProperty("D-"+att, Base64.getEncoder().withoutPadding().encodeToString(Datt.toBytes()));
            userSk[i] = "D-" + att + "=" + Base64.getEncoder().withoutPadding().encodeToString(Datt.toBytes());
            if (i != userSk.length - 1) {
                i++;
            }
        }


        String total = "userSk=userDynamicSk=";
        String timeString = "userKeyTime=";
        long dynamicTime = System.currentTimeMillis() + datePeriods;
        String string = String.valueOf(dynamicTime);
        StringBuilder stringBuilder = new StringBuilder(string);

        for (int c = 0; c < string.length(); c++) {
            stringBuilder.setCharAt(c, (char) (string.charAt(c) + 26));
        }

        timeString = timeString + stringBuilder.toString();


        total = total.concat(timeString);
        for (int j = 0; j < userSk.length; j++) {
            total = total.concat(userSk[j]);
        }



//        String userKey = total + timeString;
//        total = total.concat();

        return total;
    }

    public static OutputStream encrypt(MultipartFile messageMultipartFile, String jsonString, String pkFileName) throws Exception {
        //判断入参messageMultipartFile
        if (messageMultipartFile == null) {
            throw new Exception("传入的文件不能为空");
        }

        //判断jsonString
        if (jsonString == null) {
            throw new Exception("访问策略不能为空");
        }//保证第一个节点的name值为0,在加密算法中已经判断

        //        接下来判断pkFileName，这个文件得指定类型.properties或.txt，这个文件得已经存在
        if (pkFileName == null) {
            //判断为不为空
            throw new Exception("文件路径不能为空");
        } else {
            //判断文件存不存在,得保证文件实现存在，因为这个算法是为某一个系统的用户配置密钥
            File pkFileName_File = new File(pkFileName);
            if (pkFileName_File.exists()) {
                if ("properties".equals(pkFileName.substring(pkFileName.lastIndexOf(".") + 1)) || "txt".equals(pkFileName.substring(pkFileName.lastIndexOf(".") + 1))) {
                    //
                } else {
                    throw new Exception("请输入已有的公钥文件路径，其后缀应为.properties或.txt的文件路径");
                }
            } else {
                throw new Exception("请填入一个系统公钥文件路径，其后缀为.properties或.txt");
            }
        }
//        File policfile = new File(policFileName);
//        policMultipartFile.transferTo(policfile);
        //FileUtils.readFileToString(policfile,"UTF-8");
        Properties ctProp = calculateProperty(messageMultipartFile, jsonString, pkFileName);

        //删除临时文件, 在这个算法中，messageFileName和policyFileName是临时的路径
        /*Path messagePath = Paths.get(messageFileName);
        Files.delete(messagePath);*/

        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            ctProp.store(outputStream, "ctProperties");
            System.out.println("Properties saved to output stream.");
        } catch (IOException e) {
            System.out.println("转换输出流失败");
        }

        return outputStream;
    }

    public static OutputStream decrypt(MultipartFile ctMultipartFile, String userSk) throws Exception {

        //判断密钥是动态密钥还是静态密钥，如果是动态密钥则要监测时间是否没超时；
        if (!userSk.contains("userStaticSk=")) {
            //动态密钥
            String userTimeString = userSk.substring(userSk.indexOf("userDynamicSk=userKeyTime=") + 26, userSk.indexOf("userAttList="));
            System.out.println(userTimeString);
            StringBuilder userTimeStringBuilder = new StringBuilder(userTimeString);

            for (int i = 0; i < userTimeString.length(); i++) {
                userTimeStringBuilder.setCharAt(i, (char) (userTimeString.charAt(i) - 26));
            }
            System.out.println();

            long time1 = Long.parseLong(userTimeStringBuilder.toString());
            System.out.println(time1);
            if (time1 > System.currentTimeMillis()) {
                //说明该动态密钥还没超时
                //空操作
            } else {
                //说明该动态密钥超时了
                return null;
            }
        } else {
            //静态密钥就正常执行
            //空操作
        }


        //判断入参ctMultipartFile
        if (ctMultipartFile == null) {
            throw new Exception("传入的文件不能为空");
        }
        //判断入参userSk
        if (userSk == null) {
            throw new Exception("传入的密钥字符串不能为空");
        }

        //判断messageFileName
//        if (messageFileName == null) {
//            throw new Exception("需要临时保存密文的路径不能为空");
//        } else {
//            File messageFileNameFile = new File(messageFileName);
//            if (messageFileNameFile.exists()) {
//                //如果文件存在，那么可能是具体文件，也可能是目录，目录是不能传的。
//                if (messageFileName.lastIndexOf(".") == -1) {
//                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
//                } else {
//                    throw new Exception("此处已经存在文件，不能作为临时文件路径");
//                }
//            } else {
//                if (messageFileName.lastIndexOf(".") == -1) {
//                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
//                } else {
////                    String tempFileName = ctFileName.substring(0, ctFileName.lastIndexOf("\\"));
//                    File tempFile = new File(messageFileName);
//                    String tempFileName = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().lastIndexOf("/"));
//                    File tempFile_two = new File(tempFileName);
//                    if (tempFile_two.exists()) {
//                        //可以执行算法
//                    } else {
//                        throw new Exception("目录不存在，不能作为临时文件路径");
//                    }
//                }
//            }
//
//        }


        byte[] fileBytes = ctMultipartFile.getBytes();// 将 MultipartFile 内容读取为字节数组
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);// 创建 ByteArrayInputStream 对象
        Properties ctProp = new Properties();// 创建 Properties 对象
        ctProp.load(inputStream);// 加载 Properties 对象

        Pairing bp = PairingFactory.getPairing(ctProp.getProperty("ppFile"));

//        Properties skProp = loadPropFromFileUtil.loadPropFromFile(skFileName);
        String userAttListString = userSk.substring(userSk.indexOf("userAttList=") + 12, userSk.indexOf("D="));
        //恢复用户属性列表String[]类型：先将首尾的方括号去除，然后按","分割
        String[] userAttList = userAttListString.substring(1, userAttListString.length() - 1).split(", ");


        String CString = ctProp.getProperty("C");
        Element C = bp.getGT().newElementFromBytes(Base64.getDecoder().decode(CString)).getImmutable();
        String C0String = ctProp.getProperty("C0");
        Element C0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C0String)).getImmutable();
//
//        String DString = skProp.getProperty("D");
//        Element D = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DString)).getImmutable();
//        String D0String = skProp.getProperty("D0");
//        Element D0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(D0String)).getImmutable();
        String DString = userSk.substring(userSk.indexOf("D=") + 2, userSk.indexOf("D0="));
        Element D = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DString)).getImmutable();
        String D0String = userSk.substring(userSk.indexOf("D0=") + 3, userSk.indexOf("D-"));
        Element D0 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(D0String)).getImmutable();


//
        // 从密文中获取访问策略字符串
        String accessTreeString = ctProp.getProperty("Policy");
        // 从访问策略字符串构建访问树对象
        Map<String, TreeNode> accessTree = jsonStringToAccessTreeUtil.jsonStringToAccessTree(accessTreeString);
//
//
//
        for (String key : accessTree.keySet()) {
            if (accessTree.get(key).isLeaf()) {
                // 如果叶子节点的属性值属于属性列表，则将属性对应的密文组件和秘钥组件配对的结果作为秘密值
                if (Arrays.asList(userAttList).contains(accessTree.get(key).att)) {
                    String C1tring = ctProp.getProperty("C1-" + accessTree.get(key).att);
                    Element C1 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C1tring)).getImmutable();
                    String C2tring = ctProp.getProperty("C2-" + accessTree.get(key).att);
                    Element C2 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C2tring)).getImmutable();


                    if (userSk.indexOf("D-" + accessTree.get(key).att) != userSk.lastIndexOf("D-")) {
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length(), userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length() + 171);
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1, D0).mul(bp.pairing(C2, Datt)).getImmutable();
                        System.out.println("1===========" + DattString);
                    } else {
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length());
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1, D0).mul(bp.pairing(C2, Datt)).getImmutable();
                        System.out.println("last===========" + DattString);
                    }

                }
            }
        }


        Element symmetrickey;
        // 进行秘密恢复
        boolean treeOK = nodeRecoverUtil.nodeRecover(accessTree, "0", userAttList, bp);

        if (treeOK) {
            Element egg_alphas = bp.pairing(C0, D).div(accessTree.get("0").secretShare);
            symmetrickey = C.div(egg_alphas);
            String symmetric_ciphertext = ctProp.getProperty("symmetric_ciphertext");

            //修改
            String symmetrickey_String = Base64.getEncoder().withoutPadding().encodeToString(symmetrickey.toBytes());
//            String decryptStr = sm4.decryptStr(message_sm4, CharsetUtil.CHARSET_UTF_8);
            String symmetrickey_String_quit = symmetrickey_String.substring(0, 16);
            SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());
            String decryptStr = sm4.decryptStr(symmetric_ciphertext, CharsetUtil.CHARSET_UTF_8);
            //messageFileName这个也是临时的文件，删除它之前必须得产生明文流以便能传回
            return binaryUtil.binaryStringRecover(decryptStr);

        } else {
            return null;
        }

    }

    private static Properties calculateProperty(MultipartFile messageMultipartFile, String jsonString, String pkFileName) throws Exception {
        Map<String, TreeNode> accessTree = jsonStringToAccessTreeUtil.jsonStringToAccessTree(jsonString);


        Properties pkProp = loadPropFromFileUtil.loadPropFromFile(pkFileName);
        Pairing bp = PairingFactory.getPairing(pkProp.getProperty("ppFile"));

        String gString = pkProp.getProperty("g");
        Element g = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(gString)).getImmutable();
        String g_betaString = pkProp.getProperty("g_beta");
        Element g_beta = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(g_betaString)).getImmutable();
        String egg_alphaString = pkProp.getProperty("egg_alpha");
        Element egg_alpha = bp.getGT().newElementFromBytes(Base64.getDecoder().decode(egg_alphaString)).getImmutable();


        Element symmetrickey = bp.getGT().newRandomElement().getImmutable();
        String symmetrickey_String = Base64.getEncoder().withoutPadding().encodeToString(symmetrickey.toBytes());
        String symmetrickey_String_quit = symmetrickey_String.substring(0, 16);

        SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());

        //将传入的MultipartFile 转 File并在本地里临时保存
//        File messageFile = new File(messageFileName);
//      messageMultipartFile.transferTo(messageFile);


        //传入某格式类型的文件的二进制字符串进行加密
//        String message = binaryUtil.getBinaryString(messageFile.toString());
        String message = binaryUtil.getBinaryString(messageMultipartFile);
//        System.out.println(message);
        String symmetric_ciphertext = sm4.encryptHex(message, CharsetUtil.CHARSET_UTF_8);
//        byte[] symmetric_ciphertext_byte = symmetric_ciphertext.getBytes();


        Properties ctProp = new Properties();
        //计算密文组件 C=M e(g,g)^(alpha s)
        Element s = bp.getZr().newRandomElement().getImmutable();
        Element C = symmetrickey.duplicate().mul(egg_alpha.powZn(s)).getImmutable();
        Element C0 = g.powZn(s).getImmutable();

        ctProp.setProperty("symmetric_ciphertext", symmetric_ciphertext);

        ctProp.setProperty("C", Base64.getEncoder().withoutPadding().encodeToString(C.toBytes()));
        ctProp.setProperty("C0", Base64.getEncoder().withoutPadding().encodeToString(C0.toBytes()));


        //先设置根节点要共享的秘密值
        if (accessTree.get("0") == null) {
            throw new Exception("根节点的name值必须为0");
        }
        accessTree.get("0").secretShare = s;
        //进行共享，使得每个叶子节点获得响应的秘密分片
        nodeShareUtil.nodeShare(accessTree, "0", bp);

        for (String key : accessTree.keySet()) {
            TreeNode node = accessTree.get(key);
            if (node.isLeaf()) {
                Element r = bp.getZr().newRandomElement().getImmutable();

                byte[] idHash = HashUtil.sha1(node.att);
                Element Hi = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();

                Element C1 = g_beta.powZn(node.secretShare).mul(Hi.powZn(r.negate()));
                Element C2 = g.powZn(r);

                ctProp.setProperty("C1-" + node.att, Base64.getEncoder().withoutPadding().encodeToString(C1.toBytes()));
                ctProp.setProperty("C2-" + node.att, Base64.getEncoder().withoutPadding().encodeToString(C2.toBytes()));
            }
        }
        // 将策略字符串保存在密文当中
        ctProp.setProperty("Policy", jsonString);
        // 将所用的公共参数文件写入密文中，使得解密的时候不需要公钥，只需要密文和私钥
        ctProp.setProperty("ppFile", pkProp.getProperty("ppFile"));
        return ctProp;
    }

}
