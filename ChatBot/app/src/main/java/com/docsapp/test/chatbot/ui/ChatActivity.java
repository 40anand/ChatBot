package com.docsapp.test.chatbot.ui;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.docsapp.test.chatbot.R;
import com.docsapp.test.chatbot.core.MessageSender;
import com.docsapp.test.chatbot.db.DbHelper;
import com.docsapp.test.chatbot.model.ChatMessage;

/**
 * Main Activity, Chat messages will be shown here, and sending and receiving message will be handled
 */

@SuppressWarnings("unchecked")
public class ChatActivity extends AppCompatActivity {

    public static final int MESSAGE_RECEIVED = 100;
    public static final int MESSAGE_NOT_RECEIVED = 101;

    private ChatViewAdapter mChatViewAdapter;
    private EditText mMessageText;
    private RecyclerView mRecyclerView;

    /**
     * Interface for communicating with ChatLoaderTask
     */
    private interface ActivityCallBack {
        void onLoadFinished(List<ChatMessage> pMessageList);
    }

    private final ActivityCallBack mActivityCallBack = new ActivityCallBack() {
        @Override
        public void onLoadFinished(List<ChatMessage> pMessageList) {
            mChatViewAdapter.addChatsFromDb(pMessageList);
        }
    };

    /**
     * Async task for loading chats from db
     */
    private static class ChatLoaderTask extends AsyncTask {

        private final WeakReference<ActivityCallBack> mActivityCallBackReference;
        private final DbHelper mDbHelper;
        private List<ChatMessage> mMessageList;

        ChatLoaderTask(ActivityCallBack pActivityCallBack, Context pContext) {
            mActivityCallBackReference = new WeakReference<>(pActivityCallBack);
            mDbHelper = DbHelper.getInstance(pContext);
        }

        @Override
        protected Object doInBackground(Object[] params) {
            mMessageList = mDbHelper.getChatsFromDb();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            final ActivityCallBack activityCallBack = mActivityCallBackReference.get();
            if (activityCallBack != null) {
                activityCallBack.onLoadFinished(mMessageList);
            }
        }
    }

    /**
     * A Fixed Thread Pool Executor for Sending message to server,
     * having pool size of 4
     */
    private final ExecutorService mThreadPoolExecutor = Executors.newFixedThreadPool(4);

    /**
     * Handler , it will be used for sending message to this activity by a worker thread, after sending
     * a message to server and getting response
     */
    private final Handler mChatResponseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_RECEIVED:
                ChatMessage chatMessage = (ChatMessage) msg.obj;
                displayMessage(chatMessage);
                break;
            case MESSAGE_NOT_RECEIVED:
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        loadChatsFromDbInBackground();
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

        mRecyclerView.addOnLayoutChangeListener(mRecyclerViewLayoutChangeListener);

        mMessageText = (EditText) findViewById(R.id.chatText);

        ImageView sendButton = (ImageView) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(mSendMessageClickListener);
    }

    private void loadChatsFromDbInBackground() {
        ChatLoaderTask chatLoaderTask = new ChatLoaderTask(mActivityCallBack, this);
        chatLoaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    /**
     * LayoutChangeListener for scrolling the Message List Up when Keyboard is shown.
     */
    private final View.OnLayoutChangeListener mRecyclerViewLayoutChangeListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                   int oldRight, int oldBottom) {
            if (bottom < oldBottom) {
                scrollToLastMessage();
            }
        }
    };

    /**
     * Send Button Click Listener
     */
    private final View.OnClickListener mSendMessageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = mMessageText.getText().toString().trim();
            if (!message.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(message);
                chatMessage.setSender(ChatMessage.Sender.SENDER_USER);
                chatMessage.setTimeStamp(Calendar.getInstance().getTimeInMillis());
                /*Display the message on UI*/
                displayMessage(chatMessage);
                /*Submit a new Runnable to Thread pool Executor, for sending the current message*/
                mThreadPoolExecutor.submit(new MessageSender(chatMessage, mChatResponseHandler, ChatActivity.this));
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
     * @param pChatMessage the message
     */
    private void displayMessage(ChatMessage pChatMessage) {
        mChatViewAdapter.addNewChatItem(pChatMessage);
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
