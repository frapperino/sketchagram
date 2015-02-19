package sketchagram.chalmers.com.network;

import android.content.Context;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

/**
 * Created by Olliver on 15-02-18.
 */
public class Connection {
    ConnectionConfiguration config;
    XMPPConnection connection;
    AccountManager manager;
    public static void main(String args[]){
        Connection conn = new Connection();
        conn.createAccount("test", "test");
    }
    public Connection(){
        config = new ConnectionConfiguration("127.0.0.1", 5222);
        connection = new XMPPTCPConnection(config);
        connect();
        manager = AccountManager.getInstance(connection);
    }

    private void connect(){
        try {
            connection.connect();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }

    }

    public void createAccount(String userName, String password){
        try {
            manager.createAccount(userName, password);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }


}
