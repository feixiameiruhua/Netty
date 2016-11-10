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
		int port = 9999;
		if(args!=null && args.length>0){
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ServerSocket server = null;
		try {
			//等待接收客户端发送回的数据
			server = new ServerSocket(port);
			System.out.println("The time server is start in port : "+port);
			Socket socket = null;
			while(true){
				socket = server.accept();
				//开一个线程
				new Thread(new TimeServerHandler(socket)).start();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(server!=null){
				server.close();
				System.out.println("The time server colse");
				server = null;
			}
		}
	}
}
