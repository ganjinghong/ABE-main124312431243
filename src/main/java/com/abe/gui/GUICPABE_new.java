package com.abe.gui;

import com.abe.FileToPngAndWaterMark;
import com.abe.util.PasswordEncryptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class GUICPABE_new {
    private static String account;
    private static String password;
    private static String mskFileName;
    private static String skFileName;
    private static String ctFileName;
    private static String plainFileName;
    private static String[] userAttList;
    private static String data;
    private static String policyFileName;
    private static String lastPath = "~/";

    public static void main(String[] args) {
        createWindow();
    }

    private static void createWindow() {
        //登录界面
        JFrame login = new JFrame("登录界面");
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginUI(login);
        login.setSize(800, 300);
        login.setResizable(false);
        login.setLocationRelativeTo(null);
        login.setVisible(true);


        //解密界面
//        JFrame frame = new JFrame("属性基单机版解密软件");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        createUI(frame);
//        frame.setSize(800, 300);
//        frame.setResizable(false);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
    }

    private static void loginUI(final JFrame login) {

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel loginPanel = LoginUI(login);
        tabbedPane.addTab("登录", loginPanel);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

//        JPanel helpPanel = helpUI(login) ;
//        tabbedPane.addTab("帮助", helpPanel);
//        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        login.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

//    private static void createUI(final JFrame frame){
//
//        JTabbedPane tabbedPane = new JTabbedPane();
//
//        JPanel decPanel = DecryptUI(frame);
//        tabbedPane.addTab("解密文件", decPanel);
//        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
//
//        JPanel helpPanel = helpUI(frame) ;
//        tabbedPane.addTab("帮助", helpPanel);
//        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
//
//        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
//    }

//    private static JPanel helpUI(final JFrame frame){
//        JPanel jp = new JPanel(new FlowLayout());
//        JTextArea textArea = new JTextArea( 12, 57);
//        textArea.setFont(new Font("宋体",Font.PLAIN,14));
//        textArea.setLineWrap(true);        //激活自动换行功能
//        textArea.setEditable(false);
//        JScrollPane scroll = new JScrollPane(textArea);
//        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//
//        // 只需要添加滚动条即可
//        jp.add(scroll, BorderLayout.EAST);
//        return jp;
//    }


    private static JPanel LoginUI(final JFrame login) {
        JPanel jp = new JPanel(new FlowLayout());

        JButton skButton = new JButton("选择用户私钥文件*");
        skButton.setPreferredSize(new Dimension(200, 20));
        skButton.setForeground(Color.RED);
        JTextField skTextField = new JTextField("选择你要用于解密的私钥", 40);
        skTextField.setForeground(Color.gray);
        skTextField.setEditable(false);
        skButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(lastPath);
            fileChooser.setDialogTitle("选择用户私钥文件");
            int option = fileChooser.showOpenDialog(login);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                skFileName = file.toString();
                skTextField.setText(skFileName);
                lastPath = file.getParentFile().toString();
            }
        });

        JLabel usernameLabel = new JLabel("用户名:");
        JLabel passwordLabel = new JLabel("密码:");
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JButton decButton = new JButton("login");
        decButton.setPreferredSize(new Dimension(200, 40));
        decButton.setForeground(Color.BLUE);
        decButton.addActionListener(e -> {
//            String username = usernameField.getText();
//            String password = new String(passwordField.getPassword());
            String username = "zhangxi";
            String password = "string";
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请输入用户名和密码", "登录失败", JOptionPane.ERROR_MESSAGE);
            } else {
                if (PasswordEncryptor.encrypt(password).equals(password)) {
                    JOptionPane.showMessageDialog(null, "密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(null, "登录成功", "登录成功", JOptionPane.INFORMATION_MESSAGE);
                jp.remove(usernameLabel);
                jp.remove(usernameField);
                jp.remove(passwordLabel);
                jp.remove(passwordField);
                jp.remove(decButton);
                //添加解密输入框
                JButton decryptButton = new JButton("在线查看");
                decryptButton.setPreferredSize(new Dimension(200, 40));
                decryptButton.setForeground(Color.BLUE);
                decryptButton.addActionListener(e1 -> {
                    if (ctFileName == null) {
                        JOptionPane.showMessageDialog(login, "请先选择待解密文件");
                        return;
                    }
                    if (skFileName == null) {
                        JOptionPane.showMessageDialog(login, "请先选择秘钥文件");
                        return;
                    }
                    //存储到项目的data文件夹中
                    plainFileName = "./data/" + ctFileName.substring(ctFileName.lastIndexOf("/") + 1);
                    //去除后缀
                    plainFileName = plainFileName.substring(0, plainFileName.lastIndexOf("."));
                    System.out.println(ctFileName);
                    System.out.println(skFileName);
                    System.out.println(plainFileName);
                    boolean res = cpabeUtil.decrypt(ctFileName, skFileName, plainFileName);

                    if (res) {
                        String pdfUrl;
                        if (plainFileName.endsWith(".docx") || plainFileName.endsWith(".doc")) {
                            pdfUrl = plainFileName.substring(0, plainFileName.lastIndexOf(".")) + ".pdf";
                            try {
                                FileToPngAndWaterMark.wordToPdf(plainFileName, pdfUrl);
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(login, "文件转换失败");
                                throw new RuntimeException(ex);
                            }
                        } else {
                            pdfUrl = plainFileName;
                        }

                        SwingUtilities.invokeLater(() -> {
                            int displayWidth = 1200; // 设置展示宽度
                            int displayHeight = 800; // 设置展示高度
                            DocumentViewer viewer = new DocumentViewer(displayWidth, displayHeight);
                            viewer.setVisible(true);
                            viewer.showPDF(pdfUrl);
                        });
                    } else {
                        JOptionPane.showMessageDialog(login, "解密失败,可能密钥的权限不够或者密钥的有效时间过期了");
                    }
                });
                //上传密文文件
                JButton ctButton = new JButton("选择待查看密文文件*");
                ctButton.setPreferredSize(new Dimension(200, 20));
                ctButton.setForeground(Color.RED);
                JTextField ctTextField = new JTextField("选择你想查看的文件", 40);
                ctTextField.setForeground(Color.gray);
                ctTextField.setEditable(false);
                ctButton.addActionListener(e1 -> {
                    JFileChooser fileChooser = new JFileChooser(lastPath);
                    fileChooser.setDialogTitle("选择待查看文件");
                    int option = fileChooser.showOpenDialog(login);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        ctFileName = file.toString();
                        ctTextField.setText(ctFileName);
                        lastPath = file.getParentFile().toString();
                    }
                });
                jp.add(ctButton);
                jp.add(ctTextField);
                jp.add(decryptButton);
            }
        });

        jp.add(skButton);
        jp.add(skTextField);
        jp.add(usernameLabel);
        jp.add(usernameField);
        jp.add(passwordLabel);
        jp.add(passwordField);
        jp.add(decButton);

        return jp;
    }


