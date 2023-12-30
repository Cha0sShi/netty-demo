package handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import message.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponseMessage rpcResponseMessage) throws Exception {
        final Promise<Object> promise = PROMISES.remove(rpcResponseMessage.getSequenceId());
        if (promise != null) {

            final Object returnValue = rpcResponseMessage.getReturnValue();         // 正常结果
            final Exception exceptionValue = rpcResponseMessage.getExceptionValue();// 异常结果 【约定 为 null才是正常的】
            if (exceptionValue != null) {

                promise.setFailure(exceptionValue);

            }else{
                promise.setSuccess(returnValue);
            }


        }
    }
}
