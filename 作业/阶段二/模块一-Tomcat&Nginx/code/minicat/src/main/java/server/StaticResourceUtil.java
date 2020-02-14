package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticResourceUtil {
    /**
     * 获取静态资源文件的绝对路径
     * @param path
     * @return
     */
    public static String getAbsolutePath(String path){
        String absolutePath = StaticResourceUtil.class.getResource("/").getPath();
        absolutePath = absolutePath.replaceAll("\\\\","/");
        if (absolutePath.charAt(absolutePath.length() - 1) == '/'){
            absolutePath = absolutePath.substring(0, absolutePath.length() - 1);
        }
        return absolutePath + path;
    }


    /**
     * 读取静态资源文件输入流，通过输出流输出
     */
    public static void  outputStaticResource(InputStream inputStream, OutputStream outputStream) throws IOException {
        int count = 0;
        while (count == 0){
            count = inputStream.available();
        }

        int resourceSize = count; // 静态文件长度
        // 输出http请求头，然后再输出具体内容
        outputStream.write(HttpProtocolUtil.getHttpHeader200(resourceSize).getBytes());

        // 读取内容输出
        long written = 0;   // 已经读取的内容长度
        int byteSize = 1024;// 计划每次缓冲的长度
        byte[] bytes = new byte[byteSize];

        while (written < resourceSize){
            // 剩余未读取大小不足一个1024，那就按照真实长度处理
            if (written + byteSize > resourceSize){
                byteSize = (int) (resourceSize - written); // 剩余文件内容长度
                bytes = new byte[byteSize];
            }

            inputStream.read(bytes);
            outputStream.write(bytes);
            outputStream.flush();

            written += byteSize;
        }
    }

}
