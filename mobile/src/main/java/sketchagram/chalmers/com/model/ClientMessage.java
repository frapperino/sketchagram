package sketchagram.chalmers.com.model;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Created by Bosch on 10/02/15.
 */
public class ClientMessage<T> {
    private final long timestamp;
    private final ADigitalPerson sender;
    private final List<ADigitalPerson> receivers = new ArrayList();
    private final T content;
    private final MessageType type;
    private boolean read;
    public ClientMessage(long timestamp, ADigitalPerson sender, List<ADigitalPerson> receiver, T content, MessageType type) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.receivers.addAll(receiver);
        this.content = content;
        this.type = type;
        this.read = sender.equals(SystemUser.getInstance().getUser());//TODO: Find why sender is null.
    }
    public ClientMessage(long timestamp, ADigitalPerson sender, List<ADigitalPerson> receiver, T content, MessageType type, boolean read){
        this.timestamp = timestamp;
        this.sender = sender;
        this.receivers.addAll(receiver);
        this.content = content;
        this.type = type;
        this.read = read;
    }
    public T getContent(){
        return content;
    }
    public MessageType getType(){
        return type;
    }
    public ADigitalPerson getSender(){
        return sender;
    }
    public List<ADigitalPerson> getReceivers(){return receivers;}
    public long getTimestamp(){return timestamp;}
    public String dateToShow(){
        Date ts = new Date(timestamp);
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        int year = Integer.parseInt(sdfYear.format(ts));
        int month = Integer.parseInt(sdfMonth.format(ts));
        Calendar today = new GregorianCalendar();
        int tdYear = today.get(Calendar.YEAR);
        int tdMonth = today.get(Calendar.MONTH)+1;
        System.out.println("tdMonth: " + tdMonth + " month: " + month);
        if((tdYear-year) > 0){
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
            return sdf.format(ts);
        }
        else if (tdMonth - month == 0){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(ts);
        }
        else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm");
            return sdf.format(ts);
        }
    }
    @Override
    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy HH:mm");
        Date resultDate = new Date(timestamp);
        return "[" + sdf.format(resultDate) + "] " + sender.getUsername() + ": " + content.toString();
    }
    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean equals(Object obj){
        if(this == obj) return true;
        else if (!(obj instanceof ClientMessage)) return false;
        ClientMessage other = (ClientMessage)obj;
        return false;
    }
}