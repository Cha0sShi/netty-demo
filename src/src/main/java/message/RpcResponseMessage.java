package message;

import lombok.Data;

@Data

public class RpcResponseMessage extends Message{
        /**
         * 返回值
         */
        private Object returnValue;
        /**
         * 异常值
         */
        private Exception exceptionValue;


    @Override
    public int getMessageType() {
        return RpcResponseMessage;
    }
}
