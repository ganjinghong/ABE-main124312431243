import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class cpabe_NewUtil {
    // 第二种方式

    //注意pairingParametersFileName在以后的每个阶段都要传进去，因为要初始化群和域的环境,除了初始化阶段，其他阶段都没有传入这个参数是因为这个
    //pairingParametersFileName存入了pkFileName这个properties文件里面，之后的阶段都是在这里取，这里演示的是相对路径，可以弄一个绝对路径，看个人。
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
        System.out.println(Arrays.toString(userAttList));
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

    //MultipartFile policMultipartFile, String policFileName
    public static OutputStream encrypt(MultipartFile messageMultipartFile, String messageFileName, String jsonString, String pkFileName, String ctFileName) throws Exception {
        //判断入参messageMultipartFile
        if (messageMultipartFile == null) {
            throw new Exception("传入的文件不能为空");
        }

        //判断入参messageFileName
        if (messageFileName == null) {
            throw new Exception("需要临时保存明文的路径不能为空");
        }else {
            File messageFileNameFile = new File(messageFileName);
            if (messageFileNameFile.exists()){
                //如果文件存在，那么可能是具体文件，也可能是目录，目录是不能传的。
                if (messageFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，需要传入一个临时存储文件的具体路径");
                } else {
                    throw new Exception("此处已经存在文件，不能作为临时存放文件的路径");
                }
            }else {
                if (messageFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                }else {
                    File tempFile = new File(messageFileName);
                    String tempFileName = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().lastIndexOf("\\"));
                    File tempFile_two = new File(tempFileName);
                    if (tempFile_two.exists()){
                        //可以执行算法
                    }else {
                        throw new Exception("地址不存在，不能作为临时文件路径");
                    }
                }
            }

        }

        //判断jsonString
        if (jsonString == null) {
            throw new Exception("访问策略不能为空");
        }//保证第一个节点的name值为0,在加密算法中已经判断

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
                throw new Exception("请填入一个系统公钥文件路径，其后缀为.properties或.txt");
            }
        }

        //判断ctFileName
        if (ctFileName == null) {
            throw new Exception("需要临时保存密文的路径不能为空");
        }else {
            File ctFileNameFile = new File(ctFileName);
            if (ctFileNameFile.exists()){
                //如果文件存在，那么可能是具体文件，也可能是目录，目录是不能传的。
                if (ctFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                } else {
                    throw new Exception("此处已经存在文件，不能作为临时文件路径");
                }
            }else {
                if (ctFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                }else {
//                    String tempFileName = ctFileName.substring(0, ctFileName.lastIndexOf("\\"));
                    File tempFile = new File(ctFileName);
                    String tempFileName = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().lastIndexOf("\\"));
                    File tempFile_two = new File(tempFileName);
                    if (tempFile_two.exists()){
                        //可以执行算法
                    }else {
                        throw new Exception("目录不存在，不能作为临时文件路径");
                    }
                }
            }

        }


//        File policfile = new File(policFileName);
//        policMultipartFile.transferTo(policfile);
        String accessTreeString= jsonString;
                //FileUtils.readFileToString(policfile,"UTF-8");
        Map<String, TreeNode> accessTree = jsonStringToAccessTreeUtil.jsonStringToAccessTree(accessTreeString);



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
        String symmetrickey_String_quit = symmetrickey_String.substring(0,16);

        SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());

        //将传入的MultipartFile 转 File并在本地里临时保存
        File messageFile = new File(messageFileName);
        messageMultipartFile.transferTo(messageFile);


        //传入某格式类型的文件的二进制字符串进行加密
        String message = binaryUtil.getBinaryString(messageFile.toString());
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
        if (accessTree.get("0") == null){
            throw new Exception("根节点的name值必须为0");
        }
        accessTree.get("0").secretShare = s;
        //进行共享，使得每个叶子节点获得响应的秘密分片
        nodeShareUtil.nodeShare(accessTree, "0", bp);

        for (String key : accessTree.keySet()) {
            TreeNode node = accessTree.get(key);
            if (node.isLeaf()){
                Element r = bp.getZr().newRandomElement().getImmutable();

                byte[] idHash = HashUtil.sha1(node.att);
                Element Hi = bp.getG1().newElementFromHash(idHash, 0, idHash.length).getImmutable();

                Element C1 = g_beta.powZn(node.secretShare).mul(Hi.powZn(r.negate()));
                Element C2 = g.powZn(r);

                ctProp.setProperty("C1-"+node.att, Base64.getEncoder().withoutPadding().encodeToString(C1.toBytes()));
                ctProp.setProperty("C2-"+node.att, Base64.getEncoder().withoutPadding().encodeToString(C2.toBytes()));
            }
        }
        // 将策略字符串保存在密文当中
        ctProp.setProperty("Policy", accessTreeString);
        // 将所用的公共参数文件写入密文中，使得解密的时候不需要公钥，只需要密文和私钥
        ctProp.setProperty("ppFile", pkProp.getProperty("ppFile"));
        storePropToFileUtil.storePropToFile(ctProp, ctFileName);

        //删除临时文件, 在这个算法中，messageFileName和policyFileName是临时的路径
        Path messagePath = Paths.get(messageFileName);
