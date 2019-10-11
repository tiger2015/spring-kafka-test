package tiger;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName MyKafkaBatchMessageListener
 * @Description TODO
 * @Author zeng.h
 * @Date 2019/10/11 15:43
 * @Version 1.0
 **/
@Component(value = "messageListener")
@Slf4j
public class MyKafkaBatchMessageListener implements BatchMessageListener<String, String>, ConsumerSeekAware {
    @Override
    public void onMessage(List<ConsumerRecord<String, String>> data) {
        for (ConsumerRecord<String, String> record : data) {
            if (record.key().equals("392")) {
                log.info("message:" + record.value() + ", partition:" + record.partition() + ",offset:" + record.offset());
            }
        }
    }

    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {

    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {

        assignments.forEach((k, v) -> {
            callback.seekToEnd(k.topic(), k.partition());
        });

    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {

    }

}
