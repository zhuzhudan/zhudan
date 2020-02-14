package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;

/**
 * 将返回信息封装成Response对象，需要依赖于OutputStream
 * 该对象需要提供核心方法，输出 静态资源html（2.0）
 */
public class Response {
    private OutputStream outputStream;

    public Response() {
    }

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    // 使用输出流输出指定字符串
    public void output(String content) throws IOException {
        outputStream.write(content.getBytes());
    }

    /**
     * 要根据url来获取到静态资源的绝对路径（磁盘路径），
     * 进一步根据绝对路径读取该静态资源文件，
     * 最终通过输出流输出
     */
    public void outputHtml(String absoluteResourcePath) throws IOException {
        // 对路径解码
        absoluteResourcePath = URLDecoder.decode(absoluteResourcePath);
        // 输出静态资源文件
        File file = new File(absoluteResourcePath);
        if (file.exists() && file.isFile()){
            // 读取静态资源文件，输出静态资源 -- 提取，封装进静态资源工具类中
            StaticResourceUtil.outputStaticResource(new FileInputStream(file), outputStream);
        } else {
            // 输出404
            output(HttpProtocolUtil.getHttpHeader404());
        }
    }

}
