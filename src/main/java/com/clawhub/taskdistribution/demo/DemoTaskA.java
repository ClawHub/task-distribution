package com.clawhub.taskdistribution.demo;

import com.clawhub.taskdistribution.core.SimpleTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * <Description> 测试任务 <br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年06月27日<br>
 */
@Component
public class DemoTaskA implements SimpleTask {
    /**
     * The constant log.
     */
    private static Logger log = LoggerFactory.getLogger(DemoTaskA.class);

    /**
     * Description: 执行任务 <br>
     *
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @Override
    public void execute() {
        log.info("=======================task=====A===================");
    }
}
