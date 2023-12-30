package message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {
    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();

    public static Class<?> getMessageType(int messageType){
        return map.get(messageType);
    }
    public static final int LoginRequestMessage =0;
    public static final int LoginResponseMessage =1;
    public static final int RpcRequestMessage =2;
    public static final int RpcResponseMessage =3;
    private static Map<Integer,Class<?>> map =new HashMap<>();
    static {
        map.put(LoginRequestMessage, LoginRequestMessage.class);
        map.put(LoginResponseMessage, LoginResponseMessage.class);
        map.put(RpcRequestMessage, RpcRequestMessage.class);
        map.put(RpcResponseMessage, RpcResponseMessage.class);
    }
}
