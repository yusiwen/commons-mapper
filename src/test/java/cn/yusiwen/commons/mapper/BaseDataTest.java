package cn.yusiwen.commons.mapper;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

public class BaseDataTest {

    public static void runScript(DataSource ds, String resource) throws IOException, SQLException {
        try (Connection connection = ds.getConnection()) {
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(true);
            runner.setStopOnError(false);
            runner.setLogWriter(null);
            runner.setErrorLogWriter(null);
            runScript(runner, resource);
        }
    }

    public static void runScript(ScriptRunner runner, String resource) throws IOException, SQLException {
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            runner.runScript(reader);
        }
    }

    public static void printClassSource(ClassLoader classLoader, String className) {
        try {
            // 1. 加载目标类
            Class<?> clazz = Class.forName(className, true, classLoader);

            // 2. 获取类所在的 JAR 路径
            String jarPath = clazz.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();

            System.out.printf("类 [%s] 来源于 JAR: %s\n", className, jarPath);

        } catch (ClassNotFoundException e) {
            System.err.println("类未找到: " + className);
        } catch (Exception e) {
            System.err.println("获取 JAR 路径失败: " + e.getMessage());
        }
    }

}
