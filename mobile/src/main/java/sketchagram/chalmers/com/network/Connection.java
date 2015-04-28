package sketchagram.chalmers.com.network;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.tcp.*;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import sketchagram.chalmers.com.model.ADigitalPerson;
import sketchagram.chalmers.com.model.ClientMessage;
import sketchagram.chalmers.com.model.Contact;
import sketchagram.chalmers.com.model.Conversation;
import sketchagram.chalmers.com.model.Drawing;
import sketchagram.chalmers.com.model.MessageType;
import sketchagram.chalmers.com.model.Profile;
import sketchagram.chalmers.com.model.Status;
import sketchagram.chalmers.com.sketchagram.MyApplication;
import sketchagram.chalmers.com.model.User;

/**
 * Created by Olliver on 15-02-18.
 */
public class Connection implements IConnection{
    private ConnectionConfiguration config;
    private XMPPTCPConnection connection;
    private AccountManager manager;
    private List<Chat> chatList;
    private List<MultiUserChat> groupChatList;
    private final String HOST = "sketchagram.ollivermattsson.se";
    private final String DOMAIN = "@sketchagram";
    private final String GROUP = "Friends";
    private static Connection myInstance;
    private static boolean loggedIn;


    //private final IBinder binder = new Binder();

    private Connection() {
        super();
        init();
    }

    public static Connection getInstance(){
        if(myInstance == null){
            myInstance = new Connection();
        }

        return myInstance;

    }
    private void init(){
        //SmackAndroid.init()
        config = new ConnectionConfiguration(HOST, 5222);
        config.setReconnectionAllowed(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connection = new XMPPTCPConnection(config);
        chatList = new ArrayList<>();
        groupChatList = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        SASLAuthentication.supportSASLMechanism("PLAIN", 0);
        connection.addPacketListener(requestListener, new PacketFilter() {
            @Override
            public boolean accept(Packet packet) {
                if (packet instanceof Presence) {
                    Presence presence = (Presence) packet;
                    if (presence.getType().equals(Presence.Type.subscribed)
                            || presence.getType().equals(
                            Presence.Type.subscribe)
                            || presence.getType().equals(
                            Presence.Type.unsubscribed)
                            || presence.getType().equals(
                            Presence.Type.unsubscribe)) {
                        return true;
                    }
                }
                return false;
            }
        });
        getChatManager().addChatListener(chatManagerListener);

        getRoster().addRosterListener(rosterListener);
        connect();
        Roster roster = connection.getRoster();
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);//TODO: change to manual accept

    }

    private void connect() {
        try {
            if(!connection.isConnected()){
                SASLAuthentication.supportSASLMechanism("PLAIN", 0);
                connection.connect();
            }
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
        }
    }

