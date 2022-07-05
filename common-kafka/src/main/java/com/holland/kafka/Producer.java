package com.holland.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;

import java.util.Properties;

public class Producer {

    public static void main(String[] args) {
        new Producer().exec("quickstart-events", null, null, null, "test", null);
    }

    protected String bootstrapServers = "localhost:9092";
    protected String groupId = "group-4";

    public void exec(String topic, Integer partition, Long timestamp, String key, String value, Iterable<Header> headers) {
        Properties properties = new Properties();
        //主机信息
        properties.put("bootstrap.servers", bootstrapServers);
        //群组id
        properties.put("group.id", groupId);
        /**
         *消费者是否自动提交偏移量，默认是true
         * 为了经量避免重复数据和数据丢失，可以把它设为true,
         * 由自己控制核实提交偏移量。
         * 如果设置为true,可以通过auto.commit.interval.ms属性来设置提交频率
         */
        properties.put("enable.auto.commit", "false");
        /**
         * 自动提交偏移量的提交频率
         */
        properties.put("auto.commit.interval.ms", "1000");
        /**
         * 默认值latest.
         * latest:在偏移量无效的情况下，消费者将从最新的记录开始读取数据
         * erliest:偏移量无效的情况下，消费者将从起始位置读取分区的记录。
         */
        properties.put("auto.offset.reset", "earliest");
        /**
         * 消费者在指定的时间内没有发送心跳给群组协调器，就被认为已经死亡，
         * 协调器就会触发再均衡，把它的分区分配给其他消费者。
         */
        properties.put("session.timeout.ms", "30000");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (final KafkaProducer<Object, Object> producer = new KafkaProducer<>(properties)) {
            producer.send(new ProducerRecord<>(topic, partition, timestamp, key, value, headers), (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println(metadata);
                }
            });
        }
    }
}
