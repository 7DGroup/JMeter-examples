package hprose.hello.server;

/***
 * 服务类
 */

public class Hello {

    /***
     * 返回hello+客户端传参
     * @param name
     * @return
     */
    public String sayHello(String name) {
        return "Hello " + name + "!";
    }

    /**
     * 返回两个数字的和
     * @param a
     * @param b
     * @return
     */
    public String add(int a,int b)
    {
    	int c=a+b;
    	return "结果是："+c;
    }
}
