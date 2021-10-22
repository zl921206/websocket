package socket.bio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * socket客户端
 */
public class BioSocketClient {

    public static void main(String[] args) {
        int port = 5588;
        String host = "localhost";
        /**
         * 创建一个socket并将其连接到指定端口号
         */
        Socket socket = null;
        String reqMsg = "";
        try {
            socket = new Socket(host, port);
            /**
             * 创建输入流
             */
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            /**
             * 创建输出流
             */
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("请输出要发送给服务器端的消息......");
                reqMsg = sc.next();
                // 写入请求消息到输出流
//                reqMsg = new BufferedReader(new InputStreamReader(System.in)).readLine();
                dos.writeUTF(reqMsg);
                dos.flush();
                // 从输入流中读取响应消息
                String respMsg = dis.readUTF();
                System.out.println("服务器端返回的消息：" + respMsg);
                System.out.println("-------------------------------------");
                if(reqMsg.equals("close")){
                    socket.close();
                    System.out.println("close socket......");
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }
}
