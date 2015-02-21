package sketchagram.chalmers.com.network;

import android.content.Context;

import org.jivesoftware.smack.Manager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.*;

import java.io.IOException;

/**
 * Created by Olliver on 15-02-18.
 */
public class Connection {
    XMPPTCPConnectionConfiguration config;
    XMPPTCPConnection connection;
    AccountManager manager;
    public Connection(){
        config = XMPPTCPConnectionConfiguration.builder().setHost("83.254.68.47").setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled).build();
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
