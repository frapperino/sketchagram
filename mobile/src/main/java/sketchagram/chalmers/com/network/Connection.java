package sketchagram.chalmers.com.network;

import android.os.AsyncTask;
import android.os.StrictMode;

import com.google.gson.Gson;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.*;
import org.jivesoftware.smack.AccountManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.AMessage;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Emoticon;
import sketchagram.chalmers.com.model.Painting;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.TextMessage;

/**
 * Created by Olliver on 15-02-18.
 */
public class Connection{
    ConnectionConfiguration config;
    XMPPTCPConnection connection;
    AccountManager manager;
    ChatManager chatManager;
    List<Chat> chatList;

    public Connection(){
        config = new ConnectionConfiguration("83.254.68.47", 5222);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connection = new XMPPTCPConnection(config);
        chatList = new ArrayList<Chat>();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void connect(){
        try {
            SASLAuthentication.supportSASLMechanism("PLAIN");
            connection.connect();
            getChatManager().addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean b) {
                    if(!b) {
                        chatList.add(chat);
                        chat.addMessageListener(messageListener);
                    }
                }
            });
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }

    }
    private void disconnect(){
        try {
            connection.disconnect();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private ChatManager getChatManager(){
        if(connection.isConnected()) {
            return ChatManager.getInstanceFor(connection);
        }
        return null;
    }

    public void logout(){
        connect();
        disconnect();
        connection = new XMPPTCPConnection(config);

    }

    public Exception createAccount(String userName, String password) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params){
                try {
                    logout();
                    connect();
                    manager = AccountManager.getInstance(connection);
                    manager.supportsAccountCreation();
                    manager.createAccount(params[0].toString(), params[1].toString());
                    disconnect();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    return e;
                }
                return null;
            }
        };
        Exception e = null;
        try {
            e = (Exception)task.execute(userName, password).get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }
        return e;
    }

    public boolean login(final String userName, final String password){
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params){
                logout();
                connect();
                try {
                    connection.login(userName, password);
                    getChatManager().addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean b) {
                            if(!b){
                                chat.addMessageListener(messageListener);
                            }
                        }
                    });
                } catch (XMPPException e) {
                    e.printStackTrace();
                    return false;
                } catch (SmackException e) {
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        };
        boolean success = false;
        try {
            success = (boolean)task.execute(userName, password).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return success;
    }

    public void createConversation(ADigitalPerson recipient){
        chatManager = getChatManager();
        chatList.add(chatManager.createChat(recipient.getUsername(), messageListener));
    }

    public void sendMessage(AMessage aMessage, String type) {
        Message message = new Message();
        message.setLanguage(type);
        message.setBody(new Gson().toJson(aMessage));
        for(Chat c : chatList) {
            for(ADigitalPerson recipient : aMessage.getRECEIVER()){
                if(c.getParticipant().equals(recipient.getUsername())) {
                    try {
                        c.sendMessage(message);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private static MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, Message message) {
            List<Conversation> conversationList = SystemUser.getInstance().getUser().getConversationList();
            for(Conversation c : conversationList){
                boolean same = true;
                for(ADigitalPerson person : c.getParticipants()){
                    if(!chat.getParticipant().equals(person.getUsername())){
                        same = false;
                    }
                }
                if(same){
                    Gson gson = new Gson();
                    AMessage aMessage = null;
                    switch (message.getLanguage()) {
                        case "Painting":
                            aMessage = gson.fromJson(message.getBody(), Painting.class);
                            break;
                        case "TextMessage":
                            aMessage = gson.fromJson(message.getBody(), TextMessage.class);
                            break;
                        case "Emoticon":
                            aMessage = gson.fromJson(message.getBody(), Emoticon.class);
                            break;
                    }

                    aMessage = gson.fromJson(message.getBody(), AMessage.class);
                    c.addMessage(aMessage);
                    System.out.println(aMessage.getMessage());
                }
            }
        }
    };
}

