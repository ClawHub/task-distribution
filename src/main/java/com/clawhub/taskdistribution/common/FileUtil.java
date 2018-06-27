package com.clawhub.taskdistribution.common;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * <Description> 文件处理工具 <br>
 *
 * @author LiZhiming<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2018年06月23日<br>
 */
public class FileUtil {
    /**
     * Description: 获取资源文件 <br>
     *
     * @param location location
     * @return properties
     * @throws java.io.IOException io exception
     * @author LiZhiming <br>
     * @taskId <br>
     */
    public static Properties getResource(String location) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        return PropertiesLoaderUtils.loadProperties(resource);
    }
}
