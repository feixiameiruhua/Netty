package com.fwmagic.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServerHandler implements Runnable {

	private Socket socket;

	public TimeServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
			out = new PrintWriter(this.socket.getOutputStream(), true);
			String currentTime = null;
			String body = null;
			while (true) {
				body = in.readLine();
				if (body == null)
					break;
				System.out.println("This time server receive order :" + body);
				currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date()) : "BAD ORDER";
				out.println(currentTime);
			}
		} catch (Exception e) {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (this.socket != null) {
				try {
					this.socket.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				this.socket = null;
			}
		}
	}

}
