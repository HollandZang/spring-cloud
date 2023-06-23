package com.holland.admin;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.nacos.discovery.NacosServiceDiscovery;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class MyNacosServiceDiscovery extends NacosServiceDiscovery {

    private final NacosDiscoveryProperties discoveryProperties;
    private final NacosServiceManager nacosServiceManager;

    public MyNacosServiceDiscovery(NacosDiscoveryProperties discoveryProperties, NacosServiceManager nacosServiceManager) {
        super(discoveryProperties, nacosServiceManager);
        this.discoveryProperties = discoveryProperties;
        this.nacosServiceManager = nacosServiceManager;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) throws NacosException {
        String group = discoveryProperties.getGroup();
        List<Instance> instances = namingService().selectInstances(serviceId, group,
                true);
        List<ServiceInstance> serviceInstances = hostToServiceInstanceList(instances, serviceId);

        // 通过元数据，把 内网IP 替换成 外网IP(visitHost)
        {
            serviceInstances.forEach(serviceInstance -> {
                Map<String, String> metadata = serviceInstance.getMetadata();
                String ipInternet = metadata.get("ip.internet");
                if (StringUtils.hasText(ipInternet)) {
                    if (!metadata.containsKey("ip.local")) {
                        metadata.put("host", serviceInstance.getHost());
                    }
                    ((NacosServiceInstance) serviceInstance).setHost(ipInternet);
                }
            });
        }

        return serviceInstances;
    }

    private NamingService namingService() {
        return nacosServiceManager
                .getNamingService(discoveryProperties.getNacosProperties());
    }
}
