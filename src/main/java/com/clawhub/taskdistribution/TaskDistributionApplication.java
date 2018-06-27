package com.clawhub.taskdistribution;

import com.clawhub.taskdistribution.common.SpringContextHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * The type Task distribution application.
 */
@SpringBootApplication
public class TaskDistributionApplication {

    /**
     * Description: Main <br>
     *
     * @param args args
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext confApp = null;
        try {
            confApp = SpringApplication.run(TaskDistributionApplication.class, args);
        } finally {
            close(confApp);
        }
    }

    /**
     * Description: 获取容器中的ApplicationContext <br>
     *
     * @return spring context helper
     * @author LiZhiming <br>
     * @taskId <br>
     */
    @Bean
    public SpringContextHelper springHelper() {
        return new SpringContextHelper();
    }

    /**
     * Description: Close <br>
     *
     * @param confApp conf app
     * @author LiZhiming <br>
     * @taskId <br>
     */
    private static void close(ConfigurableApplicationContext confApp) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (confApp != null) {
                confApp.close();
            }
        }));
    }
}
