package hprose.hello.server;

import hprose.server.HproseWebSocketService;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;

/***
 * 服务发布类
 * WebSocket 绑定
 * Hprose RPC也可以工作于WebSocket之上，数据以二进制数据的方式在WebSocket上发送和接收。
 * Hprose RPC添加了4个字节的头作为请求唯一标识（id），服务器端不需要关心请求唯一标识（id）如何编码，只需要在应答中重复它就可以了。
 */

@ServerEndpoint("/wshello")
public class WebSocketServer {
    private HproseWebSocketService service = new HproseWebSocketService();
    public WebSocketServer() {
        //new出服务对象，发布类里面所有的公共方法
        service.add(new Hello());
    }
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        service.setConfig(config);
    }
    @OnMessage
    public void onMessage(ByteBuffer buf, Session session) throws IOException {
        service.handle(buf, session);
    }
    @OnError
    public void onError(Session session, Throwable error) {
        service.handleError(session, error);
    }
}
