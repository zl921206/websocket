package socket.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

/**
 * socket 客户端
 */
public class AioSocketClient implements Runnable {

    /**
     * 定义客户端socket通道
     */
    private AsynchronousSocketChannel asc;

    // 定义 IP
    private final String address = "localhost";
    // 定义 port
    private final int port = 5588;

    /**
     * 使用构造方法打开通道
     *
     * @throws IOException
     */
    public AioSocketClient() throws IOException {
        asc = AsynchronousSocketChannel.open();
    }

    /**
     * 建立连接
     */
    public void connect() {
        asc.connect(new InetSocketAddress(address, port));
    }

    /**
     * 写入数据
     * @param request
     */
    public void write(String request) {
        try {
            asc.write(ByteBuffer.wrap(request.getBytes())).get();
            read();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取数据
     */
    private void read() {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        try {
            asc.read(buf).get();
            buf.flip();
            byte[] respByte = new byte[buf.remaining()];
            buf.get();
            System.out.println("响应数据：" + new String(buf.array(), "utf-8").trim());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    /**
     * 有时调用执行main方法，会出现异常：java.nio.channels.NotYetConnectedException
     * 这是由于以下connect为异步方法，调用connect时，可能并没有成功建立连接导致
     * @param args
     */
    public static void main(String[] args) {
        try {
            int i = 1;
            while(i <= 100){
                AioSocketClient asc = new AioSocketClient();
                asc.connect();
//                Thread.sleep(500);
                String str = "client..." + i;
                new Thread(asc, str).start();
                asc.write(str + "，request......");
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
