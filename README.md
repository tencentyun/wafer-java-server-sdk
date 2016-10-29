腾讯云微信小程序服务端 SDK - Java
=================================

本 SDK 是[腾讯云微信小程序一站式解决方案][weapp-solution]（下文简称「解决方案」）的组成部分。业务服务器可通过本 SDK 为小程序客户端提供云端服务支持，包括：

1. 登录态鉴权服务
2. 信道服务

## SDK 获取

本项目遵守 [MIT](LICENSE) 协议，可以直接[下载 SDK 源码][sdk-download]进行修改、编译和发布。

> 如果从[腾讯云微信小程序控制台][la-console]购买解决方案并选择 Java 语言，则分配的业务服务器里已经部署了本 SDK 和 Demo 的发行版本。

## API

请参考[线上 API 文档][api-url]。

## 使用示例（Servlet）

### 配置 SDK

SDK 必须经过初始化配置之后才能使用。可以选择使用代码初始化或者配置文件初始化。初始化配置建议在 `Servlet::init()` 里进行。

使用代码初始化：

```java
import com.qcloud.weapp.*;

Configuration configuration = new Configuration();

// 业务服务器访问域名
configuration.setServerHost("199447.qcloud.la");
// 鉴权服务地址
configuration.setAuthServerUrl("http://10.0.12.135/mina_auth/");
// 信道服务地址
configuration.setTunnelServerUrl("https://ws.qcloud.com/");
// 信道服务签名 key
configuration.setTunnelSignatureKey("my$ecretkey");
// 网络请求超时设置，单位为秒
configuration.setNetworkTimeout(30);

ConfigurationManager.setup(configuration);
```

使用配置文件初始化：

```java
import com.qcloud.weapp.*;

var configFilePath = "/etc/qcloud/sdk.config";
ConfigurationManager.setupFromFile(configFilePath);
```

关于 SDK 配置字段的含义以及配置文件格式的更多信息，请参考[服务端 SDK 配置][sdk-config-wiki]。

### 使用 SDK 提供登录服务

#### 登录

业务服务器提供一个路由处理客户端的登录请求，直接把该请求交给 SDK 来处理即可完成登录。登录成功后，可以获取用户信息。

```java
import com.qcloud.weapp.*;
import com.qcloud.weapp.authorization.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    /**
     * 处理登录请求
     * */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 通过 ServletRequest 和 ServletResponse 初始化登录服务
        LoginService service = new LoginService(request, response);
        try {
            // 调用登录接口，如果登录成功可以获得登录信息
            UserInfo userInfo = service.login();
            System.out.println("========= LoginSuccess, UserInfo: ==========");
            System.out.println(userInfo.toString());
        } catch (LoginServiceException e) {
            // 登录失败会抛出登录失败异常
            e.printStackTrace();
        } catch (ConfigurationException e) {
            // SDK 如果还没有配置会抛出配置异常
            e.printStackTrace();
        }
    }
}
```

> 如果登录失败，[login()][login-api] 方法会抛出异常，需要使用 try-catch 来捕获异常。该异常可以不用处理，抛出来是为了方便业务服务器可以进行记录和监控。

#### 获取会话状态

客户端交给业务服务器的请求，业务服务器可以通过 SDK 来检查该请求是否包含合法的微信小程序会话。如果包含，则会返回会话对应的用户信息。

```java
import com.qcloud.weapp.*;
import com.qcloud.weapp.authorization.*;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    /**
     * 从请求中获取会话中的用户信息
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginService service = new LoginService(request, response);        
        try {
            // 调用检查登录接口，成功后可以获得用户信息，进行正常的业务请求
            UserInfo userInfo = service.check();
            
            // 获取会话成功，输出获得的用户信息            
            JSONObject result = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("userInfo", new JSONObject(userInfo));
            result.put("code", 0);
            result.put("message", "OK");
            result.put("data", data);            
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(result.toString());
            
        } catch (LoginServiceException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}

```

> 如果检查会话失败，或者会话无效，[Check()][check-api] 方法会抛出异常，需要使用 try-catch 来捕获异常。该异常可以不用处理，抛出来是为了方便业务服务器可以进行记录和监控。

