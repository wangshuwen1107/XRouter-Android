```
多模块路由通讯框架
```

#### 最新版本
|  模块 | xrouter_annotation  |  xrouter_complier|xrouter_core|
| ------------ | ------------ | ------------ | ------------ |
| 最新版本  |[ ![Download](https://api.bintray.com/packages/wenwen/maven/annotation/images/download.svg) ](https://bintray.com/wenwen/maven/annotation/_latestVersion)   |  [ ![Download](https://api.bintray.com/packages/wenwen/maven/compiler/images/download.svg) ](https://bintray.com/wenwen/maven/compiler/_latestVersion) |[ ![Download](https://api.bintray.com/packages/wenwen/maven/core/images/download.svg) ](https://bintray.com/wenwen/maven/core/_latestVersion)   |

#### 添加依赖和配置

1.root project build.gradle
 ```  gradle
buildscript {
    repositories {
       ...
    }
    dependencies {
        ...
    }
}

allprojects {
    repositories {
        maven {
            url 'https://dl.bintray.com/wenwen/maven'
        }
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
1.目前只支持activity,method的路由
2.采用协议调用无需依赖任何接口协议

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
//此时对应路由scheme://test/page1
@XRoute(path = "page1", module = "test")
public class TestActivity extends Activity {

    @XParam(name = "testParam")
    Book book;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtest_activity_1);
        XRouter.getInstance().inject(this);
    }
}

//此时对应路由scheme://home/getBookName
@XRoute(module = "home")
public class HomeMethod implements IMethod {
    //同步方法
    @XMethod(name = "getBookName")
    public static Book getBookName(@XParam(name = "book") Book book) {
        return book;
    }
	//异步方法，必须包含RouteCallback参数
    @XMethod(name = "getAsyncBookName")
	public static void getAsyncBookName(@XParam(name = "book") Book book, RouteCallback callback) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("book", book);
        callback.onResult(map);
    }
}
 ```
 3.调用
```java
//跳转界面
Integer requestCode = XRouter.page("home/page")
                        .put("testParam", book)
                        .action("cn.cheney.xrouter")
                        .anim(R.anim.enter_bottom, R.anim.exit_bottom)
                        .requestCode(1000)
                        .call();
 //调用同步方法                     
 Book book = new Book();
                book.name = "Kotlin";
                Book bookReturn = XRouter.<Book>method("home/getBookName")
                        .put("book", book)
                        .call();
                Logger.d("getSyncBookName bookReturn= " + bookReturn);
 //调用同步方法
 XRouter.<Book>method("home/getAsyncBookName")
                        .put("book", book)
                        .call(new RouteCallback() {
                            @Override
                            public void onResult(Map<String, Object> result) {
                                Logger.d("getAsyncBookName bookReturn= " + result);
                            }
                        });

```



