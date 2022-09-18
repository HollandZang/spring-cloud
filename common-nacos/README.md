# spring 集成 nacos 的配置组件

## 使用方式

1. `spring.factories`中配置

```properties
org.springframework.boot.env.EnvironmentPostProcessor=com.holland.nacos.conf.NacosEnvironmentPostProcessor
```

2. 配置文件

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: 114.115.212.83:8848
        group: DEFAULT_GROUP
        namespace: e45ec5af-12d8-4b45-a1f4-5eea3cf7a816
      discovery:
        server-addr: ${spring.cloud.nacos.config.server-addr}
```

3. spring启动前配置`prop`类

```java
/**
 * prop类
 * @apiNote public static 配置的都是对应的 nacos 的 Data Id
 */
public class NacosProp {
    public static Properties gateway;
    public static Properties gateway_router;
}
```

```java
// 启动类
public static void main(String[]args){
        NacosPropKit.setInstance(NacosProp.class);
        SpringApplication.run(GatewayApplication.class,args);
        }
```

4. 配置监听
```java
NacosPropKit.listen(NacosEnvironmentPostProcessor.configService, "你的Group", "你的Data Id", 监听函数());
```