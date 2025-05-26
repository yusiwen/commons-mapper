package cn.yusiwen.commons.mapper.query.opengauss;

import cn.yusiwen.commons.mapper.BaseDataTest;
import cn.yusiwen.commons.mapper.query.Mapper;
import cn.yusiwen.commons.mapper.query.User;
import cn.yusiwen.commons.mapper.testcontainers.OpenGaussContainer;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("OpenGaussQueryTest")
class OpenGaussQueryTest {

    private static SqlSessionFactory sqlSessionFactory;

    private static final OpenGaussContainer<?> container = new OpenGaussContainer<>();

    @BeforeAll
    static void setUp() throws SQLException, IOException {

        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{Paths.get("lib/opengauss-jdbc-5.0.3.jar").toUri().toURL()},
                OpenGaussQueryTest.class.getClassLoader());
        BaseDataTest.printClassSource(classLoader, "org.postgresql.Driver");

        container.withClassloader(classLoader);
        container.start();

        Configuration configuration = new Configuration();
        DataSource dataSource = new UnpooledDataSource(classLoader, container.getDriverClassName(),
                container.getJdbcUrl(), container.getUsername(), container.getPassword());
        Environment environment = new Environment("test", new JdbcTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);
        configuration.addMapper(Mapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
                "cn/yusiwen/commons/mapper/db/opengauss/CreateDB.sql");
    }

    @AfterAll
    static void clearUp() {
        container.stop();
    }

    @Test
    void testQueryList() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            {
                List<User> users = mapper.selectUsers();
                User user1 = users.get(0);
                assertEquals("User1", user1.getName());
            }
        }
    }

    @Test
    void testQueryById() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Mapper mapper = sqlSession.getMapper(Mapper.class);
            {
                User user1 = mapper.queryById(1L);
                assertEquals("User1", user1.getName());
            }
        }
    }
}
