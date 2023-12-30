package message;


public class LoginRequestMessage extends Message {
    private String name;

    private String password;
    public LoginRequestMessage(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public int getMessageType() {
        return LoginRequestMessage;
    }
}
