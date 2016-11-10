package com.fwmagic.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author fwmagic
 * @date 2016年11月10日 下午2:00:03
 * @Description:
 */
public class MultiplexerTimeServer implements Runnable {

	//多路复用器
	private Selector selector;

	//服务端套接字通道
	private ServerSocketChannel servChannel;

	private volatile boolean stop;

	/**
	 * 
	 * @author fwmagic
	 * @date 2016年11月10日 下午2:11:55
	 * @Description: 初始化多路复用器，绑定监听端口
	 * @param port
	 */
	public MultiplexerTimeServer(int port) {
		try {
			selector = Selector.open();//创建选择器
			servChannel = ServerSocketChannel.open();//创建服务端套接字通道
			servChannel.configureBlocking(false);//配置为异步非阻塞模式
			servChannel.socket().bind(new InetSocketAddress(port), 1024);//服务端绑定端口号
			servChannel.register(selector, SelectionKey.OP_ACCEPT);//向选择器中注册服务端通道
			System.out.println("The time server is start in port : " + port);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);//非正常结束正在运行中的java虚拟机 eg:System.exit(0);正常结束
		}
	}

	public void stop() {
		this.stop = true;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				selector.select(1000);//在等待某个通道准备就绪时最多阻塞 1秒
				//返回此选择器的已选择键集。 
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectedKeys.iterator();
				SelectionKey key = null;//选择键
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
								;
							}
						}
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		// 多路复用器关闭后，所有注册在上面deChannel和Pipe等资源都会被自动去注册并关闭，，所以不需要重复释放资源
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {//选择键有效
			// 已准备好新接入的请求信息
			if (key.isAcceptable()) {
				// Accept the new connection
				//返回为之创建此键的通道
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				//接收一个客户端通道
				SocketChannel sc = ssc.accept();
				sc.configureBlocking(false);//配置为异步非阻塞模式
				// Add the new connection to the selector
				//向选择器中注册客户端通道
				sc.register(selector, SelectionKey.OP_READ);
			}
			//读取客户端的请求信息
			if (key.isReadable()) {//通道是否准备好读取信息
				// Read the data
				//返回为之创建此键的通道
				SocketChannel sc = (SocketChannel) key.channel();
				//分配一个1MB的缓冲区
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int readBytes = sc.read(readBuffer);
				if (readBytes > 0) {
					readBuffer.flip();//the position is set to zero
					byte[] bytes = new byte[readBuffer.remaining()];//剩余元素数
					readBuffer.get(bytes);//将缓冲区可读的字节数组复制到新创建的字节数组中
					String body = new String(bytes, "UTF-8");
					System.out.println("The time server receive order : "
							+ body);
					String currentTime = "QUERY TIME ORDER"
							.equalsIgnoreCase(body) ? new SimpleDateFormat(
							"yyyy-MM-dd : HH:mm:ss").format(new Date())
							: "BAD ORDER";
					doWrite(sc, currentTime);
				} else if (readBytes < 0) {
					// 对端链路关闭
					key.cancel();
					sc.close();
				} else {
					;// 读到0字节，忽略
				}
			}
		}
	}

	/**
	 * 
	 * @author fwmagic
	 * @date 2016年11月10日 下午2:40:05 
	 * @Description: 将应答消息异步发送给客户端
	 * @param channel
	 * @param response
	 * @throws IOException
	 */
	private void doWrite(SocketChannel channel, String response)
			throws IOException {
		if (response != null && response.trim().length() > 0) {
			byte[] bytes = response.getBytes();
			ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
			writeBuffer.put(bytes);//将字节数组复制到缓冲区
			//将limit的值设为position的当前值，再将position的值设为0。这个操作可以通过这个flip()方法实现。
			writeBuffer.flip();
			channel.write(writeBuffer);
		}
	}

}
