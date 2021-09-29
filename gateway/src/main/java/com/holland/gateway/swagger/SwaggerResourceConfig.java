package com.holland.gateway.swagger;

import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class SwaggerResourceConfig implements SwaggerResourcesProvider {
    @Resource
    private DiscoveryClient discoveryClient;

    @Override
    public List<SwaggerResource> get() {
        /**
         * 先把自身加入swagger
         * 在通过发现服务找到其他需要添加到swagger的模块，同时排除掉没有swagger模块防止报错
         * swagger调用地址应该是`/v2/api-docs`，但是knife4j访问其他模块时请求地址会有异常，所以用`/swagger/v2/api-docs`调原接口并修改参数里面的地址
         */
        List<SwaggerResource> resources = new ArrayList<>() {{
            add(swaggerResource("gateway", "/v2/api-docs"));
        }};

        discoveryClient.getServices()
                .stream()
                .filter(s -> !"eureka".equals(s) && !"admin".equals(s) && !"gateway".equals(s))
                .forEach(appName -> resources.add(swaggerResource(appName, appName + "/swagger/v2/api-docs")));

        return resources;
    }

    /**
     * 此写法只能获取配置文件里面的路由规则
     */
   /*@Resource
    private RouteLocator routeLocator;
    @Resource
    private GatewayProperties gatewayProperties;

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>() {{
            add(swaggerResource("gateway", "/v2/api-docs"));
        }};

        List<String> routes = new ArrayList<>();
        routeLocator.getRoutes().subscribe(route -> routes.add(route.getId()));
        gatewayProperties.getRoutes().stream().filter(routeDefinition -> routes.contains(routeDefinition.getId())).forEach(route -> {
            route.getPredicates().stream()
                    .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                    .forEach(predicateDefinition -> resources.add(swaggerResource(route.getId(),
                            predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                    .replace("**", "v2/api-docs"))));
        });
        return resources;
    }*/
    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}