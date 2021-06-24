package socket.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 实现Runnable接口是为了注册到Selector中一直处于轮询的状态
 * NIO，同步非阻塞IO
 */
public class NioSocketServer implements Runnable {

    /**
     * 1：多路复用器：管理所有的通道
     */
    private Selector selector;

    /**
     * 2：建立缓冲区
     */
    private ByteBuffer buf = ByteBuffer.allocate(1024);

    public NioSocketServer(int port) {
        try {
            // 1: 开启多路复用器
            this.selector = Selector.open();
            // 2: 打开服务端通道
            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 3: 设置服务端通道为非阻塞模式
            ssc.configureBlocking(false);
            // 4: 绑定地址
            ssc.bind(new InetSocketAddress(port));
            // 5: 把服务端通道注册到多路复用器上，并且监听阻塞事件
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("nio socketServer start，port: " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("执行run()......");
        while (true) {
            try {
                // 1: 让多路复用器开始监听
                this.selector.select();
                // 2: 返回多路复用器已经选择的结果集
                Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
                // 3: 进行响应
                while (keys.hasNext()) {
                    // 4: 获取一个选择元素
                    SelectionKey key = keys.next();
                    // 5: 直接从容器中移除
                    keys.remove();
                    // 6: 如果key有效
                    if (key.isValid()) {
                        // 7: 如果为阻塞状态
                        if (key.isAcceptable()) {
                            this.accept(key);   // 这里的key就是服务器端的Channel的key
                        }
                        // 8: 如果为可读状态
                        if (key.isReadable()) {
                            this.read(key);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取客户端请求数据
     *
     * @param key
     */
    private void read(SelectionKey key) {
        try {
            // 1: 清空缓冲区旧的数据
            this.buf.clear();
            // 2: 获取之前注册的socket通道对象
            SocketChannel sc = (SocketChannel) key.channel();
            // 3: 读取数据
            int count = sc.read(this.buf);
            // 4: 如果没有数据
            if (count == -1) {
                key.channel().close();
                key.cancel();
                return;
            }
            // 5: 有数据则进行读取，读取之前需要进行复位方法（把position和limit进行复位）
            this.buf.flip();
            // 6: 根据缓冲区的数据长度创建相应大小的byte数组，接收缓冲区的数据
            byte[] bytes = new byte[this.buf.remaining()];
            // 7: 接收缓冲区数据
            this.buf.get(bytes);
            // 8: 打印结果
            String body = new String(bytes).trim();
            System.out.println("SocketClient req: " + body);
            // 9: 写回给客户端
            buf.clear();
            String resp = "客户端你好，已收到消息（" + body + "）";
            buf.put(resp.getBytes());
            buf.flip();
            sc.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 阻塞处理
     *
     * @param key
     */
    private void accept(SelectionKey key) {
        try {
            // 1: 获取服务端通道
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            // 2: 执行客户端Channel的阻塞方法
            SocketChannel sc = ssc.accept();
            // 3: 设置阻塞模式
            sc.configureBlocking(false);
            // 4: 注册到多路复用器上，并设置读取标识
            sc.register(this.selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(
                new NioSocketServer(5588)
        ).start();
    }
}
