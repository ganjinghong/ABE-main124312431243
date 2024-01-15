
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public final class txtToPdfUtil {
    private static final Logger logger = LoggerFactory.getLogger(docToPdfUtil.class);
    //+ srcPath + " --outdir " + desPath
    public static void main(String[] args) throws NullPointerException {
        String srcPath = "C:/Users/57802/Desktop/1.txt", desPath = "C:/Users/57802/Desktop/1";
        txtToPdf(srcPath, desPath);
//        String command = "";
//        String osName = System.getProperty("os.name");
//        if (osName.contains("Windows")) {
//            command = "cmd /c soffice --headless --convert-to pdf " + srcPath + " --outdir " + desPath;
//            exec(command);
//        }
    }
    public static void txtToPdf(String srcPath, String desPath) {
        String command = "";
        String osName = System.getProperty("os.name");
        if (osName.contains("Windows")) {
            command = "cmd /c soffice --headless --convert-to pdf " + srcPath + " --outdir " + desPath;
            exec(command);
        }
    }

    public static boolean exec(String command) {
        Process process;// Process可以控制该子进程的执行或获取该子进程的信息
        try {
            logger.debug("exec cmd : {}", command);
            process = Runtime.getRuntime().exec(command);// exec()方法指示Java虚拟机创建一个子进程执行指定的可执行程序，并返回与该子进程对应的Process对象实例。
            // 下面两个可以获取输入输出流
            InputStream errorStream = process.getErrorStream();
            InputStream inputStream = process.getInputStream();
        } catch (IOException e) {
            logger.error(" exec {} error", command, e);
            return false;
        }

        int exitStatus = 0;
        try {
            exitStatus = process.waitFor();// 等待子进程完成再往下执行，返回值是子线程执行完毕的返回值,返回0表示正常结束
            // 第二种接受返回值的方法
            int i = process.exitValue(); // 接收执行完毕的返回值
            logger.debug("i----" + i);
        } catch (InterruptedException e) {
            logger.error("InterruptedException  exec {}", command, e);
            return false;
        }

        if (exitStatus != 0) {
            logger.error("exec cmd exitStatus {}", exitStatus);
        } else {
            logger.debug("exec cmd exitStatus {}", exitStatus);
        }

        process.destroy(); // 销毁子进程
        process = null;

        return true;
    }

}