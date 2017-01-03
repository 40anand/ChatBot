package com.docsapp.test.chatbot.core;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import android.content.Context;
import android.os.Handler;
import com.docsapp.test.chatbot.db.DbHelper;
import com.docsapp.test.chatbot.model.ChatMessage;
import com.docsapp.test.chatbot.model.Response;
import com.docsapp.test.chatbot.network.NetworkUtil;
import static com.docsapp.test.chatbot.ui.ChatActivity.MESSAGE_NOT_RECEIVED;
import static com.docsapp.test.chatbot.ui.ChatActivity.MESSAGE_RECEIVED;

/**
 * A Runnable implementation for sending User's messages to the server and also saving the messages in db on worker
 * threads
 */
public class MessageSender implements Runnable {

    private final ChatMessage mChatMessage;
    private final WeakReference<Handler> mHandlerReference;
    private final DbHelper mDbHelper;

    public MessageSender(ChatMessage pChatMessage, Handler pHandler, Context pContext) {
        this.mChatMessage = pChatMessage;
        this.mHandlerReference = new WeakReference<>(pHandler);
        this.mDbHelper = DbHelper.getInstance(pContext);
    }

    @Override
    public void run() {
        mDbHelper.saveChatInDb(mChatMessage);
        Response response = NetworkUtil.sendMessage(mChatMessage);
        ChatMessage serverMessage = null;
        final Handler handler = mHandlerReference.get();
        if (handler != null) {
            android.os.Message message = handler.obtainMessage();
            if (response != null && response.getSuccess() != null && response.getSuccess() == 1 &&
                    response.getMessage() != null) {
                message.what = MESSAGE_RECEIVED;
                serverMessage = new ChatMessage();
                serverMessage.setSender(ChatMessage.Sender.SENDER_BOT);
                serverMessage.setTimeStamp(Calendar.getInstance().getTimeInMillis());
                serverMessage.setMessage(response.getMessage().getMessage());
                message.obj = serverMessage;
            } else {
                message.what = MESSAGE_NOT_RECEIVED;
            }
            handler.sendMessage(message);
            if (serverMessage != null) {
                mDbHelper.saveChatInDb(serverMessage);
            }
        }
    }
}
