package hprose.hello.client;

import hprose.client.HproseHttpClient;
import java.io.IOException;

/**
 * Hprose客户端
 */
public class HelloClient {
    public static void main(String[] args) throws Throwable {
        //new一个HproseHttpClient对象
        HproseHttpClient client = new HproseHttpClient();
        //使用无参构造器创建的客户端，在进行调用前，需要先调用useService方法初始化服务器地址
        client.useService("http://localhost:8080/hprose_demo/Hello");
        //使用invoke方法来动态调用服务。
        String result = (String) client.invoke("sayHello", new Object[] { "Hprose" });
        System.out.println(result);
        result = (String) client.invoke("sayHello", new Object[] { "7DGroup" });
        System.out.println(result);
        System.out.println(client.invoke("add",new Object[]{1,6}));
    }
}
