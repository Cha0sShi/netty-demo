package handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import message.RpcRequestMessage;
import message.RpcResponseMessage;
import service.HelloService;
import service.ServiceFactory;

import java.lang.reflect.Method;

@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        final RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(message.getSequenceId());
        try{
            // 上面对象里 获取【接口类】全限定名
            final Class<?> interfaceClazz = Class.forName(message.getInterfaceName());
            // 根据接口类 获取 【接口实现类】
            final HelloService service = (HelloService) ServiceFactory.getService(interfaceClazz);
            // clazz 根据 方法名和参数类型 确定 【具体方法】
            final Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            // 根据 具体方法 使用代理 【执行方法】
            final Object invoke = method.invoke(service, message.getParameterValue());

            response.setReturnValue(invoke);

        } catch (Exception e) {
            e.printStackTrace();
            response.setExceptionValue(new Exception("远程调用出错：" + e.getCause().getMessage()));
        }
        ctx.writeAndFlush(response);
    }
}
