![](media/XRouter.png)

# XRRouter
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](https://www.apache.org/licenses/LICENSE-2.0)

#### 最新版本

|  模块 | xrouter-annotation  |  xrouter-compiler|xrouter-core|xrouter-plugin|
| ------------ | ------------ | ------------ | ------------ |------------ |
| 最新版本 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-annotation/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-annotation) |[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-compiler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-compiler)  |[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-core) |[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.wangshuwen1107/xrouter-plugin)|

#### 添加依赖和配置

1.root project build.gradle

```gradle
allprojects {
   repositories {
       mavenCentral()
   }
  dependencies {
        classpath "io.github.wangshuwen1107:xrouter-plugin:x.x.x"
    }
}
```

2.module build.gradle

```gradle
dependencies {
   implementation 'io.github.wangshuwen1107:xrouter-core:x.x.x'
   annotationProcessor 'io.github.wangshuwen1107:xrouter-compiler:x.x.x'
}
```

3.application build.gradle

```gradle
apply plugin: 'XRouter'
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
       XRouter.init(this, "scheme");  
   }
}
```

2.路由注册

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

3.拦截器注册

```java
@XInterceptor(modules = {"moduleA"}, priority = 1)
public class TestInterceptorA implements RouterInterceptor {

    @Override
    public Object intercept(Chain chain) {
        Logger.d("TestInterceptorA url=" + chain.call().getUri().toString());
        return chain.proceed();
    }

}
```



4.路由调用

```java
//跳转界面
Integer requestCode = XRouter.page("moduleName/methodName")
                        .put(key, value)
                        .action("cn.cheney.xrouter")
                        .anim(R.anim.enter_bottom, R.anim.exit_bottom)
                        .requestCode(1000)
                        .call();
 //同步调用
T result = XRouter.<T>method("moduleName/methodName")
                 .put(key, value)
                 .call();

 //异步调用
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



