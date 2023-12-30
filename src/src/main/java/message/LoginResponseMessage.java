package message;

public class LoginResponseMessage extends Message{
    boolean success;
    String cause;
     public LoginResponseMessage(boolean success,String cause){
        this.cause=cause;
        this.success=success;
    }
    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
}
