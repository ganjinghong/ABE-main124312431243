//
//    import java.security.NoSuchAlgorithmException;
//    public class Test_new {
//        public static void main(String[] args) throws Exception {
//            //这个用户的属性是写死的，这个得改，这个是面对KeyGen算法的。
//            // 这个得从网页端传过来。
//            String[] userAttList = {"1", "2", "3","4"};
//
//
//            //这个是面对Enc算法的，Enc算法必须传入一棵树。这个也得从网页端传过来
//            //用数组的方法构建一棵树
////            TreeNode[] accessTree = new TreeNode[5];
////            accessTree[0] = new TreeNode(new int[]{4,4}, new int[]{1,2,3,4});
////            accessTree[1] = new TreeNode("1");
////            accessTree[2] = new TreeNode("2");
////            accessTree[3] = new TreeNode("3");
////            accessTree[4] = new TreeNode("4");
//
//
//            //这是保存各个阶段的信息文件，有些是单个用户  有些是全部用户一起用的
//            String dir = "data/";
//            String pairingParametersFileName = "a.properties";
//            String pkFileName = dir + "pk.properties";
//            String mskFileName = dir + "msk.properties";
//            String skFileName = dir + "sk.properties";
//            String ctFileName = dir + "ct.properties";
//
//
//            //注意：以下的文件路径都是写死的。具体情况还得看前端传来什么
//            cpabeUtil.setup(pairingParametersFileName,pkFileName, mskFileName);
//            cpabeUtil.keygen(userAttList, pkFileName, mskFileName, skFileName);
//
//
//
//            //jpg照片文件类型先转为pdf
////            String imagePath = "C:\\Users\\57802\\Desktop\\11.jpg";
////            String pdfPath = "C:\\Users\\57802\\Desktop\\11.pdf";
////            imgToPdfUtil.imgToPdf(imagePath, pdfPath);
//
//            //png照片文件类型先转为pdf
////            String imagePath = "C:\\Users\\57802\\Desktop\\11.png";
////            String pdfPath = "C:\\Users\\57802\\Desktop\\11.pdf";
////            imgToPdfUtil.imgToPDF(imagePath, pdfPath);
//
//            //txt文件类型先转pdf
////            String txtPath = "C:\\Users\\57802\\Desktop\\1.txt";
////            String pdfPath = "C:\\Users\\57802\\Desktop\\1";
////            txtToPdfUtil.txtToPdf(txtPath, pdfPath);
//
//            //word文件类型先转为pdf
////            String srcPath = "C:/Users/57802/Desktop/11.doc";
////            String desPath = "C:/Users/57802/Desktop/11";
////            docToPdfUtil.docToPdf(srcPath, desPath);
//
//
//            String policyName = "C:\\Users\\57802\\Desktop\\测试实验文件夹\\config (4).json";
////            PdfWaterMarkUtil pwm=new PdfWaterMarkUtil();
////            pwm.addWaterMark(baseSrcUrl, baseOutUrl,username);
//
//
//            //pdf转化为二进制
//            String message = binaryUtil.getBinaryString("C:\\Users\\57802\\Desktop\\11.jpg");
//            //二进制放进加密包中进行加密
//            cpabeUtil.encrypt(message, policyName, pkFileName, ctFileName);
//
//            //解密
//            String messageBinary_String_get = cpabeUtil.decrypt(ctFileName, skFileName);
//            if (messageBinary_String_get == null) {
//                System.out.println("The access tree is not satisfied.");
//            } else {
//                binaryUtil.binaryStringRecover(messageBinary_String_get,"C:\\Users\\57802\\Desktop\\解密出来的文件.jpg");
////                //添加水印
////                String baseSrcUrl = "C:\\Users\\57802\\Desktop\\解密出来的文件.pdf";
////                String baseOutUrl = "C:\\Users\\57802\\Desktop\\添加水印之后的解密文件.pdf";
////                String username = "Test";
////                PdfWaterMarkUtil pwm=new PdfWaterMarkUtil();
////                pwm.addWaterMark(baseSrcUrl, baseOutUrl,username);
//
//                //pdf转化为png
////                String srcPath= "C:/Users/57802/Desktop/添加水印之后的解密文件.pdf";
////                String desPath= "C:/Users/57802/Desktop/添加水印之后的PNG解密文件";
////
////                pdfToImgUtil.pdfToPng(srcPath, desPath);
//            }
//        }
//    }
//
//            //因为每个公司有很多个人，而现在存储信息的文件都是一个文件名，所以应该有两种方法区别这些人的文件
//            //比如：1、每次执行完之后都把文件的信息除了pk和msk的全部清楚
//            //2、再弄个前缀，会随着哪个用户执行，前缀就加哪个用户
//
//
//
////        setup(pairingParametersFileName, pkFileName, mskFileName);
////
////        keygen(pairingParametersFileName, userAttList, pkFileName, mskFileName, skFileName);
////
////        String message = "woaini";
////        System.out.println("message: " + message);
////
////        encrypt(pairingParametersFileName, message, accessTree, pkFileName, ctFileName);
////
////        String message_String_get = Decrypt(pairingParametersFileName, accessTree, ctFileName, skFileName);
////
////        System.out.println("message_String_get: " + message_String_get);
//
//
//
//
//
//            //        Element symmetrickey = PairingFactory.getPairing(pairingParametersFileName).getGT().newRandomElement().getImmutable();
////        String symmetrickey_String = Base64.getEncoder().withoutPadding().encodeToString(symmetrickey.toBytes());
////        String symmetrickey_String_quit = symmetrickey_String.substring(0,16);
////        System.out.println(symmetrickey_String_quit);
////        System.out.println(Arrays.toString(symmetrickey_String_quit.getBytes()));
////        SymmetricCrypto sm4 = SmUtil.sm4(symmetrickey_String_quit.getBytes());
//
////        if (symmetrickey_String_get_quit.equals(symmetrickey_String_quit)) {
////            String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
////            System.out.println("解密结果:" + decryptStr);
////        } else {
////            System.out.println("false" );
////        }
//
