package server;

import server.core.Connector;
import server.core.Mapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 建立ServerSocket，监听不同的端口
 */
public class Coyote extends Thread {
    private Connector connector;
    private ThreadPoolExecutor threadPoolExecutor;
    private Mapper mapper;

    public Coyote(Connector connector, ThreadPoolExecutor threadPoolExecutor, Mapper mapper) {
        this.connector = connector;
        this.threadPoolExecutor = threadPoolExecutor;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        try {
            // 根据设置监听端口
            ServerSocket serverSocket = new ServerSocket(this.connector.getPort());
            while (true){
                // 使用线程池
                Socket socket = serverSocket.accept();
                RequestProcessor requestProcessor = new RequestProcessor(socket, mapper);
                threadPoolExecutor.execute(requestProcessor);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
