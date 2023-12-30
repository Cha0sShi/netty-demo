package server;

import handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import protocol.MessageCodecSharable;
import protocol.ProtocolFrameDecoder;

import java.io.IOException;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcRequestMessageHandler RPC_REQUEST_HANDLER = new RpcRequestMessageHandler();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {


                @Override
                protected void initChannel(SocketChannel channel) throws Exception {

                    channel.pipeline()
                            .addLast(new ProtocolFrameDecoder())
                            .addLast(loggingHandler)
                            .addLast(MESSAGE_CODEC)
                   .addLast(RPC_REQUEST_HANDLER);
                }
            });
            Channel channel=serverBootstrap.bind(8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.debug("wrong");
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }//心跳检测
}
