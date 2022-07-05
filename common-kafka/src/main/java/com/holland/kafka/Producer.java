package com.holland.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Producer {

    public static void main(String[] args) {
        new Producer("localhost:9092", "group-4")
                .exec("quickstart-events", null, null, null, "test1", null, (metadata, exception) -> {
                });
    }

    protected final Logger logger = LoggerFactory.getLogger(Producer.class);

    protected final String bootstrapServers;
    protected final String groupId;
    protected final KafkaProducer<Object, Object> producer;

    public Producer(String bootstrapServers, String groupId) {
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;

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
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.producer = new KafkaProducer<>(properties);
    }

    public void exec(String topic, String value) {
        producer.send(new ProducerRecord<>(topic, value), (metadata, exception) -> {
            if (exception != null) {
                logger.error("topis=" + topic + ", value=" + value, exception);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("topic={}, value={}", topic, value);
                }
            }
        });
    }

    public void exec(String topic, String value, Callback callback) {
        producer.send(new ProducerRecord<>(topic, value), callback);
    }

    public void exec(String topic, Integer partition, Long timestamp, String key, String value, Iterable<Header> headers, Callback callback) {
        producer.send(new ProducerRecord<>(topic, partition, timestamp, key, value, headers), callback);
    }
}
