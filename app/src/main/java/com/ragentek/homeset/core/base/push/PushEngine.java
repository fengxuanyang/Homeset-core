package com.ragentek.homeset.core.base.push;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.ragentek.homeset.core.BuildConfig;
import com.ragentek.homeset.core.base.Engine;
import com.ragentek.homeset.core.utils.DeviceUtils;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.protocol.constants.DeviceType;
import com.ragentek.protocol.constants.MessageType;
import com.ragentek.protocol.messages.JsonMsg;
import com.ragentek.protocol.messages.tcp.AckMsg;
import com.ragentek.protocol.messages.tcp.DeviceLoginMessage;
import com.ragentek.protocol.messages.tcp.PushMessagePack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * Notes: get an instance of this class by calling EngineManager.getEngine(EngineManager.ENGINE_PUSH).
 */
public class PushEngine extends Engine {
    public static final String TAG = PushEngine.class.getSimpleName();
    public static final String SUB_TAG = "Client";

    private static final int MSG_INIT = 1;
    private static final int MSG_CONNECT = 2;
    private static final int MSG_SEND = 3;
    private static final int MSG_DISCONNECT = 4;
    private static final int MSG_RELEASE = 5;

//    private static final String DEFAULT_HOST = "192.168.12.10";
    private static final String DEFAULT_HOST = "www.robyun.com";
    private static final int DEFAULT_PORT = BuildConfig.PUSH_PORT;
    private static int DEFAULT_HEART_BEAT_INTERVAL_IN_SECONDS = 300;    // TODO: need to confirm
    private static int MAX_FRAME_LENGTH = 1024 * 1024;


    private boolean mDebug = true; // TODO: set false if released
    private int mHeartBeatTimeInterval = DEFAULT_HEART_BEAT_INTERVAL_IN_SECONDS;

    private DeviceUtils mDeviceUtils;
    private HandlerThread mWorkingThread;
    private WorkingHandler mWorkingHandler;

    private EventLoopGroup mWorkGroup;
    private Channel mChannel;

    private Context mContext;

    /**
     * It will be called when received push message.
     */
    public interface OnMessageReceiveListener {
        void onReceived(PushMessagePack messagePack);
    }
    private HashMap<Integer, OnMessageReceiveListener> mOnReceiveListenerMap;

    /**
     * Construct function.
     *
     * @param name engine name.
     */
    public PushEngine(Context context, String name) {
        super(name);

        mContext = context;
        mDeviceUtils = DeviceUtils.getInstance(context);
        mOnReceiveListenerMap = new HashMap<Integer, OnMessageReceiveListener>();
    }

    public enum STATE {
        INIT,
        ACTIVE,
        INACTIVE,
        RELEASE
    }
    private STATE mState = STATE.RELEASE;

    /**
     * the message parameter of connect function
     */
    class ConnectParam {
        private String host;
        private int port;

        public ConnectParam(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    @Override
    public void init(InitListener listener) {
        assert(getState() == STATE.RELEASE);

        mWorkingThread = new HandlerThread(TAG);
        mWorkingThread.start();
        mWorkingHandler = new WorkingHandler(mWorkingThread.getLooper());
        sendToHandler(MSG_INIT, listener);
    }

    @Override
    public void release() {
        sendToHandler(MSG_RELEASE);
    }

    public void connect() {
        connect(DEFAULT_PORT);
    }

    public void connect(int port) {
        connect(DEFAULT_HOST, port);
    }

    public void connect(String host, int port) {
        sendToHandler(MSG_CONNECT, new ConnectParam(host, port));
    }

    public void sendMessage(PushMessagePack messagePack) {
        sendToHandler(MSG_SEND, messagePack);
    }

    public void registerMessageListener(int messageType, OnMessageReceiveListener listener) {
        if (findMessageListener(messageType) != null) {
            // Only use for debug
            LogUtils.e(TAG, "registerMessageListener, messageType=" + messageType + " already registered", new Throwable());
        }
        mOnReceiveListenerMap.put(messageType, listener);
    }

    public void unregiterMessageListener(int messageType) {
        if (findMessageListener(messageType) == null) {
            // Only use for debug
            LogUtils.e(TAG, "registerMessageListener, messageType=" + messageType + " is not exists", new Throwable());
        }
        mOnReceiveListenerMap.remove(messageType);
    }

    /**
     * Return null if no found.
     * */
    private OnMessageReceiveListener findMessageListener(int messageType) {
        return mOnReceiveListenerMap.get(messageType);
    }

    public void disconnect() {
        sendToHandler(MSG_DISCONNECT);
    }

    public void setDebug(boolean isDebug) {
        mDebug = isDebug;
    }

    public void setHeartBeatTimeIntervalInSecond(int timeInterval) {
        mHeartBeatTimeInterval = timeInterval;
    }

    public boolean isConnected() {
        return (getState() == STATE.ACTIVE);
    }

    class WorkingHandler extends Handler {

        public WorkingHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    setState(STATE.INIT);
                    InitListener listener = (InitListener) msg.obj;
                    listener.onInit(PushEngine.this, true);
                    break;
                case MSG_CONNECT:
                    ConnectParam connectParam = (ConnectParam) msg.obj;
                    handleConnect(connectParam.host, connectParam.port);
                    break;
                case MSG_SEND:
                    PushMessagePack messagePack = (PushMessagePack) msg.obj;
                    handleSendMessage(messagePack);
                    break;
                case MSG_DISCONNECT:
                    handleDisconnect();
                    break;
                case MSG_RELEASE:
                    handleRelease();
                    break;
            }
        }
    }

