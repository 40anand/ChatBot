package com.docsapp.test.chatbot.core;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import android.os.Handler;
import com.docsapp.test.chatbot.model.ChatMessage;
import com.docsapp.test.chatbot.model.Response;
import com.docsapp.test.chatbot.network.NetworkUtil;
import static com.docsapp.test.chatbot.ui.ChatActivity.MESSAGE_NOT_RECEIVED;
import static com.docsapp.test.chatbot.ui.ChatActivity.MESSAGE_RECEIVED;

/**
 * A Runnable implementation for sending User's messages to the server on worker threads
 */
public class MessageSender implements Runnable {

    private final ChatMessage mChatMessage;
    private final WeakReference<Handler> mHandlerReference;

    public MessageSender(ChatMessage pChatMessage, Handler pHandler) {
        this.mChatMessage = pChatMessage;
        this.mHandlerReference = new WeakReference<>(pHandler);
    }

    @Override
    public void run() {
        Response response = NetworkUtil.sendMessage(mChatMessage);
        final Handler handler = mHandlerReference.get();
        if (handler != null) {
            android.os.Message message = handler.obtainMessage();
            if (response != null && response.getSuccess() != null && response.getSuccess() == 1 &&
                    response.getMessage() != null) {
                message.what = MESSAGE_RECEIVED;
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSender(ChatMessage.Sender.SENDER_BOT);
                chatMessage.setTimeStamp(Calendar.getInstance().getTimeInMillis());
                chatMessage.setMessage(response.getMessage().getMessage());
                message.obj = chatMessage;
            } else {
                message.what = MESSAGE_NOT_RECEIVED;
            }
            handler.sendMessage(message);
        }
    }
}
