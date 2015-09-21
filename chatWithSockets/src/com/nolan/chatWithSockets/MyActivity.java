package com.nolan.chatWithSockets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class MyActivity extends Activity {

    private Button btnBeginChat;
    private EditText etPublicName;
    private TextView tvPublicName;

    private String publicName = "User_" + (new Random()).nextInt(100);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setupUserInterface();
    }


    private void setupUserInterface() {
        // Setup TextView for public name to display.
        tvPublicName = (TextView) findViewById(R.id.tvPublicName);
        tvPublicName.setText("Public name: " + publicName);

        // Setup EditText for user input for public name.
        etPublicName = (EditText) findViewById(R.id.etPublicName);
        etPublicName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                publicName = s.toString();
                tvPublicName.setText("Public name: " + publicName);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup Button that begins chat and takes public name for new intent.
        btnBeginChat = (Button) findViewById(R.id.btnBeginChat);
        btnBeginChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyActivity.this, ChatActivity.class);
                intent.putExtra("publicName", publicName);
                startActivity(intent);
            }
        });

    }

}
