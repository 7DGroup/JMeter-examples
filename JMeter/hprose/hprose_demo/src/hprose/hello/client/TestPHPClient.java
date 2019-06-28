package hprose.hello.client;

import java.io.IOException;

import hprose.client.HproseHttpClient;

public class TestPHPClient {

	public static void main(String[] args) throws Throwable {

		HproseHttpClient client = new HproseHttpClient();
		client.useService("http://127.0.0.1/hprose/http_server.php");
		String result = (String) client.invoke("hello",
				new Object[] { "Hprose来自Java客户端" });
		System.out.println(result);
		
		System.out.println(client.invoke("asyncHello",new Object[] { "Hprose来自Java客户端"}));
	}
}
