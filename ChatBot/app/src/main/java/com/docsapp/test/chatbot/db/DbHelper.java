package com.docsapp.test.chatbot.db;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import com.docsapp.test.chatbot.model.ChatMessage;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat_db";
    private static final int DATABASE_VERSION = 1;

    private final String CHAT_TABLE_NAME = "chat_bot";

    /**
     * Unique id associated with every chat
     */
    private final String COLUMN_ID = "id";
    /**
     * Time Stamp of the Chat Message
     */
    private final String COLUMN_TIMESTAMP = "time_stamp";
    /**
     * Whether the message was sent by user or bot;
     */
    private final String COLUMN_SENDER = "sender";
    /**
     * The message sent
     */
    private final String COLUMN_MESSAGE = "message";

    private static DbHelper sInstance;

    private final String CREATE_CHAT_TABLE_STATEMENT =
            "create table " + CHAT_TABLE_NAME + " (" + COLUMN_ID + " integer primary key autoincrement," +
                    COLUMN_TIMESTAMP + " long not null," + COLUMN_SENDER + " integer not null," + COLUMN_MESSAGE +
                    " text not null" + " )";


    public static DbHelper getInstance(Context pContext) {
        if (sInstance == null) {
            synchronized (DbHelper.class) {
                if (sInstance == null) {
                    sInstance = new DbHelper(pContext);
                }
            }
        }
        return sInstance;
    }

    /**
     * Private Constructor for Singleton class
     *
     * @param pContext the context
     */
    private DbHelper(Context pContext) {
        super(pContext, DATABASE_NAME, new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase pDb, SQLiteCursorDriver pMasterQuery, String pEditTable,
                                    SQLiteQuery pQuery) {
                return new SQLiteCursor(pMasterQuery, pEditTable, pQuery);
            }
        }, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHAT_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Insert a chat into db
     *
     * @param pChatMessage The ChatMessage Object
     */
    public void saveChatInDb(ChatMessage pChatMessage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TIMESTAMP, pChatMessage.getTimeStamp());
        contentValues.put(COLUMN_SENDER, pChatMessage.getSender().value);
        contentValues.put(COLUMN_MESSAGE, pChatMessage.getMessage());
        db.insert(CHAT_TABLE_NAME, null, contentValues);
    }

    /**
     * Retrieve the ChatMessages stored in db and return them
     *
     * @return The Messages stored in db
     */
    public List<ChatMessage> getChatsFromDb() {
        List<ChatMessage> messageList = new ArrayList<>();
        Cursor cursor = getReadableDatabase()
                .query(CHAT_TABLE_NAME, new String[] { COLUMN_TIMESTAMP, COLUMN_SENDER, COLUMN_MESSAGE }, null, null,
                       null, null, COLUMN_TIMESTAMP + " asc");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setTimeStamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                chatMessage.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)));
                int senderType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENDER));
                if (senderType == 0) {
                    chatMessage.setSender(ChatMessage.Sender.SENDER_USER);
                } else {
                    chatMessage.setSender(ChatMessage.Sender.SENDER_BOT);
                }
                messageList.add(chatMessage);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return messageList;
    }
}
