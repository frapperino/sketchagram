package sketchagram.chalmers.com.network;

import android.os.AsyncTask;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.tcp.*;
import org.jivesoftware.smack.AccountManager;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
            SASLAuthentication.supportSASLMechanism("PLAIN");
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

    public Exception createAccount(String userName, String password) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params){
                try {
                    disconnect();
                    connect();
                    manager = AccountManager.getInstance(connection);
                    manager.supportsAccountCreation();
                    manager.createAccount(params[0].toString(), params[1].toString());
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
                disconnect();
                connect();
                try {
                    connection.login(userName, password);
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        }.execute(userName, password);
        return true;
    }

}
