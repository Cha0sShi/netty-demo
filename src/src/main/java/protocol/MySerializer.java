package protocol;

import java.io.*;

public interface MySerializer {
    <T> T deserialize(Class<T> clazz,byte[] bytes) throws IOException;
    <T> byte[] serialize(T object) throws IOException;

    enum Algorithm implements MySerializer{
        Java{
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) throws IOException {
                try(ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
                    ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);){
                    T message =(T) objectInputStream.readObject();
                    return message;

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }


            }

            @Override
            public <T> byte[] serialize(T object) throws IOException {
                try( ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                     ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);){
                    objectOutputStream.writeObject(object);
                    return byteArrayOutputStream.toByteArray();

                }
            }
        }
    }
}
