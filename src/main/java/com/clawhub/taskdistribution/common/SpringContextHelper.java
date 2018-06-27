package com.clawhub.taskdistribution.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <Description> 获取容器中的ApplicationContext <br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年06月27日<br>
 */
public class SpringContextHelper implements ApplicationContextAware {
    /**
     * appCtx
     */
    private static ApplicationContext appCtx;

    /**
     * Description: Set application context <br>
     *
     * @param applicationContext application context
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHelper.setAppCtx(applicationContext);
    }

    /**
     * Description: Set app ctx <br>
     *
     * @param applicationContext application context
     * @author LiZhiming <br>
     * @taskId <br>
     */
    private static void setAppCtx(ApplicationContext applicationContext) {
        appCtx = applicationContext;
    }

    /**
     * Description: Get application context <br>
     *
     * @return application context
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    /**
     * Description: Get bean <br>
     *
     * @param beanName bean name
     * @return object
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public static Object getBean(String beanName) {
        return appCtx.getBean(beanName);
    }

    /**
     * Description: Get bean <br>
     *
     * @param <T>   type parameter
     * @param clazz clazz
     * @return t
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public static <T> T getBean(Class<T> clazz) {
        return appCtx.getBean(clazz);
    }
}
