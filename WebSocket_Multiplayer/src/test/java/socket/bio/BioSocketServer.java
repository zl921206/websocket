package socket.bio;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * socket服务端: BIO，同步阻塞IO
 */
public class BioSocketServer {

    public static void main(String[] args) throws Exception {
        int port = 5588;
        int clientNo = 1;
        /**
         * 创建服务端socket
         */
        ServerSocket serverSocket = new ServerSocket(port);
        /**
         * 创建线程池
         */
        ExecutorService es = Executors.newCachedThreadPool();
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                /**
                 * 使用多线程处理socket信息服务
                 */
                es.execute(new BioSingleServer(socket, clientNo));
                clientNo++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("close serverSocket......");
            serverSocket.close();
        }
    }
}