//        Path policyPath = Paths.get(policFileName);
        Files.delete(messagePath);
//        Files.delete(policyPath);



//        MultipartFile ctMultipartFile = getMultipartFile(new File(ctFileName.toString()));


        File ctfile = new File(ctFileName);

        //将文件转换成输出流
        OutputStream outputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = new FileInputStream(ctfile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer,0,length);
        }
        fileInputStream.close();


        //删除临时文件，如果不存在会抛出文件不存在会自动抛出异常
        Path ctPath = Paths.get(ctFileName);
        Files.delete(ctPath);


        return outputStream;



        //ctFileName也是临时文件，但是删除这个文件之前必须先产生输出流传回


        //把ctFileName转成文件，再转成流传出去
        //先看看properties能不能直接转成流，如果不行的话就用file把
//        System.out.println(ctFileName);
//        File ctFile = new File(ctFileName);
//        FileOutputStream ctFileOutputStream = new FileOutputStream(ctFile);
//        outputStream = new FileOutputStream(ctfile);
//        return outputStream;



    }

    public static OutputStream decrypt(MultipartFile ctMultipartFile, String ctFileName, String userSk, String messageFileName) throws Exception {

        //判断密钥是动态密钥还是静态密钥，如果是动态密钥则要监测时间是否没超时；
        if (userSk.indexOf("userStaticSk=") == -1) {
            //动态密钥
            String userTimeString = userSk.substring(userSk.indexOf("userDynamicSk=userKeyTime=") + 26, userSk.indexOf("userAttList="));
            System.out.println(userTimeString);
            StringBuilder userTimeStringBuilder = new StringBuilder(userTimeString);

            for (int i = 0; i < userTimeString.length(); i++) {
                userTimeStringBuilder.setCharAt(i, (char) (userTimeString.charAt(i) - 26));
            }
            System.out.println();

            long time1 = Long.parseLong(userTimeStringBuilder.toString());
            System.out.println(String.valueOf(time1));
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

        //判断入参ctFileName
        if (ctFileName == null) {
            throw new Exception("需要临时保存密文的路径不能为空");
        }else {
            File ctFileNameFile_determine = new File(ctFileName);
            if (ctFileNameFile_determine.exists()){
                //如果文件存在，那么可能是具体文件，也可能是目录，目录是不能传的。
                if (ctFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                } else {
                    throw new Exception("此处已经存在文件，不能作为临时文件路径");
                }
            }else {
                if (ctFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                }else { ////////////////////////////////
                    File tempFile = new File(ctFileName);
                    String tempFileName = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().lastIndexOf("\\"));
                    File tempFile_two = new File(tempFileName);
                    if (tempFile_two.exists()){
                        //可以执行算法
                    }else {
                        throw new Exception("目录不存在，不能作为临时文件路径");
                    }
                }
            }

        }


        //判断messageFileName
        if (messageFileName == null) {
            throw new Exception("需要临时保存密文的路径不能为空");
        }else {
            File messageFileNameFile = new File(messageFileName);
            if (messageFileNameFile.exists()){
                //如果文件存在，那么可能是具体文件，也可能是目录，目录是不能传的。
                if (messageFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                } else {
                    throw new Exception("此处已经存在文件，不能作为临时文件路径");
                }
            }else {
                if (messageFileName.lastIndexOf(".") == -1) {
                    throw new Exception("不能传入一个目录路径，得传入一个临时文件的具体路径");
                }else {
//                    String tempFileName = ctFileName.substring(0, ctFileName.lastIndexOf("\\"));
                    File tempFile = new File(messageFileName);
                    String tempFileName = tempFile.getAbsolutePath().substring(0, tempFile.getAbsolutePath().lastIndexOf("\\"));
                    File tempFile_two = new File(tempFileName);
                    if (tempFile_two.exists()){
                        //可以执行算法
                    }else {
                        throw new Exception("目录不存在，不能作为临时文件路径");
                    }
                }
            }

        }



        File ctFile = new File(ctFileName);
        ctMultipartFile.transferTo(ctFile);


        Properties ctProp = loadPropFromFileUtil.loadPropFromFile(ctFileName);
        Pairing bp = PairingFactory.getPairing(ctProp.getProperty("ppFile"));

//        Properties skProp = loadPropFromFileUtil.loadPropFromFile(skFileName);
        String userAttListString = userSk.substring(userSk.indexOf("userAttList=") + 12, userSk.indexOf("D="));
        System.out.println(userAttListString);
        //恢复用户属性列表String[]类型：先将首尾的方括号去除，然后按","分割
        String[] userAttList = userAttListString.substring(1, userAttListString.length()-1).split(", ");



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
                if (Arrays.asList(userAttList).contains(accessTree.get(key).att)){
                    String C1tring = ctProp.getProperty("C1-"+accessTree.get(key).att);
                    Element C1 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C1tring)).getImmutable();
                    String C2tring = ctProp.getProperty("C2-"+accessTree.get(key).att);
                    Element C2 = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(C2tring)).getImmutable();


                    if (userSk.indexOf("D-" + accessTree.get(key).att) != userSk.lastIndexOf("D-")){
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length(), userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length() + 171);
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1,D0).mul(bp.pairing(C2,Datt)).getImmutable();
                        System.out.println("1===========" + DattString);
                    } else {
                        String DattString = userSk.substring(userSk.indexOf("D-" + accessTree.get(key).att) + 3 + accessTree.get(key).att.length());
                        Element Datt = bp.getG1().newElementFromBytes(Base64.getDecoder().decode(DattString)).getImmutable();
                        accessTree.get(key).secretShare = bp.pairing(C1,D0).mul(bp.pairing(C2,Datt)).getImmutable();
                        System.out.println("last===========" + DattString);
                    }

                }
            }
        }



        Element symmetrickey;
        // 进行秘密恢复
        boolean treeOK = nodeRecoverUtil.nodeRecover(accessTree, "0", userAttList, bp);

        if (treeOK) {
            Element egg_alphas = bp.pairing(C0,D).div(accessTree.get("0").secretShare);
            symmetrickey = C.div(egg_alphas);
            String symmetric_ciphertext = ctProp.getProperty("symmetric_ciphertext");

            //修改
            String symmetrickey_String = Base64.getEncoder().withoutPadding().encodeToString(symmetrickey.toBytes());
//            String decryptStr = sm4.decryptStr(message_sm4, CharsetUtil.CHARSET_UTF_8);
            String symmetrickey_String_quit = symmetrickey_String.substring(0,16);
            SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());
