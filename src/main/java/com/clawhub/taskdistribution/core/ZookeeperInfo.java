package com.clawhub.taskdistribution.core;

import com.alibaba.fastjson.JSONObject;
import com.clawhub.taskdistribution.common.IDGenarator;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.Participant;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * <Description> zk信息 <br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年06月27日<br>
 */
@Component
@EnableScheduling
public class ZookeeperInfo {
    /**
     * The constant log.
     */
    private static Logger log = LoggerFactory.getLogger(ZookeeperInfo.class);
    /**
     * The Server id.
     */
    private String serverId = "Client # " + IDGenarator.getID();

    /**
     * The Client.
     */
    private CuratorFramework client;

    /**
     * The Leader latch.
     */
    private LeaderLatch leaderLatch;
    /**
     * 任务队列
     */
    private DistributedQueue<String> queue = null;
    /**
     * 是否是leader 默认为false
     */
    private boolean isLeader = false;
    /**
     * The Global name space.
     */
    @Value("${task.distribution.zk.global.name.space}")
    private String globalNameSpace;

    /**
     * The Leader path.
     */
    @Value("${task.distribution.zk.leader.path}")
    private String leaderPath;
    /**
     * The Queue path.
     */
    @Value("${task.distribution.zk.queue.path}")
    private String queuePath;
    /**
     * The Lock path.
     */
    @Value("${task.distribution.zk.queue.lock.path}")
    private String lockPath;

    /**
     * The Connect string.
     */
    @Value("${task.distribution.connect}")
    private String connectString;
    /**
     * The Retry policy base sleep time ms.
     */
    @Value("${task.distribution.retry.policy.base.sleep.time.ms}")
    private int retryPolicyBaseSleepTimeMs;
    /**
     * The Retry policy max retries.
     */
    @Value("${task.distribution.retry.policy.max.retries}")
    private int retryPolicyMaxRetries;
    /**
     * The Session timeout ms.
     */
    @Value("${task.distribution.session.timeout.ms}")
    private int sessionTimeoutMs;
    /**
     * The Connection timeout ms.
     */
    @Value("${task.distribution.connection.timeout.ms}")
    private int connectionTimeoutMs;

    /**
     * The Queue consumer client.
     */
    @Autowired
    private QueueConsumerClient queueConsumerClient;

    /**
     * Description: 系统初始化 <br>
     *
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @PostConstruct
    public void init() {
        log.info("===================init===================");
        try {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(retryPolicyBaseSleepTimeMs, retryPolicyMaxRetries);
            client = CuratorFrameworkFactory.builder()
                    .connectString(connectString)
                    .retryPolicy(retryPolicy)
                    .sessionTimeoutMs(sessionTimeoutMs)
                    .connectionTimeoutMs(connectionTimeoutMs)
                    .namespace(globalNameSpace)
                    .build();
            client.start();
            leaderLatch = new LeaderLatch(client, leaderPath, serverId);
            leaderLatch.start();
            //定义消费队列
            QueueConsumer<String> consumer = queueConsumerClient.createQueueConsumer();
            QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, queueConsumerClient.createQueueSerializer(), queuePath).lockPath(lockPath);
            queue = builder.buildQueue();
            queue.start();
        } catch (Exception ex) {
            log.error("setUp Exception", ex);
        }
    }

    /**
     * Description: 周期性选主 <br>
     *
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @Scheduled(fixedDelayString = "${task.distribution.scheduled.check.leader.fixed.delay}")
    public void checkLeader() {
        log.info("==================checkLeader====================");
        try {
            //首先利用serverId检查自己是否还存在于leader latch选举结果集中
            //考虑网络阻塞，zk数据异常丢失等情况
            boolean isExist = false;
            Collection<Participant> participants = leaderLatch.getParticipants();
            for (Participant participant : participants) {
                if (serverId.equals(participant.getId())) {
                    isExist = true;
                    break;
                }
            }
            //如果不存在，则重新加入选举
            if (!isExist) {
                log.info("Current server does not exist on zk, reset leader latch");
                leaderLatch.close();
                leaderLatch = new LeaderLatch(client, leaderPath, serverId);
                leaderLatch.start();
                log.info("Successfully reset leader latch");
            }
            //查看当前leader是否是自己
            //注意，不能用leaderLatch.hasLeadership()因为有zk数据丢失的不确定性
            //利用serverId对比确认是否主为自己
            Participant leader = leaderLatch.getLeader();
            boolean hashLeaderShip = serverId.equals(leader.getId());
            if (log.isInfoEnabled()) {
                log.info("Current Participant: {}", JSONObject.toJSONString(participants));
                log.info("Current Leader: {}", leader);
            }
            if (hashLeaderShip) {
                isLeader = true;
            } else {
                isLeader = false;
            }
        } catch (Exception ex) {
            log.error("checkLeader Exception", ex);
        }
    }


    /**
     * Description: 获取任务队列 <br>
     *
     * @return distributed queue
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public DistributedQueue<String> getQueue() {
        return queue;
    }


    /**
     * Description: 判断是否为主 <br>
     *
     * @return boolean
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public boolean isLeader() {
        return isLeader;
    }
}