//    private static JPanel DecryptUI(final JFrame frame){
//        JPanel jp = new JPanel(new FlowLayout());
//
//        JButton ctButton = new JButton("选择待解密密文文件*");
//        ctButton.setPreferredSize(new Dimension(200, 20));
//        ctButton.setForeground(Color.RED);
//        JTextField ctTextField = new JTextField("选择你想解密的文件",40);
//        ctTextField.setForeground(Color.gray);
//        ctTextField.setEditable(false);
//
//        ctButton.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser(lastPath);
//            fileChooser.setDialogTitle("选择待解密密文文件");
//            int option = fileChooser.showOpenDialog(frame);
//            if(option == JFileChooser.APPROVE_OPTION){
//                File file = fileChooser.getSelectedFile();
//                ctFileName = file.toString();
//                ctTextField.setText(ctFileName);
//                lastPath = file.getParentFile().toString();
//            }
//        });
//
//        JButton policyButton = new JButton("导出密文访问策略");
//        policyButton.setPreferredSize(new Dimension(200, 20));
//        JTextField policyTextField = new JTextField("导出的访问策略可以在浏览器打开access_tree.html后导入以图形化方式查看。", 40);
//        policyTextField.setForeground(Color.gray);
//        policyTextField.setEditable(false);
//
//        policyButton.addActionListener(e -> {
//            if (ctFileName == null) {
//                JOptionPane.showMessageDialog(frame, "请先选择待解密文件");
//                return;
//            }
//            JFileChooser fileChooser = new JFileChooser(lastPath);
//            fileChooser.setDialogTitle("导出密文访问策略至");
//            int option = fileChooser.showSaveDialog(frame);
//            if(option == JFileChooser.APPROVE_OPTION){
//                File file = fileChooser.getSelectedFile();
//                lastPath = file.getParentFile().toString();
//                Properties ctProp = loadPropFromFileUtil.loadPropFromFile(ctFileName);
//                String accessTreeString = ctProp.getProperty("Policy");
//                // 将accessTree的json字符串写入用户选择的文件中
//                try(PrintWriter out = new PrintWriter(file.toString())) {
//                    out.write(accessTreeString);
//                    JOptionPane.showMessageDialog(frame, "密文访问策略已保存至： " + file.toString(), "密文策略导出成功", JOptionPane.PLAIN_MESSAGE);
//                    policyTextField.setForeground(Color.BLACK);
//                    policyTextField.setText("密文访问策略已保存至： " + file.toString());
//                } catch (FileNotFoundException ef) {
//                    ef.printStackTrace();
//                    System.exit(-1);
//                }
//
//            }
//        });
//
//        JButton skButton = new JButton("选择用户私钥文件*");
//        skButton.setPreferredSize(new Dimension(200, 20));
//        skButton.setForeground(Color.RED);
//        JTextField skTextField = new JTextField("选择你要用于解密的私钥",40);
//        skTextField.setForeground(Color.gray);
//        skTextField.setEditable(false);
//        skButton.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser(lastPath);
//            fileChooser.setDialogTitle("选择用户私钥文件");
//            int option = fileChooser.showOpenDialog(frame);
//            if(option == JFileChooser.APPROVE_OPTION){
//                File file = fileChooser.getSelectedFile();
//                skFileName = file.toString();
//                skTextField.setText(skFileName);
//                lastPath = file.getParentFile().toString();
//            }
//        });
//
//        JButton skAttButton = new JButton("查看用户私钥对应的属性");
//        skAttButton.setPreferredSize(new Dimension(200, 20));
//        JTextField skAttTextField = new JTextField("查看你导出的私钥文件中具有的属性有什么",40);
//        skAttTextField.setForeground(Color.gray);
//        skAttTextField.setEditable(false);
//        skAttButton.addActionListener(e -> {
//            if (skFileName == null) {
//                JOptionPane.showMessageDialog(frame, "请先选择秘钥文件");
//                return;
//            }
//            Properties skProp = loadPropFromFileUtil.loadPropFromFile(skFileName);
//            String userSk = skProp.getProperty("userSk");
//            String userAttListString = userSk.substring(userSk.indexOf("userAttList=") + 12, userSk.indexOf("D="));
//            skAttTextField.setText("私钥文件中具有的属性为："+ userAttListString);
//        });
//
//        JButton decButton = new JButton("解密密文");
//        decButton.setPreferredSize(new Dimension(200, 40));
//        decButton.setForeground(Color.BLUE);
//        decButton.addActionListener(e -> {
//
//            if (ctFileName == null) {
//                JOptionPane.showMessageDialog(frame, "请先选择待解密文件");
//                return;
//            }
//            if (skFileName == null) {
//                JOptionPane.showMessageDialog(frame, "请先选择秘钥文件");
//                return;
//            }
//            JFileChooser fileChooser = new JFileChooser(lastPath);
//            fileChooser.setDialogTitle("选择明文要保存到的文件");
//            int option = fileChooser.showSaveDialog(frame);
//            if(option == JFileChooser.APPROVE_OPTION){
//                File file = fileChooser.getSelectedFile();
//                plainFileName = file.toString();
//                lastPath = file.getParentFile().toString();
//            }
//            else {
//                plainFileName = null;
//                return;
//            }
//            boolean res = cpabeUtil.decrypt(ctFileName, skFileName, plainFileName);
//            if (res == true){
//                JOptionPane.showMessageDialog(frame, String.format("解密结果已保存至：%s，如果出现乱码则表示还原文件的后缀和加密前不一样", plainFileName),"解密成功", JOptionPane.PLAIN_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(frame, "解密失败,可能密钥的权限不够或者密钥的有效时间过期了");
//            }
//        });
//
//        jp.add(ctButton);
//        jp.add(ctTextField);
//        jp.add(policyButton);
//        jp.add(policyTextField);
//        jp.add(skButton);
//        jp.add(skTextField);
//        jp.add(skAttButton);
//        jp.add(skAttTextField);
//        jp.add(decButton);
//
//        return jp;
//    }

}
