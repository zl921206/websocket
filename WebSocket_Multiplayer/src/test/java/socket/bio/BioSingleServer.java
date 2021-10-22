package socket.bio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * 一个单独的用于处理socket信息的服务
 */
public class BioSingleServer implements Runnable {

    private Socket socket;
    private int clientNo;

    public BioSingleServer() {
    }

    public BioSingleServer(Socket socket, int clientNo) {
        this.socket = socket;
        this.clientNo = clientNo;
    }

    String msg = "";

    @Override
    public void run() {
        try {
            /**
             * 获取创建输入流
             */
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            /**
             * 获取创建输出流
             */
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            while (true) {
                // 从输入流中读取客户端请求消息
                msg = dis.readUTF();
                System.out.println("从客户端：" + clientNo + "，接收到的消息为：" + msg);
                // 写入服务端响应消息到输出流
                dos.writeUTF("客户端你好！已收到消息：" + msg);
                dos.flush();
                if (msg.equals("close")) {
                    socket.close();
                    System.out.println("与客户端：" + clientNo + "，通信结束");
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