//
            //

            //解密函数删除了CharsetUtil.CHARSET_UTF_8，因为我看加密的时候按Hex加密的，解密的时候感觉是不是不对应才报错BadPaddingException: pad block corrupted
            //下面的这个可以做个trycatsh
            String decryptStr = sm4.decryptStr(symmetric_ciphertext, CharsetUtil.CHARSET_UTF_8);
            //下面的这个可以做个trycatsh
            binaryUtil.binaryStringRecover(decryptStr,messageFileName);

            //
//            MultipartFile messageMultipartFile = getMultipartFile(new File(messageFileName.toString()));
//            Path messagePath = Paths.get(messageFileName);
//            Files.delete(messagePath);

            File messageFile = new File(messageFileName);
            //将文件转换成输出流
            OutputStream outputStream = new ByteArrayOutputStream();
            FileInputStream messageFileInputStream = new FileInputStream(messageFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = messageFileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer,0,length);
            }
            messageFileInputStream.close();


            //删除临时文件，如果不存在会抛出文件不存在异常
            Path messagePath = Paths.get(messageFileName);
            Files.delete(messagePath);
            //删除临时文件，在这个算法中，ctFileName是临时文件
            Path ctPath = Paths.get(ctFileName);
            Files.delete(ctPath);
            //messageFileName这个也是临时的文件，删除它之前必须得产生明文流以便能传回
            return outputStream;

        }
        else {
            return null;
        }

    }







    public static void main(String[] args) throws Exception {

        String[] userAttList = {"1", "2","3"};
        String dir = "data/";
        String dir1 = "data1/";
        String pairingParametersFileName = dir + "a.properties";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";
        String skFileName = dir + "sk.properties";
        String ctFileName = dir + "ct.properties";
        String pk1FileName = dir1 + "pk.properties";
        String messageFileName = "C:\\Users\\57802\\Desktop\\解密出来的文件.pdf";
        String messageNewFileName = "C:\\Users\\57802\\Desktop\\解密出来的文件转Multi再转普通文件.doc";

        String cctFileName = "C:\\Users\\57802\\Desktop\\cct.properties";
        File ppolicyFile = new File("C:\\Users\\57802\\Desktop\\config (4).json");
        File mmessageFile = new File("C:\\Users\\57802\\Desktop\\22.doc");


        String jsonString = "{\n" +
                "  \"name\": 0,\n" +
                "  \"value\": \"(2,3)\",\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"name\": 2292,\n" +
                "      \"value\": \"1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": 6667,\n" +
                "      \"value\": \"2\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": 183,\n" +
                "      \"value\": \"3\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        File policyFile = new File("C:\\Users\\57802\\Desktop\\测试实验文件夹\\config (4).json");

        MultipartFile policymultipartFile = getMultipartFileUtil.getMultipartFile(policyFile);

        File messageFile = new File("C:\\Users\\57802\\Desktop\\老师的权重.pdf");
        MultipartFile messagemultipartFile = getMultipartFileUtil.getMultipartFile(messageFile);


        //一天的毫秒数是86400000
        //一分钟的毫秒数是60000
        long time = 60000;
        //初始化
//        setup(pairingParametersFileName,pkFileName, mskFileName);
        //产生用户密钥
        String userSk = keygen(userAttList, pkFileName, mskFileName);
//        String userSk_differentsystem = "userAttList=[0, 1, 2, 3, 4, 10401]D=oGqpjD0nJg2N+IkKs3x5BLwW2zwsiQwyQY4fRIf5G24DcZUINGFNNBTgOQZXCUF4Skp0FBoOo5znZfWI2wmu1ZFx+Yh4ie0yXjncWC7aopbzFKHirk3KcOlCmh8eUBrU38haLQJtLs+7SYtQhqNM4eyTelSgpoY2cCB7Lf8dSgcD0=ge+7JdFQuZ5NKXVOTqV9/yr7vaiR3ALiRNRlK7ni3zxr1+RtlN0ikIRw50eJ4pTkV2Xyk02XvqsL5KYBZZnJjFlE6AHdKjZ4zayrJOS9CadW2TjxE7EDAYHzPQ+Q8+pLBrCaXOeUUy+2llWXK0H2Zs3+T4LWopFmehv8zvLngOAD-0=ab6b7pDQbj1jwEMC05WC9O49pYM/92Ry1rcVxHojcG0tS7XTkr1wpTKDphOQsDdKHZ7WUbPSt1Ew+jpzHmJH3EAhOOlmWnYA32LrBGTW2U3x2FHnosPTPrGoS7sjsKo6daIhXtITZqgFe0M2VvohvaX1FG8ivQTQI1WM5XV26+wD-1=E1yrRt+FP7BjcVYXvD/6OwQZ+76NqwcPrYXru2Rv4UD2uIyT+cM9PCg4cbt+Ds5z20hp92OvT7qjMh7YsKF/X5xqD3qSxq/yLvlQB/GyVs7epR7yWidQF1utzvMOKgDCi0P2jWmiRa1s4tuNBGu9/J4Xq2Dh1yAE5AVh7fCJFUoD-2=YqHGXczHlNjzqNu6SzvO3PIhwRew3scNw1IV09GYRi7Lbgl5WqkrSqQlop35jARQcdEmC2Q8kaTIKnzEAI1ICXDRyu7aVpWpClR9qXiCJg6e1vto3s9dKbkDVH9eCmwMsMSIABetBvt9GCGrShIyoWea76vY9qALwrpdkdKaKloD-3=k65GZD2+jqbBGVS6f3p+SRNd1UmMzQQkIP5cAWTqN0QqEn8F6V6idZyeM98EHkjjCjsuKAR2B3x5BgzSlMzbz4+6TffZ0tfwEbAnXEYl1/WE6cCbS1EkXVE4QP7q7nJHcrjjRpcQGTCVQehoPQlPyBO0JSPj9XCYuMtXHg3j+bUD-4=KyusdKmDfaJAbR7sp5CF6maiZjk3fDK6PhJ7OEPHBfL7gO0iEuXyD74GZQCCmuZNGZLv75V9BAtzoQLAR0yd9jOWuJjtxj6psGst5reKwRT9H7+VfPrpFG6hodZObuog+lAqod+WsZDVRW00/MN/UgphIYimRPLfY17K+nB8CX4D-10401=dW437PS6flVoTPMBCK59ZgtkEG0Cf5z5jP/K3qeIfcpzSQPk9jSTLFxbk4G0rsYP23tn7/wHOCU5B22tGiza5pchaINs6yOz6SvRDKaxq2YhNnhbh985U32g3gpaWWZ1p/IGuqmLC1SrCZaxpkoiWUkNmQFihKFCC/7SlIicx9A";
        System.out.println(userSk);
        //加密文件，产生一个MultipartFile文件,注意什么类型的文件转MultipartFile，填入的mmessageFile也必须是相同的类型
//        OutputStream ctOutputStream = encrypt(messagemultipartFile, mmessageFile.toString(), policymultipartFile, ppolicyFile.toString(), pkFileName, ctFileName);
        OutputStream ctOutputStream = encrypt(messagemultipartFile, mmessageFile.toString(), jsonString, pkFileName, ctFileName);
        //OutputStream-->txt文件--》MultipartFile-->txt文件
        //将输出流转换为密文文件
        String outputStreamToCtFileName = "C:\\Users\\57802\\Desktop\\outputStreamToCt.txt";
        byte[] data = ((ByteArrayOutputStream) ctOutputStream).toByteArray();
        File ctFile = new File(outputStreamToCtFileName);
        FileOutputStream ctfileOutputStream = new FileOutputStream(ctFile);
        ctfileOutputStream.write(data);
        ctfileOutputStream.close();

        //密文文件转成MultipartFile
        File outputStreamCtFile = new File("C:\\Users\\57802\\Desktop\\outputStreamToCt.txt");
        MultipartFile ctMultipartFile = getMultipartFileUtil.getMultipartFile(outputStreamCtFile);

        //解密文件，产生一个MultipartFile文件
        OutputStream messageOutputStream = decrypt(ctMultipartFile, cctFileName, userSk, messageFileName);




        //将解密出来的MultipartFile类型保存到本地看看正确不
        if (messageOutputStream != null) {
            //将输出流转换为明文文件
            String outputStreamToMessageFileName = "C:\\Users\\57802\\Desktop\\outputStreamToMessage.pdf";
            byte[] messageData = ((ByteArrayOutputStream) messageOutputStream).toByteArray();
            File outputStreamToMessageFile = new File(outputStreamToMessageFileName);
            FileOutputStream messageFileOutputStream = new FileOutputStream(outputStreamToMessageFile);
            messageFileOutputStream.write(messageData);
            messageFileOutputStream.close();


//            //添加水印
//            //添加水印
//            String baseSrcUrl = "C:\\Users\\57802\\Desktop\\outputStreamToMessage.pdf";
//            String baseOutUrl = "C:\\Users\\57802\\Desktop\\添加水印之后的解密文件.pdf";
//            String username = "Test";
//            PdfWaterMarkUtil pwm=new PdfWaterMarkUtil();
//            pwm.addWaterMark(baseSrcUrl, baseOutUrl,username);
        } else {
            System.out.println("The access tree is not satisfied.");
        }





    }

}