阅读解决方案文档中的[鉴权服务][auth-service-wiki]了解更多解决方案中关于鉴权服务的技术资料。

### 使用 SDK 提供信道服务

业务在一个路由上提供信道服务，只需把该路由上的请求都交给 SDK 的信道服务处理即可。

```java
import com.qcloud.weapp.*;
import com.qcloud.weapp.tunnel.*;
import com.qcloud.weapp.demo.ChatTunnelHandler;

@WebServlet("/tunnel")
public class TunnelServlet extends HttpServlet {
    /**
     * 把所有的请求交给 SDK 处理，提供 TunnelHandler 处理信道事件
     */
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 创建信道服务处理信道相关请求
        TunnelService tunnelService = new TunnelService(request, response);
        
        try {
            // 配置是可选的，配置 CheckLogin 为 true 的话，会在隧道建立之前获取用户信息，以便业务将隧道和用户关联起来
            TunnelHandleOptions options = new TunnelHandleOptions();
            options.setCheckLogin(true);
            
            // 需要实现信道处理器，ChatTunnelHandler 是一个实现的范例
            tunnelService.handle(new ChatTunnelHandler(), options);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }
}
```

使用信道服务需要实现处理器，来获取处理信道的各种事件，具体可参考接口 [TunnelHandler][tunnel-handler-api] 的 API 文档以及配套 Demo 中的 [ChatTunnelHandler][chat-handler-source] 的实现。

阅读解决方案文档中的[信道服务][tunnel-service-wiki]了解更多解决方案中关于鉴权服务的技术资料。

## 反馈和贡献

如有问题，欢迎使用 [Issues][new-issue] 提出，也欢迎广大开发者给我们提 [Pull Request][pr]。

[weapp-solution]: https://github.com/tencentyun/weapp-solution "查看腾讯云微信小程序解决方案"
[sdk-download]: https://github.com/tencentyun/weapp-java-server-sdk/archive/master.zip "下载 Java SDK 源码"
[la-console]: https://console.qcloud.com/la "打开腾讯云微信小程序一站式解决方案控制台"
[api-url]: https://tencentyun.github.io/weapp-java-server-sdk/api/ "查看 Java SDK API 文档"
[sdk-config-wiki]: https://github.com/tencentyun/weapp-solution/wiki/%E6%9C%8D%E5%8A%A1%E7%AB%AF-SDK-%E9%85%8D%E7%BD%AE "查看服务端 SDK 配置"
[auth-service-wiki]: https://github.com/tencentyun/weapp-solution/wiki/%E9%89%B4%E6%9D%83%E6%9C%8D%E5%8A%A1 "查看关于鉴权服务的更多资料"
[tunnel-service-wiki]: https://github.com/tencentyun/weapp-solution/wiki/%E9%89%B4%E6%9D%83%E6%9C%8D%E5%8A%A1 "查看关于信道服务的更多资料"
[login-api]: https://tencentyun.github.io/weapp-java-server-sdk/api/com/qcloud/weapp/authorization/LoginService.html#login-- "查看 LoginService::login() 方法 API"
[check-api]: https://tencentyun.github.io/weapp-java-server-sdk/api/com/qcloud/weapp/authorization/LoginService.html#check-- "查看 LoginService::ckeck() 方法 API"
[tunnel-handler-api]: https://tencentyun.github.io/weapp-java-server-sdk/api/com/qcloud/weapp/tunnel/TunnelHandler.html "查看 TunnelHandler 接口 API 文档"
[chat-handler-source]: https://github.com/tencentyun/weapp-java-server-sdk/blob/master/com.qcloud.weapp.demo/src/com/qcloud/weapp/demo/ChatTunnelHandler.java "查看 ChatTunnelHandler 示例代码"

[new-issue]: https://github.com/CFETeam/qcloud-weapp-server-sdk-csharp/issues/new "反馈建议和问题"
[pr]: https://github.com/CFETeam/qcloud-weapp-server-sdk-csharp/pulls "创建