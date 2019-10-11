package tiger;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;


/**
 * @ClassName ConsumeTask
 * @Description TODO
 * @Author zeng.h
 * @Date 2019/10/11 14:32
 * @Version 1.0
 **/
@Component
@EnableScheduling
@Slf4j
public class ConsumeTask {

    @Autowired
    @Qualifier("consumer")
    private Consumer consumer;

    @Scheduled(fixedRate = 100)
    public void consume() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ZERO);
        for (ConsumerRecord<String, String> record : records) {
            if (record.key().equals("392")) {
                log.info("message:" + record.value() + ", partition:" + record.partition() + ",offset:" + record.offset());
            }
        }
    }
}
