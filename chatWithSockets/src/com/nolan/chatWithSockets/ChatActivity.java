package com.nolan.chatWithSockets;

import android.app.Activity;
import android.os.*;
import android.view.View;
import android.widget.*;
import com.nolan.websocket.client.WebSocketClient;
import com.nolan.websocket.client.WebSocketClientInterface;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatActivity extends Activity {

    private String publicName; // public name of user.

    WebSocketClient websocketClient; // WebSocket Client

    private Button btnSend;
    private EditText etMessage;

    private ListView lvMessages;
    ArrayAdapter<String> adapter;
    String[] userMessages = new String[] {};
    ArrayList<String> list = new ArrayList<String>(Arrays.asList(userMessages));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        getExtras(); // get extras from bundle.

        createWebSocketClient(); // create websocket client and connect.

        setupUserInterface(); // setup user interface.
    }

    private void getExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            publicName = extras.getString("publicName");
        }
    }

    public void createWebSocketClient() {
        websocketClient = new WebSocketClient();
        try {
            websocketClient.setPublicName(publicName);
            websocketClient.setupWebSocketClient();
        } catch (Exception e) {
            System.out.println("createWebSocketClient - Exception e: " + e);
        }
        websocketClient.manageListener(new WebSocketClientInterface() {
            @Override
            public void receivedMessage(String message) { // Manage commands received by WebSocket Connection.
                try {
                    if (message.equals("connect")) {
                        System.out.println("connect");
                    } else if (message.equals("ready")) {
                        System.out.println("ready");
                    } else if (message.equals("disconnect")) {
                        System.out.println("disconnect");
                    } else if (message.equals("error")) {
                        System.out.println("error");
                    } else if (message.contains("message")) {
                        System.out.println("message:  " + message);
                        try {
                            JSONObject json = new JSONObject(message);

                            Message msg = new Message();
                            msg.message = json.getString("publicName").toString() + ": " + json.getString("message").toString();
                            msg.start();
                        } catch (Exception e) {
                            System.out.println("handleMessage - Exception e: " + e);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("receivedMessage - Exception e: " + e);
                    System.out.println("[debug] message is: " + message);
                }
            }
        });
    }

    public class Message extends Thread {
        public String message;
        @Override
        public void run() {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        adapter.insert(message, adapter.getCount());
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        System.out.println("Message - runOnUiThread Exception e: " + e);
                    }
                }
            });
        }
    }

    private void setupUserInterface() {
        // Setup adapter for handling added messages to listview.
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        // Setup Listview for messages received and sent.
        lvMessages = (ListView) findViewById(R.id.listViewChat);
        lvMessages.setAdapter(adapter); // Assign adapter to ListView
        lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // Setup EditText for output messages of user.
        etMessage = (EditText) findViewById(R.id.etMessage);

        // Setup Button for sending messages.
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                websocketClient.broadcastMessage(message);
            }
        });
    }

}
