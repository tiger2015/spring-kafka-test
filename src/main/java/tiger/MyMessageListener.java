package tiger;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName MyMessageListener
 * @Description TODO
 * @Author zeng.h
 * @Date 2019/10/11 19:10
 * @Version 1.0
 **/
@Slf4j
@Component
public class MyMessageListener {

    @KafkaListener(topics = {"cors_msg"}, containerFactory = "containerFactory")
    public void onMessage(ConsumerRecords<String, String> records) {
        for (ConsumerRecord<String, String> record : records) {
            if (record.value().contains("cors:8:")) {
                log.info("message:key:"+record.key()+", value:" + record.value() + ", partition:" + record.partition() + ",offset:" + record.offset());
            }
        }
    }
}
