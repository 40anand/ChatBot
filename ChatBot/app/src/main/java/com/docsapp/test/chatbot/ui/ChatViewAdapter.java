package com.docsapp.test.chatbot.ui;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.docsapp.test.chatbot.R;
import com.docsapp.test.chatbot.model.ChatMessage;


/**
 * Adapter for displaying message list
 */
public class ChatViewAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private final int MESSAGE_TYPE_USER = 101;
    private final int MESSAGE_TYPE_BOT = 102;

    private List<ChatMessage> mChatMessageList = new ArrayList<>();

    private LayoutInflater mLayoutInflater;

    public ChatViewAdapter(Context pContext) {
        mLayoutInflater = LayoutInflater.from(pContext);
    }

    /**
     * Add a new ChatMessage at the end of data set of this adapter,
     * and notify the data set observer for the same
     *
     * @param pChatMessage the ChatMessage object to be added
     */
    public void addNewChatItem(ChatMessage pChatMessage) {
        this.mChatMessageList.add(pChatMessage);
        notifyItemInserted(mChatMessageList.size() - 1);
    }

    /**
     * Display chats loaded from Db
     *
     * @param pMessageList the List of ChatMessage loaded from Db
     */
    public void addChatsFromDb(List<ChatMessage> pMessageList) {
        if (pMessageList == null || pMessageList.isEmpty()) {
            return;
        }
        if (mChatMessageList.isEmpty()) {
            mChatMessageList.addAll(pMessageList);
        } else {
            pMessageList.addAll(mChatMessageList);
            mChatMessageList = pMessageList;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        final ChatMessage chatMessage = mChatMessageList.get(position);
        switch (chatMessage.getSender()) {
        case SENDER_BOT:
            return MESSAGE_TYPE_BOT;
        case SENDER_USER:
            return MESSAGE_TYPE_USER;
        default:
            return 0;
        }
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
        case MESSAGE_TYPE_USER:
            view = mLayoutInflater.inflate(R.layout.chat_message_user, null);
            break;
        case MESSAGE_TYPE_BOT:
            view = mLayoutInflater.inflate(R.layout.chat_message_bot, null);
            break;
        default:
            break;
        }
        ChatViewHolder chatViewHolder = null;
        if (view != null) {
            chatViewHolder = new ChatViewHolder(view);
        }
        return chatViewHolder;
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        final ChatMessage chatMessage = mChatMessageList.get(position);
        holder.messageView.setText(chatMessage.getMessage());
    }

    @Override
    public int getItemCount() {
        return mChatMessageList.size();
    }

}