    private void handleConnect(String host, int port) {
        if (isConnected()) {
            handleDisconnect();
        }

        mWorkGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(mWorkGroup).
                channel(NioSocketChannel.class).
                handler(new ServerChannelInitializer()).
                option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LogUtils.e(TAG, future.cause().toString());
                    return;
                }

                DeviceLoginMessage loginMsg = new DeviceLoginMessage(mDeviceUtils.getAccessToken());
                PushMessagePack messagePack = new PushMessagePack(MessageType.SYS_CHANNEL_LOGIN, mDeviceUtils.getLDid(), loginMsg);

                future.channel().writeAndFlush(messagePack);
            }
        });
    }

    class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            mChannel = socketChannel;
            ChannelPipeline pipeline = socketChannel.pipeline();

            if (mDebug) {
                pipeline.addLast(new AndroidLoggingHandler(TAG, SUB_TAG));
            }

            /* Connection state handler */
            pipeline.addLast(new ConnectionStateHandler());

            /* Process in bound message */
            pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, 2, 4, 0, 6));
            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
            pipeline.addLast(new MessageFrameDecoder());
            pipeline.addLast(new InBoundMessagePackHandler());

            /* Process out bound message */
            pipeline.addLast(new MessageFrameEncoder());

            /* Process user event */
            // TODO: need to optimize
            pipeline.addLast(new IdleStateHandler(0, 0, mHeartBeatTimeInterval, TimeUnit.SECONDS));
            pipeline.addLast(new HeartbeatSenderHandler());

            /* Handle exceptions */
            pipeline.addLast(new ExceptionHandler(TAG));
        }
    }

    class ConnectionStateHandler extends ChannelHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            setState(STATE.ACTIVE);

            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            if (getState() != STATE.RELEASE) {
                setState(STATE.INACTIVE);
            }

            mWorkGroup.shutdownGracefully();

            super.channelInactive(ctx);
        }
    }

    class InBoundMessagePackHandler  extends SimpleChannelInboundHandler<PushMessagePack> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, PushMessagePack messagePack) throws Exception {
            /* Handle ack message */
            if (messagePack.getMsgType() == MessageType.SYS_CHANNEL_ACK) {
                return;
            }

            /* Handle user message */
            if (messagePack.getQos() == 1) {
                sendMessage(createConfirmMessage(messagePack.getMid()));
            }

            OnMessageReceiveListener mListener = findMessageListener(messagePack.getMsgType());
            if (mListener != null) {
                mListener.onReceived(messagePack);
            }
        }
    }

    public class HeartbeatSenderHandler extends ChannelHandlerAdapter {

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent e = (IdleStateEvent) evt;

                if (e.state() == IdleState.ALL_IDLE) {
                    // TODO: need to confirm
                    PushMessagePack messagePack = new PushMessagePack(MessageType.SYS_CHANNEL_HEARTBEAT, mDeviceUtils.getLDid(), new JsonMsg());
                    ctx.channel().writeAndFlush(messagePack).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                } else {
                    super.userEventTriggered(ctx, evt);
                }
            }
        }
    }

    // TODO: Qos message need to confirm.
    private PushMessagePack createConfirmMessage(int mid) {
        PushMessagePack messagePack = new PushMessagePack(MessageType.SYS_CHANNEL_ACK, mDeviceUtils.getLDid(), new AckMsg(0));
        messagePack.setMid(mid);
        return  messagePack;
    }

    private void handleDisconnect() {
        if (!isConnected()) {
            return;
        }
        mChannel.close();
    }

    private void handleRelease() {
        handleDisconnect();
        mWorkingThread.quitSafely();
        setState(STATE.RELEASE);
    }

    private void handleSendMessage(PushMessagePack messagePack) {
        if (!isConnected()) {
            LogUtils.e(TAG, "handleSendMessage fail for netty is disconnect");
            return;
        }

        mChannel.writeAndFlush(messagePack);
    }

    private void setState(STATE state) {
        mState = state;
    }

    private STATE getState() {
        return mState;
    }

    private void sendToHandler(int what) {
        sendToHandler(what, null);
    }

    private void sendToHandler(int what, Object obj) {
        sendToHandler(what, obj, 0);
    }

    private void sendToHandler(int what, Object obj, int arg1) {
        sendToHandler(what, obj, arg1, 0);
    }

    private void sendToHandler(int what, Object obj, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        mWorkingHandler.sendMessage(msg);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append("state=").append(getState()).append(',');
        builder.append("mChannel=").append(mChannel).append(',');
        builder.append(']');
        return builder.toString();
    }

}
