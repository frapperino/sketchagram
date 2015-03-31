package sketchagram.chalmers.com.network;

/**
 * Holds all different types of exceptions related to the network.
 * This way we can encapsulate our code by not using plugin exceptions.
 * Created by Alexander on 2015-03-30.
 */
public class NetworkException {
    public static class ServerNotRespondingException extends Exception {
        public ServerNotRespondingException(String message) {
            super(message);
        }
    }
    public static class NotConnectedToNetworkException extends Exception {
        public NotConnectedToNetworkException(String message) {
            super(message);
        }
    }
    public static class UsernameAlreadyTakenException extends Exception {
        public UsernameAlreadyTakenException(String message) {
            super(message);
        }
    }
}
