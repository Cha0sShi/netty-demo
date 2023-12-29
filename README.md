# 普通框架

## config包

+ `config`类从项目配置文件读取相关配置

## message包

+ `Message`抽象类: 定义消息类型标识和类对象的映射(HashMap<Integer,Class>实现)

+ `AbstractResponseMessage`抽象类: 继承Message,定义success,reason成员变量,用于接收部分response



上面两个作为具体消息类的父类.具体消息类要有getMesseageType方法,返回Messege中定义的映射的Key,用于之后发送给服务端

![image-20231228004138009](C:\Users\jason\AppData\Roaming\Typora\typora-user-images\image-20231228004138009.png)



## protcol包

+ `MySerializer` 接口

  ```java
  // 反序列化
  <T> T deserializer(Class<T> clazz, byte[] bytes);
  
  // 序列化
  <T> byte[] serializ(T object);
  ```

  包含`enum Algorithm implements MySerializer` 里面定义实现上面两个方法的序列化算法的具体实现

+ `MessageCodecSharable`一个继承自`MessageToMessageCodec`的类

  重写encode()和decode()方法

  首先时encode()的"输出"一般包含下面几段(字节数仅推荐)

  - <b>魔数 (4 Byte)    </b>，标记自定义协议的格式

   + <b>版本号(1 Byte)   </b>，可以支持协议的升级
   + <b>序列化算法</b>(1 Byte)，消息正文到底采用哪种序列化反序列化方式，在`enum Algorithm`定义，例如：json、jdk
   + <b>指令类型  </b>，具体的业务,是之前定义的映射的Key
   + <b>请求序号  </b>，为了后面的RPC双工通信，提供异步能力,即在返回时能找到对应的promise
   + <b>保留字段,</b> 保留字段是可选项，为了应对协议升级的可能性，可以预留若干字节的保留字段，以备不时之需。
   + <b>正文长度  </b>
   + <b>消息正文 </b>

  decode()将对应的字段取出来,用相应的算法和具体消息类型进行处理

+ `ProtocolFrameDecoder`继承`LengthFieldeBasedFrameDecoder`类 

  用于自动根据长度字段来读取消息,而且可以选择跳过读取的信息

  ![img](https://nyimapicture.oss-cn-beijing.aliyuncs.com/img/20210425200007.png)

  ![image-20231228013115390](C:\Users\jason\AppData\Roaming\Typora\typora-user-images\image-20231228013115390.png)

Hello前面的`......`是除正文外的其他信息,这些信息在前面字节码中,但不会被解析,如果把initialBytesToStrio参数设为1,那么会少一个".",而且前面的"ca"那个字节会被跳过读取,不显示在字节码



## client包

+ `ChatClient`类主要定义了

  + NioEventLoopGroup: 循环组

  + LoggingHandel: Netty日志打印

  + ProtocolFrameDecoder: 长度字段解码器

  + IdelStateHandler: 用于心跳检测,关闭断开线程

  + ChannelDulplexHandler: 重写userEventTriggered()以处理上面断开事件

  + CountDownLatch: 用于控制线程进行的一个JUC工具类

    

  + ChannelInboundHandelerAdapter: 

    + channelActive()创建一个组逻辑的线程,并用CountDownLatch对象等待登录
    + channelRead()的重写负责处理登录事件,成功将调用countDown()使主线程运行



## 剩下server包下的包

​	`Server`类

​	

## service包

一个存储账号信息的包,实际不用



## session包

+ `SessionMemoryImpl` 创建用户和创建对话的双向映射,以及每个channel包含的成员信息
+ `GroupSessionMemoryImpl` 存储群组的信息,提供获取群组信息的方法



## handler

通过继承SimpleChannelInboundHandler确定接收消息的类型

+ `ChatRequestMessageHandler`
+ `GroupChatRequestMessageHandler`
+ `GroupCreateRequestMessageHandler`
+ `LoginRequestMessageHandler`
+ `QuitHandler`





# RPC框架升级



## client包



通过反射代理进行函数的调用

```java
Proxy.newProxyInstance(serviceClass.getClassLoader/*代理的接口*/,interfaces,(proxy,  method,  args)->{
    //逻辑
})
```



上面的lambda函数是一个handler,但实际上,不是本地执行,所以不需要接收target对象来执行函数,所以直接用lambda进行远程调用

```java
public class InvocationHandlerImplement implements InvocationHandler {
    Object target;  // 被代理的对象，实际的方法执行者

    public InvocationHandlerImplement(Object target) {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        Object result = method.invoke(target, args);  // 调用 target 的 method 方法
        after();
        return result;  // 返回方法的执行结果
    }
    // 调用invoke方法之前执行
    private void before() {
        System.out.println(String.format("log start time [%s] ", new Date()));
    }
    // 调用invoke方法之后执行
    private void after() {
        System.out.println(String.format("log end time [%s] ", new Date()));
    }
}
```

用map储存有对应id的promise对象,以方便接收远程函数调用的结果

​	

+ 用``RPC_RESPONSE_HANDLER``代替之前的handler

拿到response对应的id来获取promise,最后拿到结果



## server包

也是用`RPC_REQUEST_HANDLER`替换之前的handler