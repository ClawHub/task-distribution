package com.clawhub.taskdistribution.core;

import com.clawhub.taskdistribution.common.SpringContextHelper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <Description> 任务队列消费者 <br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreteDate 2018年06月27日<br>
 */
@Component
public class QueueConsumerClient {
    /**
     * The constant log.
     */
    private static Logger log = LoggerFactory.getLogger(QueueConsumerClient.class);
    /**
     * The Spring helper.
     */
    @Autowired
    private SpringContextHelper springHelper;

    /**
     * Description: 队列消费者 <br>
     *
     * @return queue consumer
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public QueueConsumer<String> createQueueConsumer() {
        return new QueueConsumer<String>() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                log.info("connection new state:{} ", newState.name());
            }

            @Override
            public void consumeMessage(String message) throws Exception {
                log.info("consume one message: {}", message);

                SimpleTask task;
                try {
                    task = (SimpleTask) springHelper.getBean(Class.forName(message));
                    task.execute();
                } catch (Exception e) {
                    log.error("task execute exception !", e);
                }
            }
        };
    }

    /**
     * Description: 序列化 <br>
     *
     * @return queue serializer
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public QueueSerializer<String> createQueueSerializer() {
        return new QueueSerializer<String>() {
            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        };
    }
}
