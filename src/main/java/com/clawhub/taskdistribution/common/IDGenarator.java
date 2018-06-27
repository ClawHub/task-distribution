package com.clawhub.taskdistribution.common;

import java.util.UUID;

/**
 * <Description> ID生成器 <br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年06月27日<br>
 */
public class IDGenarator {
    /**
     * 私有构造器
     */
    private IDGenarator() {
    }

    /**
     * Description: 生成32位uuid<br>
     *
     * @return string
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public static String getID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
