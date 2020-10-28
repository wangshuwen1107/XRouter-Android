![](media/XRouter.png)

# XRRouter

#### 最新版本
|  模块 | xrouter_annotation  |  xrouter_complier|xrouter_core|
| ------------ | ------------ | ------------ | ------------ |
| 最新版本  |[ ![Download](https://api.bintray.com/packages/wenwen/maven/annotation/images/download.svg) ](https://bintray.com/wenwen/maven/annotation/_latestVersion)   |  [ ![Download](https://api.bintray.com/packages/wenwen/maven/compiler/images/download.svg) ](https://bintray.com/wenwen/maven/compiler/_latestVersion) |[ ![Download](https://api.bintray.com/packages/wenwen/maven/core/images/download.svg) ](https://bintray.com/wenwen/maven/core/_latestVersion)   |

#### 添加依赖和配置

1.root project build.gradle
 ```  gradle
allprojects {
    repositories {
        jcenter()
    }
}
 ```

2.module build.gradle

 ```  gradle
dependencies {
    implementation 'cn.cheney.xrouter:core:x.x.x'
    annotationProcessor 'cn.cheney.xrouter:compiler:x.x.x'
}
 ```
#### 功能&说明
url : `scheme://moduleName/methodName?key=value`
* 协议方法必须为static
* 支持任意数据类型路由传参



#### 功能使用
1.初始化
 ```java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        XRouter.init(this, "scheme");
        //增加拦截器
        XRouter.getInstance().addInterceptor(new RouterInterceptor() {
            @Override
            public Invokable intercept(Chain chain) {
                BaseCall call = chain.call();
                String urlStr = call.getUri().toString();
                Logger.d(" RouterInterceptor urlStr=" + urlStr);
                return chain.proceed(call);
            }
        });
        //全局路由错误处理
        XRouter.getInstance().setErroHandler(new RouterErrorHandler() {
            @Override
            public void onError(String url, String errorMsg) {
                Logger.e("Url:" + url + " errorMsg:" + errorMsg);
            }
        });
    }
}
 ```
2.注解
 ```java
@XRoute(path = "pageName", module = "moduleName")
public class YourActivity extends Activity {

    @XParam(name = "paramObjectName")
    Object book;
    @XParam(name = "paramStrName")
    String paramStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xxx);
        XRouter.getInstance().inject(this);
    }
}

@XRoute(module = "moduleName")
public class YourModule{
    //同步方法
    @XMethod(name = "methodA")
    public static Object methodA(@XParam(name = "book") String paramValue) {
        return "";
    }
	//异步方法，必须包含requestId参数
    @XMethod(name = "asyncMethodB")
	public static void asyncMethodB(@XParam(name = XParam.RequestId) String requestId) {
        XRouter.getInstance().invokeCallback(requestId, new HashMap());
    }
}
 ```

 3.调用
```java
//跳转界面
Integer requestCode = XRouter.page("moduleName/methodName")
                        .put(key, value)
                        .action("cn.cheney.xrouter")
                        .anim(R.anim.enter_bottom, R.anim.exit_bottom)
                        .requestCode(1000)
                        .call();
 //调用同步方法
T result = XRouter.<T>method("moduleName/methodName")
                 .put(key, value)
                 .call();

 //调用同步方法
 XRouter.<T>method("moduleName/methodName")
                        .put(key, value)
                        .call(new RouteCallback() {
                            @Override
                            public void onResult(Map<String, Object> result) {

                            }
                        });


```
#### 混淆配置
```
内部已有混淆规则，不用配置
```
#### 致谢
* JavaPoet，感谢提供高效的生成代码方式
* ARouter，感谢提供了inject思路



