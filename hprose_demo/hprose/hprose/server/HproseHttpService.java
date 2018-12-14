/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * HproseHttpService.java                                 *
 *                                                        *
 * hprose http service class for Java.                    *
 *                                                        *
 * LastModified: Jun 8, 2015                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.server;

import hprose.common.HproseContext;
import hprose.common.HproseMethods;
import hprose.io.ByteBufferStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HproseHttpService extends HproseService {
    private boolean crossDomainEnabled = false;
    private boolean p3pEnabled = false;
    private boolean getEnabled = true;
    private long timeout = 30000;
    private final HashMap<String, Boolean> origins = new HashMap<String, Boolean>();
    private static final ThreadLocal<HttpContext> currentContext = new ThreadLocal<HttpContext>();

    public static HttpContext getCurrentContext() {
        return currentContext.get();
    }

    @Override
    public HproseMethods getGlobalMethods() {
        if (globalMethods == null) {
            globalMethods = new HproseHttpMethods();
        }
        return globalMethods;
    }

    @Override
    public void setGlobalMethods(HproseMethods methods) {
        if (methods instanceof HproseHttpMethods) {
            this.globalMethods = methods;
        }
        else {
            throw new ClassCastException("methods must be a HproseHttpMethods instance");
        }
    }

    public boolean isCrossDomainEnabled() {
        return crossDomainEnabled;
    }

    public void setCrossDomainEnabled(boolean enabled) {
        crossDomainEnabled = enabled;
    }

    public boolean isP3pEnabled() {
        return p3pEnabled;
    }

    public void setP3pEnabled(boolean enabled) {
        p3pEnabled = enabled;
    }

    public boolean isGetEnabled() {
        return getEnabled;
    }

    public void setGetEnabled(boolean enabled) {
        getEnabled = enabled;
    }

    public void addAccessControlAllowOrigin(String origin) {
        origins.put(origin, true);
    }

    public void removeAccessControlAllowOrigin(String origin) {
        origins.remove(origin);
    }
    
    public void setTimeout(long value) {
        timeout = value;
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments, HproseContext context) {
        int count = arguments.length;
        HttpContext httpContext = (HttpContext)context;
        if (argumentTypes.length != count) {
            Object[] args = new Object[argumentTypes.length];
            System.arraycopy(arguments, 0, args, 0, count);
            Class<?> argType = (Class<?>) argumentTypes[count];
            if (argType.equals(HproseContext.class)) {
                args[count] = context;
            }
            else if (argType.equals(HttpContext.class)) {
                args[count] = httpContext;
            }
            else if (argType.equals(HttpServletRequest.class)) {
                args[count] = httpContext.getRequest();
            }
            else if (argType.equals(HttpServletResponse.class)) {
                args[count] = httpContext.getResponse();
            }
            else if (argType.equals(HttpSession.class)) {
                args[count] = httpContext.getSession();
            }
            else if (argType.equals(ServletContext.class)) {
                args[count] = httpContext.getApplication();
            }
            else if (argType.equals(ServletConfig.class)) {
                args[count] = httpContext.getConfig();
            }
            return args;
        }
        return arguments;
    }

    protected void sendHeader(HttpContext httpContext) throws IOException {
        if (event != null && HproseHttpServiceEvent.class.isInstance(event)) {
            ((HproseHttpServiceEvent)event).onSendHeader(httpContext);
        }
        HttpServletRequest request = httpContext.getRequest();
        HttpServletResponse response = httpContext.getResponse();
        response.setContentType("text/plain");
        if (p3pEnabled) {
            response.setHeader("P3P", "CP=\"CAO DSP COR CUR ADM DEV TAI PSA PSD " +
                                      "IVAi IVDi CONi TELo OTPi OUR DELi SAMi " +
                                      "OTRi UNRi PUBi IND PHY ONL UNI PUR FIN " +
                                      "COM NAV INT DEM CNT STA POL HEA PRE GOV\"");
        }
        if (crossDomainEnabled) {
            String origin = request.getHeader("Origin");
            if (origin != null && !origin.equals("null")) {
                if (origins.isEmpty() || origins.containsKey(origin)) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                }
            }
            else {
                response.setHeader("Access-Control-Allow-Origin", "*");
            }
        }
    }

    public void handle(HttpContext httpContext) throws IOException {
        handle(httpContext, null);
    }

    public void handle(HttpContext httpContext, HproseHttpMethods methods) throws IOException {
        sendHeader(httpContext);
        String method = httpContext.getRequest().getMethod();
        if (method.equals("GET")) {
            if (getEnabled) {
                ByteBufferStream ostream = null;
                try {
                    ostream = doFunctionList(methods, httpContext);
                    ostream.writeTo(httpContext.getResponse().getOutputStream());
                }
                finally {
                    if (ostream != null) {
                        ostream.close();
                    }
                }
            }
            else {
                httpContext.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        else if (method.equals("POST")) {
            if (httpContext.getRequest().isAsyncSupported()) {
                asyncHandle(httpContext, methods);
            }
            else {
                syncHandle(httpContext, methods);
            }
        }
    }

    private void asyncHandle(final HttpContext httpContext, final HproseHttpMethods methods) {
        final AsyncContext async = httpContext.getRequest().startAsync();
        async.setTimeout(timeout);
        async.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent ae) throws IOException {
            }
            public void onTimeout(AsyncEvent ae) throws IOException {
                ByteBufferStream ostream = sendError(ae.getThrowable(), httpContext);
                ostream.writeTo(ae.getSuppliedResponse().getOutputStream());
            }
            public void onError(AsyncEvent ae) throws IOException {
            }
            public void onStartAsync(AsyncEvent ae) throws IOException {
            }
        });
        async.start(new Runnable() {
            public void run() {
                ByteBufferStream istream = null;
                ByteBufferStream ostream = null;
                try {
                    currentContext.set(httpContext);
                    istream = new ByteBufferStream();
                    istream.readFrom(async.getRequest().getInputStream());
                    ostream = handle(istream, methods, httpContext);
                    ostream.writeTo(async.getResponse().getOutputStream());
                }
                catch (IOException ex) {
                    fireErrorEvent(ex, httpContext);
                }
                finally {
                    currentContext.remove();
                    if (istream != null) {
                        istream.close();
                    }
                    if (ostream != null) {
                        ostream.close();
                    }
                    async.complete();
                }
            }
        });
    }

    private void syncHandle(HttpContext httpContext, HproseHttpMethods methods) throws IOException {
        ByteBufferStream istream = null;
        ByteBufferStream ostream = null;
        try {
            currentContext.set(httpContext);
            istream = new ByteBufferStream();
            istream.readFrom(httpContext.getRequest().getInputStream());
            ostream = handle(istream, methods, httpContext);
            ostream.writeTo(httpContext.getResponse().getOutputStream());
        }
        finally {
            currentContext.remove();
            if (istream != null) {
                istream.close();
            }
            if (ostream != null) {
                ostream.close();
            }
        }
    }
}
