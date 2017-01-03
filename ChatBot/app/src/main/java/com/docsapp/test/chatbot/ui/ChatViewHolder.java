package com.docsapp.test.chatbot.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.docsapp.test.chatbot.R;

/**
 * The View Holder for Chat Messages
 */
public class ChatViewHolder extends RecyclerView.ViewHolder {

    TextView messageView;

    public ChatViewHolder(View itemView) {
        super(itemView);
        messageView = (TextView) itemView.findViewById(R.id.message);
    }
}
