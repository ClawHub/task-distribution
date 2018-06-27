package com.clawhub.taskdistribution.core;

import com.clawhub.taskdistribution.common.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * <Description> 队列生产者 <br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年06月27日<br>
 */
@Component
public class QueueProducerClient {
    /**
     * The constant log.
     */
    private static Logger log = LoggerFactory.getLogger(QueueProducerClient.class);
    /**
     * The Zookeeper info.
     */
    @Autowired
    private ZookeeperInfo zookeeperInfo;


    @PostConstruct
    public void init() {
        log.info("===================================任务读取=============================");
        try {
            //初始化任务池
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setPoolSize(10);
            scheduler.initialize();
            //读取配置文件 将任务发送队列
            Properties properties = FileUtil.getResource("task.properties");
            String cron;
            String task;
            for (Entry<?, ?> entry : properties.entrySet()) {
                log.info("===================================任务创建=============================");
                task = (String) entry.getKey();
                cron = (String) entry.getValue();
                log.info("task:{}", task);
                log.info("cron:{}", cron);
                scheduler.schedule(new TaskRunable(task), new CronTrigger(cron));
            }
        } catch (Exception ex) {
            log.error("init Exception", ex);
        }
    }


    /**
     * <Description> 任务 <br>
     *
     * @author LiZhiming<br>
     * @version 1.0<br>
     * @taskId <br>
     * @CreateDate 2018年06月27日<br>
     */
    private class TaskRunable implements Runnable {

        /**
         * The Task.
         */
        private String task;

        /**
         * Instantiates a new Task runable.
         *
         * @param task the task
         */
        public TaskRunable(String task) {
            this.task = task;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            try {
                if (zookeeperInfo.isLeader()) {
                    log.info("Currently run as leader");
                    zookeeperInfo.getQueue().put(task);
                }
            } catch (Exception ex) {
                log.error("Task Run able Exception", ex);
            }
        }
    }


}
