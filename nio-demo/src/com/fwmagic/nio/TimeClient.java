package com.fwmagic.nio;

/**
 * 
 * @author fwmagic
 * @date 2016年11月10日 下午3:51:12 
 * @Description:客户端通道
 */
public class TimeClient {
	public static void main(String[] args) {
		int port =9999;
		if(args!=null && args.length>0){
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		new Thread(new TimeClientHandle("127.0.0.1",port),"TimeClient-001").start();
	}
}
