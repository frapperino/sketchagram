package sketchagram.chalmers.com.network;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.*;
import org.jivesoftware.smack.AccountManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.AMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Emoticon;
import sketchagram.chalmers.com.model.Painting;
import sketchagram.chalmers.com.model.SystemUser;
import sketchagram.chalmers.com.model.TextMessage;
import sketchagram.chalmers.com.sketchagram.MainActivity;

/**
 * Created by Olliver on 15-02-18.
 */
public class Connection extends Service{
    ConnectionConfiguration config;
    XMPPTCPConnection connection;
    AccountManager manager;
    List<Chat> chatList;
    private final IBinder binder = new Binder();

    public Connection() {
        super();
    }

    @Override
    public void onCreate(){
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void init(){
        //SmackAndroid.init()
        config = new ConnectionConfiguration("83.254.68.47", 5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connection = new XMPPTCPConnection(config);
        chatList = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        SASLAuthentication.supportSASLMechanism("PLAIN");
        connect();
    }

    private void connect(){
        try {
            if(!connection.isConnected()){
                connection.connect();
            }
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }

    }
    private void disconnect(Presence presence){
        try {
            if (presence != null){
                connection.disconnect(presence);
            }else {
                connection.disconnect();
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private ChatManager getChatManager(){
        ChatManager chatManager = null;
        if(connection.isConnected()) {
            chatManager = ChatManager.getInstanceFor(connection);
        }
        return chatManager;
    }

    public void logout(){
        Presence presence = new Presence(Presence.Type.unavailable);
        presence.setMode(Presence.Mode.away);
        if(connection.isConnected()){
            disconnect(presence);
            init();
        }

    }

    public Exception createAccount(String userName, String password) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params){
                try {
                        if(connection.isConnected()){
                        manager = AccountManager.getInstance(connection);
                        manager.createAccount(params[0].toString(), params[1].toString());
                    }
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
                try {
                    if(connection.isConnected()){
                        connection.login(userName, password);
                        getChatManager().addChatListener(new ChatManagerListener() {
                            @Override
                            public void chatCreated(Chat chat, boolean b) {
                                if(!b){
                                    chatList.add(chat);
                                    chat.addMessageListener(messageListener);
                                }
                            }
                        });
                       }
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
        ChatManager chatManager = getChatManager();
        Chat chat = chatManager.createChat(recipient.getUsername()+ "@raspberrypi", messageListener);
        chatList.add(chat);
    }

    public void sendMessage(AMessage aMessage, String type) {
        Message message = new Message();
        switch (type){
            case "TextMessage":
                NetworkMessage<String> networkMessage = new NetworkMessage<>();
                networkMessage.convertToNetworkMessage(aMessage);
                message.setLanguage(type);
                Gson gson = new Gson();
                String object = gson.toJson(networkMessage);
                message.setBody(object);
                sendMessageToContacts(networkMessage, message);

        }

    }
    private void sendMessageToContacts(NetworkMessage networkMessage,  Message message){
        for(Chat c : chatList) {
            List<String> receivers = networkMessage.getReceivers();
            for(Object recipient : receivers){
                if(c.getParticipant().split("@")[0].equals(recipient.toString())) {
                    try {
                        c.sendMessage(message);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, Message message) {
            List<Conversation> conversationList = SystemUser.getInstance().getUser().getConversationList();
            for(Conversation c : conversationList){
                boolean same = true;
                for(ADigitalPerson person : c.getParticipants()){
                    if(!chat.getParticipant().split("@")[0].equals(person.getUsername())){
                        if(!SystemUser.getInstance().getUser().getUsername().equals(person.getUsername())){
                            same = false;
                        }
                    }
                }
                if(same){
                    Gson gson = new Gson();
                    c.addMessage(getMessage(message.getBody(), message.getLanguage()));
                }else{
                    String participant = chat.getParticipant();
                    List<ADigitalPerson> participants = new ArrayList<ADigitalPerson>();
                    for(Contact contact : SystemUser.getInstance().getUser().getContactList()){
                        if(contact.getUsername().equals(participant.split("@")[0])){
                            participants.add(contact);
                        }
                    }

                    participants.add(SystemUser.getInstance().getUser());

                    Conversation con = new Conversation(participants);
                    SystemUser.getInstance().getUser().addConversation(con);
                    con.addMessage(getMessage(message.getBody(), message.getLanguage()));
                }
            }
        }
    };

    private AMessage getMessage(String body, String type){
        AMessage message = null;
        Gson gson = new Gson();
        switch (type) {
            case "Painting":
                message = gson.fromJson(body, Painting.class);
                break;
            case "TextMessage":
                NetworkMessage<String> networkMessage = gson.fromJson(body, NetworkMessage.class);
                TextMessage textMessage = (TextMessage)networkMessage.convertFromNetworkMessage(type);
                System.out.println(textMessage.getMessage());
                return textMessage;
            case "Emoticon":
                message = gson.fromJson(body, Emoticon.class);
                break;
            default:
                message = gson.fromJson(body, AMessage.class);
                break;
        }
        return message;
    }
}