    private void disconnect(final Presence presence){
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    if (presence != null){
                        while(connection.isConnected()) {
                            connection.sendPacket(presence);
                            try {
                                Thread.sleep((long)2);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            getChatManager().removeChatListener(chatManagerListener);
                            getRoster().removeRosterListener(rosterListener);
                            connection.removePacketListener(requestListener);
                            connection.disconnect();

                        }
                        resetService();
                    }else {
                        while(connection.isConnected()) {
                            getChatManager().removeChatListener(chatManagerListener);
                            getRoster().removeRosterListener(rosterListener);
                            connection.removePacketListener(requestListener);
                            connection.disconnect();

                        }
                        resetService();
                    }
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute();

    }

    private void reset(){
        getChatManager().removeChatListener(chatManagerListener);
        getRoster().removeRosterListener(rosterListener);
        connection.removePacketListener(requestListener);
        resetService();
    }

    private void resetService(){
        connection = null;
        myInstance = null;
        MyApplication.getInstance().stopNetworkService();
        try {
            Thread.sleep((long)5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MyApplication.getInstance().startNetworkService();
    }

    private ChatManager getChatManager(){
        ChatManager chatManager;
        if(connection.isConnected()) {
            chatManager = ChatManager.getInstanceFor(connection);
        }else {
            connect();
            chatManager = ChatManager.getInstanceFor(connection);
        }
        return chatManager;
    }

    private Roster getRoster(){
        return connection.getRoster();
    }

    public void logout(){
        Presence presence = new Presence(Presence.Type.unavailable);
        presence.setMode(Presence.Mode.away);
        if(loggedIn && connection != null) {
            if (connection.isConnected()) {
                disconnect(presence);
                loggedIn = false;
            }
        }

    }

    public void createAccount(String userName, String password) throws NetworkException.UsernameAlreadyTakenException{
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
                    return new NetworkException.ServerNotRespondingException(e.getMessage());
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
        if(e != null) {
            switch(e.getMessage().toString()) {
                case "conflict":
                    throw new NetworkException.UsernameAlreadyTakenException(e.getMessage());
            }
        }
    }

    public boolean login(final String userName, final String password){
        if(loggedIn){return true;}
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params){
                try {
                    if(connection == null){
                        return false;
                    }
                    if(connection.isConnected()){
                        connection.login(userName, password);
                    }
                } catch (XMPPException e) {
                    e.printStackTrace();
                    disconnect(null);
                    return false;
                } catch (SmackException e) {
                    e.printStackTrace();
                    disconnect(null);
                    return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnect(null);
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
        if(success){
            loggedIn = true;
        }
        return success;
    }

    public void updateUsers(){
        Collection<RosterEntry> entries = getRoster().getEntries();
        Iterator<RosterEntry> it = entries.iterator();
        while(it.hasNext()){
            RosterEntry current = it.next();
            String name = current.getUser().split("@")[0];
            switch (current.getType()){
                case none:
                    MyApplication.getInstance().getUser().removeContact(new Contact(name, new Profile()));
                    break;
                case from:
                    List<Contact> contacts = MyApplication.getInstance().getDatabase().getAllContacts();
                    boolean exists = false;
                    for(Contact contact : contacts){
                        if(contact.getUsername().toLowerCase().equals(name.toLowerCase())){
                            exists = true;
                        }
                    }
                    if(!exists) {
                        MyApplication.getInstance().getUser().addContact(name);
                    } else {
                        addContact(name);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static boolean isLoggedIn(){
        return loggedIn;
    }

    @Override
    public void createGroupConversation(List<ADigitalPerson> recipients, String name) {
        /*MultiUserChat muc = null;
        if(name.isEmpty()){
            String newName = SystemUser.getInstance().getUser().getUsername() + ", ";
            for(ADigitalPerson recipient: recipients){
                newName += recipient.getUsername() + ", ";
            }
            muc = new MultiUserChat(connection, newName);
            groupChatList.add(muc);
        }else {
            muc = new MultiUserChat(connection, name);
            groupChatList.add(muc);
        }
        for(ADigitalPerson recipient : recipients){
            try {
                muc.invite(recipient.getUsername() + DOMAIN, "chatting");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
        Conversation c = new Conversation(recipients);
        SystemUser.getInstance().getUser().addConversation(c);*/

    }

    public void sendMessage(ClientMessage clientMessage) {
        org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
        NetworkMessage networkMessage = null;
        Gson gson = new Gson();
        switch (clientMessage.getType()){
            case TEXTMESSAGE:
                networkMessage = new NetworkMessage<String>();
                break;
            case DRAWING:
                networkMessage= new NetworkMessage<Drawing>();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        networkMessage.convertToNetworkMessage(clientMessage);
        message.setLanguage(clientMessage.getType().toString());
        String object = gson.toJson(networkMessage);
        message.setBody(object);
        sendMessageToContacts(networkMessage, message);
    }

    /**
     * Adds the specified user if it exists
     * @param userName the user to be added
     * @return true if user exists false otherwise
     */
    public boolean addContact(String userName) {
        Roster roster = connection.getRoster();
        List<String> matchingUsers = null;
        try {
            matchingUsers = searchUsers(userName);
            for(String match : matchingUsers){
                if(match.equals(userName)){
                    roster.createEntry(userName+DOMAIN, userName, null);
                    return true;
                }
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeContact(String userName){
        Roster roster = connection.getRoster();

        try {
            RosterEntry entry = roster.getEntry(userName+DOMAIN);
            if(entry != null){
                roster.removeEntry(entry);
            } else {
                return false;
            }
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isConnected(){
        return connection.isConnected();
    }

    @Override
    public boolean changePassword(String password) {
        manager = AccountManager.getInstance(connection);
        try {
            manager.changePassword(password);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Gets the matching users from the server.
     * @return matching users
     */
    public List<String> searchUsers(String userName) {
        UserSearchManager search = new UserSearchManager(connection);

        Form searchForm = null;
        List<String> matchingUsers = new ArrayList<>();
        try {
            searchForm = search.getSearchForm("search." + connection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);

            answerForm.setAnswer("search", userName);

            ReportedData data = search.getSearchResults(answerForm, "search." + connection.getServiceName());

            if (data.getRows() != null) {
                Iterator<ReportedData.Row> it = data.getRows().iterator();
                while (it.hasNext()) {
                    ReportedData.Row row = it.next();
                    matchingUsers.add(row.getValues("Username").get(0));
                }
            }
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }


        return matchingUsers;
    }

    public List<Contact> getContacts(){
        List<Contact> list = new ArrayList<>();
        Collection<RosterEntry> entries = getRoster().getEntries();
        for(RosterEntry entry : entries){
            Contact contact = new Contact(entry.getUser().split("@")[0], new Profile());
            Presence presence = getRoster().getPresence(entry.getUser());
            if(presence.isAvailable()){
                contact.setStatus(Status.ONLINE);
            } else if (presence.isAway()) {
                contact.setStatus(Status.AWAY);
            } else {
                contact.setStatus(Status.OFFLINE);
            }
            list.add(contact);
        }
        return list;
    }

    private void sendMessageToContacts(NetworkMessage networkMessage,  org.jivesoftware.smack.packet.Message message){
        boolean exists = false;
        for(Chat c : chatList) {
            List<String> receivers = networkMessage.getReceivers();
            for(Object recipient : receivers){
                if(c.getParticipant().split("@")[0].equals(recipient.toString())) {
                    exists = true;
                    try {
                        c.sendMessage(message);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        if(!exists){
            ChatManager chatManager = getChatManager();
            for(Object receiver : networkMessage.getReceivers()){
                Chat chat = chatManager.createChat((String)receiver + DOMAIN, messageListener);
                chatList.add(chat);
                try {
                    chat.sendMessage(message);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {
            ClientMessage clientMessage = getMessage(message.getBody(), message.getLanguage());
            Conversation conversation = MyApplication.getInstance().getUser().addMessage(clientMessage, false);
            NotificationHandler notificationHandler = new NotificationHandler(MyApplication.getContext());
            notificationHandler.pushNewMessageNotification(conversation, clientMessage);
        }
    };
    private RosterListener rosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> strings) {
            Log.d("REQUEST", "ENTRIES ADDED REQUEST");
        }

        @Override
        public void entriesUpdated(Collection<String> strings) {

        }

        @Override
        public void entriesDeleted(Collection<String> strings) {

        }

        @Override
        public void presenceChanged(Presence presence) {
            Presence prec = presence;
            Log.d("Presence changed" + presence.getFrom()+ " "+presence, "");
        }
    };

    private ChatManagerListener chatManagerListener = new ChatManagerListener() {
        @Override
        public void chatCreated(Chat chat, boolean b) {
            if (!b) {
                if (!chatList.contains(chat)) {
                    chatList.add(chat);
                }
                Chat c = chatList.get(chatList.indexOf(chat));
                c.addMessageListener(messageListener);


            }
        }
    };

    private ClientMessage getMessage(String body, String type){
        ClientMessage clientMessage = null;
        Gson gson = new Gson();
        MessageType messageType = MessageType.valueOf(type);
        switch (messageType) {
            case TEXTMESSAGE:
                NetworkMessage<String> networkMessage = gson.fromJson(body, NetworkMessage.class);
                clientMessage = networkMessage.convertFromNetworkMessage(messageType);
                break;
            case DRAWING:
                Type classType = new TypeToken<NetworkMessage<Drawing>>(){}.getType();
                NetworkMessage<Drawing> drawingNetworkMessage = gson.fromJson(body, classType);
                clientMessage = drawingNetworkMessage.convertFromNetworkMessage(messageType);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return clientMessage;
    }

    private PacketListener requestListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) throws SmackException.NotConnectedException {
            if(packet instanceof Presence){
                Presence presence = (Presence)packet;
                if(presence.getType().equals(Presence.Type.subscribe)){
                    String userName = packet.getFrom().split("@")[0];
                    User user = MyApplication.getInstance().getUser();
                    boolean exists = false;
                    for(Contact contact : user.getContactList()){
                        if(contact.getUsername().equals(userName)){
                            exists = true;
                            break;
                        }
                    }

                    if(!exists) {
                        user.addContact(packet.getFrom().split("@")[0].toLowerCase());

                    }
                } else if (presence.getType().equals(Presence.Type.unsubscribe) || presence.getType().equals(Presence.Type.unsubscribed)) {
                    String userName = packet.getFrom().split("@")[0];
                    User user = MyApplication.getInstance().getUser();
                    boolean exists = false;
                    for(Contact contact : user.getContactList()){
                        if(contact.getUsername().equals(userName)){
                            exists = true;
                            break;
                        }
                    }

                    if(exists) {
                        user.removeContact(new Contact(packet.getFrom().split("@")[0], new Profile()));
                    }
                }
            }
        }
    };
}

