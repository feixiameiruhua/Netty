package com.fwmagic.nio;

/**
 * 
 * @author fwmagic
 * @date 2016年11月10日 下午1:58:42 
 * @Description: 服务端
 */
public class TimeServer {
	public static void main(String[] args) {
		int port =9999;
		if(args!=null && args.length>0){
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
		new Thread(timeServer,"NIO-MultiplexerTimeServer-001").start();
	}
}
