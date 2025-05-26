package cn.yusiwen.commons.mapper.testcontainers;

import org.testcontainers.utility.DockerImageName;

public class OpenGaussContainer<SELF extends OpenGaussContainer<SELF>> extends CustomJdbcDatabaseContainer<SELF> {

    private static final String DRIVER = "org.postgresql.Driver";

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("opengauss/opengauss");

    @Deprecated
    public static final String DEFAULT_TAG = "5.0.0";

    @Deprecated
    public static final String IMAGE = DEFAULT_IMAGE_NAME.getUnversionedPart();

    public static Integer PORT = 5432;

    static final String DEFAULT_USER = "gaussdb";

    static final String DEFAULT_PASSWORD = "openGauss@123";

    private String databaseName = "test";

    private String username = DEFAULT_USER;

    private String password = DEFAULT_PASSWORD;

    @Deprecated
    public OpenGaussContainer() {
        this(DEFAULT_IMAGE_NAME.withTag(DEFAULT_TAG));
    }

    public OpenGaussContainer(String dockerImageName) {
        this(DockerImageName.parse(dockerImageName));
    }

    public OpenGaussContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);

        addExposedPort(PORT);
    }

    @Override
    protected void configure() {
        addEnv("GS_DB", databaseName);
        addEnv("GS_PASSWORD", password);
    }

    @Override
    public String getDriverClassName() {
        return DRIVER;
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:postgresql://" + getHost() + ":" + getMappedPort(PORT) + "/" + databaseName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
        return self();
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }
}
