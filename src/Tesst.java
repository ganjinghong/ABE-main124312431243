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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class Tesst {
    public static void setup(String pairingParametersFileName, String pkFileName, String mskFileName) {

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

    public static String keygen(String[] userAttList, String pkFileName, String mskFileName) throws NoSuchAlgorithmException {

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


        String total = "";
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

    public static MultipartFile encrypt(MultipartFile messageMultipartFile, String messageFileName, MultipartFile policMultipartFile, String policFileName, String pkFileName, String ctFileName) throws NoSuchAlgorithmException, IOException {

        File policfile = new File(policFileName);
        policMultipartFile.transferTo(policfile);
        String accessTreeString= FileUtils.readFileToString(policfile,"UTF-8");
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
        String symmetric_ciphertext = sm4.encryptHex(message);
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
        Path policyPath = Paths.get(policFileName);
        Files.delete(messagePath);
        Files.delete(policyPath);
        MultipartFile ctMultipartFile = getMultipartFile(new File(ctFileName.toString()));
        Path ctPath = Paths.get(ctFileName);
        Files.delete(ctPath);
        return ctMultipartFile;



        //ctFileName也是临时文件，但是删除这个文件之前必须先产生输出流传回


        //把ctFileName转成文件，再转成流传出去
        //先看看properties能不能直接转成流，如果不行的话就用file把
//        System.out.println(ctFileName);
//        File ctFile = new File(ctFileName);
//        FileOutputStream ctFileOutputStream = new FileOutputStream(ctFile);
//        outputStream = new FileOutputStream(ctfile);
//        return outputStream;



    }

    public static MultipartFile decrypt(MultipartFile ctMultipartFile, String ctFileName, String userSk, String messageFileName) throws IOException {

        File ctFile = new File(ctFileName);
        ctMultipartFile.transferTo(ctFile);


        Properties ctProp = loadPropFromFileUtil.loadPropFromFile(ctFileName);
        Pairing bp = PairingFactory.getPairing(ctProp.getProperty("ppFile"));

//        Properties skProp = loadPropFromFileUtil.loadPropFromFile(skFileName);
        String userAttListString = userSk.substring(userSk.indexOf("userAttList=") + 12, userSk.indexOf("D="));
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

            String symmetrickey_String = Base64.getEncoder().withoutPadding().encodeToString(symmetrickey.toBytes());
//            String decryptStr = sm4.decryptStr(message_sm4, CharsetUtil.CHARSET_UTF_8);
            String symmetrickey_String_quit = symmetrickey_String.substring(0,16);
            SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());
//
            //
            //
            String decryptStr = sm4.decryptStr(symmetric_ciphertext, CharsetUtil.CHARSET_UTF_8);
            binaryUtil.binaryStringRecover(decryptStr,messageFileName);
            MultipartFile messageMultipartFile = getMultipartFile(new File(messageFileName.toString()));
            Path messagePath = Paths.get(messageFileName);
            Files.delete(messagePath);

            //删除临时文件，在这个算法中，ctFileName是临时文件
            Path policyPath = Paths.get(ctFileName);
            Files.delete(policyPath);
            //messageFileName这个也是临时的文件，删除它之前必须得产生明文流以便能传回
            return messageMultipartFile;

        }
        else {
            return null;
        }

    }


    public static MultipartFile getMultipartFile(File file){
        FileInputStream fileInputStream = null;
        MultipartFile multipartFile = null;
        try {
            fileInputStream = new FileInputStream(file);
            multipartFile = new MockMultipartFile(file.getName(),file.getName(),
                    ContentType.APPLICATION_OCTET_STREAM.toString(),fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multipartFile;
    }



    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        String[] userAttList = {"1", "2", "3","4"};
        String dir = "data/";
        String pairingParametersFileName = "a.properties";
        String pkFileName = dir + "pk.properties";
        String mskFileName = dir + "msk.properties";
        String skFileName = dir + "sk.properties";
        String ctFileName = dir + "ct.properties";
        String messageFileName = "C:\\Users\\57802\\Desktop\\解密出来的文件.doc";
        String messageNewFileName = "C:\\Users\\57802\\Desktop\\解密出来的文件转Multi再转普通文件.doc";

        String cctFileName = "C:\\Users\\57802\\Desktop\\cct.properties";
        File ppolicyFile = new File("C:\\Users\\57802\\Desktop\\config (5).json");
        File mmessageFile = new File("C:\\Users\\57802\\Desktop\\22.doc");


        File policyFile = new File("C:\\Users\\57802\\Desktop\\测试实验文件夹\\config (4).json");

        MultipartFile policymultipartFile = getMultipartFile(policyFile);

        File messageFile = new File("C:\\Users\\57802\\Desktop\\11.doc");
        MultipartFile messagemultipartFile = getMultipartFile(messageFile);

        //初始化
        setup(pairingParametersFileName,pkFileName, mskFileName);
        //产生用户密钥
        String userSk = keygen(userAttList, pkFileName, mskFileName);
        //加密文件，产生一个MultipartFile文件,注意什么类型的文件转MultipartFile，填入的mmessageFile也必须是相同的类型
        MultipartFile ctMultipartFile = encrypt(messagemultipartFile, mmessageFile.toString(), policymultipartFile, ppolicyFile.toString(), pkFileName, ctFileName);
        //解密文件，产生一个MultipartFile文件
        MultipartFile messageNewMultipartFile = decrypt(ctMultipartFile, cctFileName, userSk, messageFileName);
        //将解密出来的MultipartFile类型保存到本地看看正确不
        if (messageNewMultipartFile != null) {
            File messageNew = new File(messageNewFileName);
            messageNewMultipartFile.transferTo(messageNew);
        } else {
            System.out.println("The access tree is not satisfied.");
        }

    }
}
