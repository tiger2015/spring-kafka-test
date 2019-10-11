package tiger;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ApplicationConfig
 * @Description TODO
 * @Author zeng.h
 * @Date 2019/10/11 13:55
 * @Version 1.0
 **/
@Configuration
@PropertySource(value = "classpath:application.properties")
@ComponentScan(basePackages = {"tiger"})
@EnableKafka
@Slf4j
public class ApplicationConfig {


    @Value("${kafka.bootstrap-servers}")
    private String kafkaBootStrapServer;

    @Value("${kafka.topic}")
    private String[] topics;

    @Value("${kafka.consumer.group.id}")
    private String groupId;

    @Value("${kafka.consumer.enable.auto.commit}")
    private boolean enableAutoCommit;

    @Value("${kafka.consumer.auto.commit.interval.ms}")
    private int autoCommitInterval;

    @Value("${kafka.consumer.auto.offset.reset}")
    private String autoOffsetReset;

    @Value("${kafka.consumer.max.poll.records}")
    private int maxPollRecords;

    @Value("${kafka.consumer.session.timeout.ms}")
    private int sessionTimeout;

    @Value("${kafka.consumer.key.deserializer}")
    private String keyDeserializer;

    @Value("${kafka.consumer.value.deserializer}")
    private String valueDeserializer;


    private Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootStrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitInterval);
        return props;
    }


    @Bean
    public Consumer consumer() {
        Consumer consumer = new KafkaConsumer(consumerConfig());
//        consumer.subscribe(Arrays.asList(topics));
//        Set<TopicPartition> assignment = new HashSet<>();
//        while (assignment.size() == 0) {
//            consumer.poll(Duration.ofMillis(3000));
//            assignment = consumer.assignment();
//        }

//
        final List<TopicPartition> list = new ArrayList<>();
        for (String topic : topics) {
            List<PartitionInfo> list1 = consumer.partitionsFor(topic);
            list1.forEach(partition -> {
                log.info("partition info:" + partition.toString());
                list.add(new TopicPartition(partition.topic(), partition.partition()));
            });
        }
        consumer.assign(list);

        consumer.seekToEnd(list);

//        final Map<TopicPartition, Long> map = new HashMap<>();
//        list.forEach(partition -> map.put(partition, System.currentTimeMillis() - 4 * 3600 * 1000L));
//        Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = consumer.offsetsForTimes(map);
//        offsetsForTimes.forEach((partition, offset) -> {
//            consumer.seek(partition, offset.offset());
//            log.info("partition:" + partition.toString() + ", offset:" + offset.offset());
//        });
        return consumer;
    }


    private ConsumerFactory consumerFactory() {
        return new DefaultKafkaConsumerFactory(consumerConfig());
    }


    // @Bean
    public ConcurrentMessageListenerContainer<String, String> container(@Autowired BatchMessageListener<String,
            String> messageListener) {
        ContainerProperties containerProperties = new ContainerProperties(topics);
        containerProperties.setMessageListener(messageListener);
        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProperties);
        container.setConcurrency(8);
        return container;

    }


}
