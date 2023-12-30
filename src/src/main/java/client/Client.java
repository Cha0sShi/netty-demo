package client;

import handler.RpcResponseMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import message.RpcRequestMessage;
import protocol.MessageCodecSharable;
import protocol.ProtocolFrameDecoder;
import protocol.SequenceIdGenerator;
import service.HelloService;

import java.lang.reflect.Proxy;

@Slf4j
public class Client {
   static Channel channel=null;
    private static final Object LOCK =new Object();
    public static Channel getChannel(){
        if(channel==null){
            synchronized(LOCK){
                initChannel();
            }
        }
        return channel;
    }
    static void initChannel(){
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .handler(new ChannelInitializer<SocketChannel>(){

                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new ProtocolFrameDecoder())
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new MessageCodecSharable())
                                    .addLast(new RpcResponseMessageHandler());

                    }
                });
        try{
            channel=bootstrap.connect("localhost",8080).sync().channel();
            channel.closeFuture().addListener(future -> group.shutdownGracefully());
        }catch (Exception e){
            log.debug("sad");
        }
    }
    public static <T> T getProxyService(Class<T> serviceClass) {
        ClassLoader loader = serviceClass.getClassLoader();
        Class[] interfaces = {serviceClass};
        final int nextId = SequenceIdGenerator.nextId();

        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            final RpcRequestMessage msg = new RpcRequestMessage(
                    nextId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );

            getChannel().writeAndFlush(msg);
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            // 使用 synchronized 块确保在调用 wait() 之前获取对象的监视器锁
                RpcResponseMessageHandler.PROMISES.put(nextId, promise);
                promise.await();



                if (promise.isSuccess()) {
                    // 调用正常
                    return promise.getNow();
                } else {
                    // 调用失败
                    throw new RuntimeException(promise.cause());
                }
        });
        return (T) o;
    }

    public static void main(String[] args) {
        HelloService service=getProxyService(HelloService.class);
        System.out.println(service.sayHello("ni"));

        System.out.println(1111);
    }
}
