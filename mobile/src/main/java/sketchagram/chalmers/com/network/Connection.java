package sketchagram.chalmers.com.network;

import android.os.AsyncTask;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.*;
import org.jivesoftware.smack.AccountManager;

import java.io.IOException;

/**
 * Created by Olliver on 15-02-18.
 */
public class Connection{
    ConnectionConfiguration config;
    XMPPTCPConnection connection;
    AccountManager manager;
    public Connection(){
        config = new ConnectionConfiguration("83.254.68.47", 5222);
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        connection = new XMPPTCPConnection(config);
    }

    private void connect(){
        try {
            connection.connect();
        } catch (SmackException e) {
            e.printStackTrace();
            System.out.println(e.toString());
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

    public void createAccount(String userName, String password){
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    disconnect();
                    connect();
                    manager = AccountManager.getInstance(connection);
                    manager.supportsAccountCreation();
                    manager.createAccount(params[0].toString(), params[1].toString());
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (SmackException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute(userName, password);
    }


}
