package com.docsapp.test.chatbot.ui;

import java.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.docsapp.test.chatbot.R;
import com.docsapp.test.chatbot.model.ChatMessage;

/**
 * Main Activity, Chat messages will be shown here, and sending and receiving message will be handled
 */
public class ChatActivity extends AppCompatActivity {

    private ChatViewAdapter mChatViewAdapter;
    private EditText mMessageText;
    private ImageView mSendButton;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
    }

    /**
     * Initialize the views and attached required Event Listeners
     */
    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.chatList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mChatViewAdapter = new ChatViewAdapter(this);
        mRecyclerView.setAdapter(mChatViewAdapter);

        mMessageText = (EditText) findViewById(R.id.chatText);

        mSendButton = (ImageView) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(mSendMessageClickListener);
    }


    /**
     * Send Button Click Listener
     */
    private final View.OnClickListener mSendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = mMessageText.getText().toString().trim();
            if (!message.isEmpty()) {
                displayMessage(message, ChatMessage.Sender.SENDER_USER);
                mMessageText.setText("");
            } else {
                Toast.makeText(ChatActivity.this, "Message is Empty", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Display the message in Message List, A new ChatMessage Object will be added in the data set of
     * adapter, and adapter will be notified for new data
     *
     * @param pMessage the message
     * @param pSender  the sender i.e. user or bot
     */
    private void displayMessage(String pMessage, ChatMessage.Sender pSender) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(pMessage);
        chatMessage.setSender(pSender);
        chatMessage.setTimeStamp(Calendar.getInstance().getTimeInMillis());
        mChatViewAdapter.addNewChatItem(chatMessage);
        scrollToLastMessage();
    }

    /**
     * Scroll the message list till last message
     */
    private void scrollToLastMessage() {
        final int count = mRecyclerView.getAdapter().getItemCount();
        if (count >= 1) {
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.smoothScrollToPosition(count - 1);
                }
            }, 100);
        }
    }
}
