package com.asm.message;

import com.google.common.base.Utf8;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.*;
import java.nio.charset.Charset;

@Data()
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class LoginResponseMessage extends AbstractResponseMessage {

    public LoginResponseMessage(boolean success, String reason) {
        super(success, reason);
    }


    @Override
    public int getMessageType() {
        return LoginResponseMessage;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        ByteArrayOutputStream byteArrayInputStream=new ByteArrayOutputStream();
        ObjectOutputStream outputStream =new ObjectOutputStream(byteArrayInputStream);
        outputStream.writeObject(new S("scs"));
        System.out.println(byteArrayInputStream);
        ByteArrayInputStream byteArrayInputStream1 =new ByteArrayInputStream(byteArrayInputStream.toByteArray());
        ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream1);
        System.out.println(objectInputStream.readObject());
    }
}
@Data
@AllArgsConstructor
class S implements Serializable {
    String name;
}
