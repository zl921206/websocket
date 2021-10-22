package socket.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AIO，异步非阻塞IO
 */
public class AioSocketServer {
    /**
     * 线程池
     */
    private ExecutorService executorService;
    /**
     * 线程组
     */
    private AsynchronousChannelGroup threadGroup;
    /**
     * 服务器通道
     */
    private AsynchronousServerSocketChannel assc;

    public AioSocketServer(int port) {
        try {
            // 创建一个缓存线程池
            executorService = Executors.newCachedThreadPool();
            // 创建线程组
            threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
            // 创建服务端通道
            assc = AsynchronousServerSocketChannel.open(threadGroup);
            // 绑定
            assc.bind(new InetSocketAddress(port));
            System.out.println("aio socketServer start，port: " + port);
            // 进行阻塞
            assc.accept(this, new CompletionHandler<AsynchronousSocketChannel, AioSocketServer>() {
                @Override
                public void completed(AsynchronousSocketChannel asc, AioSocketServer attachment) {
                    // 当有下一个客户端接入的时候，直接调用Server的accept方法，这样反复执行下去，保证多个客户端都可以阻塞
                    attachment.assc.accept(attachment, this);
                    read(asc);
                }
                @Override
                public void failed(Throwable exc, AioSocketServer attachment) {

                }
            });
            // 阻塞不让服务器停止
            Thread.sleep(10000);    // 10秒钟
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取数据
     * @param asc
     */
    private void read(final AsynchronousSocketChannel asc){
        // 读取数据
        ByteBuffer buf = ByteBuffer.allocate(1024);
        asc.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer resultSize, ByteBuffer attachment) {
                // 读取之后，重新标识
                attachment.flip();
                // 获取读取的字节数
                System.out.println("Server -> 收到客户端的数据长度为： " + resultSize);
                // 获取读取的数据
                String resultData = new String(attachment.array()).trim();
                System.out.println("Server -> 收到客户端发过来的数据： " + resultData);
                // 响应客户端
                String response = "服务器响应，收到客户端发过来的数据： " + resultData;
                write(asc, response);
            }
            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
    }

    /**
     * 写入数据
     * @param asc
     * @param response
     */
    private void write(AsynchronousSocketChannel asc, String response){
        try {
            ByteBuffer buf = ByteBuffer.allocate(1024);
            buf.put(response.getBytes());
            buf.flip();
            asc.write(buf).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AioSocketServer(5588);
    }
}
