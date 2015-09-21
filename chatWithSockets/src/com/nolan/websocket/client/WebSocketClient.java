package com.nolan.websocket.client;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;

public class WebSocketClient {

	protected static final String LOGC = "WebSocketClient";

	private String publicName;
	public void setPublicName(String publicName) {
		this.publicName = publicName;
	}

	public Socket socket; // socket client.
	private String sSocketId; // WebSocket User Socket Id.

	public WebSocketClientInterface wscInterface;

	public void manageListener(WebSocketClientInterface w) {
		this.wscInterface = w;
	}

	public void setupWebSocketClient() throws Exception {
		System.out.println("Starting websocket client...");

		socket = IO.socket("http://192.168.15.4:1337"); // can't use localhost or 127.0.0.1

		socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				wscInterface.receivedMessage("connect");
				try {
					JSONObject user = new JSONObject();
					user.put("publicName", publicName);

					socket.emit("new user", user);
					System.out.println("sent 'new user' with user data.");
				} catch (Exception e) {
					System.out.println("Socket.Event_Connect call - Exception e: " + e);
				}
			}
		}).on("ready", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				wscInterface.receivedMessage("ready"); // ready to chat.
			}
		}).on("new user", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				wscInterface.receivedMessage("new user");
			}
		}).on("disconnect", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				wscInterface.receivedMessage("disconnect"); // other user disconnect
			}

		}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				wscInterface.receivedMessage("error"); // self disconnect
			}

		}).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				wscInterface.receivedMessage("error"); // error.
			}
		});
		socket.connect(); // Start connecting.
	}

	public void broadcastMessage(String s) {
		try {
			JSONObject message = new JSONObject();

			message.put("publicName", publicName);
			message.put("message", s);

			socket.emit("message", message);
			System.out.println("sent broadcast message.");
		} catch (Exception e) {
			System.out.println("broadcastMessage - Exception e: " + e);
		}
	}

}
