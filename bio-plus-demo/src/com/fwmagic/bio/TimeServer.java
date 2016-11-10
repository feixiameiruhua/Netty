package com.fwmagic.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author root
 *
 */
public class TimeServer {

	public static void main(String[] args) throws IOException {
		int port = 9998;
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ServerSocket server = null;
		try {
			// 等待接收客户端发送回的数据
			server = new ServerSocket(port);
			System.out.println("The time server is start in port : " + port);
			Socket socket = null;
			// 创建I/O任务线程池
			TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(
					50, 10000);
			while (true) {
				socket = server.accept();
				singleExecutor.execute(new TimeServerHandler(socket));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				server.close();
				System.out.println("The time server colse");
				server = null;
			}
		}
	}
}
