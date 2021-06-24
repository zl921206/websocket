package socket.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Socket 客户端
 */
public class NioSocketClient {

    public static void main(String[] args) {
        /**
         * 1：创建连接地址
         */
        InetSocketAddress address = new InetSocketAddress("localhost", 5588);
        // 2: 声明连接通道
        SocketChannel sc = null;
        // 3: 建立缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);
        try {
            // 4: 打开通道
            sc = SocketChannel.open();
            // 5: 进行连接
            sc.connect(address);
            while (true) {
                // 6: 定义一个字节数组，然后使用系统录入的功能
                byte[] bytes = new byte[1024];
                System.in.read(bytes);
                // 7: 把数据放到缓冲区
                buf.put(bytes);
                // 8: 复位
                buf.flip();
                // 9: 写出数据
                sc.write(buf);
                // 10: 清空缓冲区数据
                buf.clear();
                // 11: 监听获取服务端返回数据
                read(buf, sc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                sc.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    /**
     * 读取服务端响应数据
     * @param buf
     * @param sc
     */
    private static void read(ByteBuffer buf, SocketChannel sc) {
        try {
            // 1: 读取数据
            int count = sc.read(buf);
            // 2: 如果没有数据
            if (count == -1) {
                sc.close();
                return;
            }
            // 3: 有数据则进行读取，读取之前需要进行复位方法（把position和limit进行复位）
            buf.flip();
            // 4: 根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
            byte[] bytes = new byte[buf.remaining()];
            // 5: 接收缓冲区数据
            buf.get(bytes);
            // 6: 打印结果
            String body = new String(bytes).trim();
            System.out.println("SocketServer resp: " + body);
            buf.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
