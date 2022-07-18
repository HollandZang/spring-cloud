import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.Properties;

public class PubConf {
    public static void main(String[] args) throws NacosException {
        final Properties properties = new Properties();
        properties.put("serverAddr", "localhost.vm:8848");
        properties.put("namespace", "public");
        ConfigService configService = NacosFactory.createConfigService(properties);
        final String content = configService.getConfig("gateway", "DEFAULT_GROUP", 3000);

        final Properties properties1 = new Properties();
        properties1.put("serverAddr", "localhost.vm:8848");
        properties1.put("namespace", "public");
        ConfigService configService1 = NacosFactory.createConfigService(properties);
        final boolean b = configService1.publishConfig("gateway_1", "DEFAULT_GROUP", content, ConfigType.PROPERTIES.getType());
        System.out.println(b);
    }
}
