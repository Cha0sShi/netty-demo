package protocol;

import config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import message.Message;

import java.nio.Buffer;
import java.util.List;
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf,Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message message, List<Object> list) throws Exception {
        ByteBuf out =ctx.alloc().buffer();
        out.writeBytes(new byte[]{1,2,3,4}); // 4字节的 魔数
        out.writeByte(1);                    // 1字节的 版本
        out.writeByte(Config.getAlgorithm().ordinal()); // 1字节的 序列化方式 0-jdk,1-json
        out.writeByte(message.getMessageType()); // 1字节的 指令类型
        out.writeInt(message.getSequenceId());   // 4字节的 请求序号 【大端】
        out.writeByte(0xff);                 // 1字节的 对其填充，只为了非消息内容 是2的整数倍
        final byte[] bytes = Config.getAlgorithm().serialize(message);
        // 写入内容 长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);
        list.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        int magicNum = in.readInt();        // 大端4字节的 魔数
        byte version = in.readByte();       // 版本
        byte serializerType = in.readByte();// 0 Java 1 Json
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();

        int length = in.readInt();
        final byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length); // 读取进来，下面再进行 解码

        // 1. 找到反序列化算法
        final MySerializer.Algorithm algorithm = MySerializer.Algorithm.values()[serializerType];
        // 2. 找到消息具体类型
        final Object message = algorithm.deserialize(Message.getMessageType(messageType),bytes);
        list.add(message);
    }
}
