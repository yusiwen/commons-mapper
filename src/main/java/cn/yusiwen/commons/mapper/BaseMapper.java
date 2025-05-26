package cn.yusiwen.commons.mapper;

import cn.yusiwen.commons.mapper.annotation.JSONColumn;
import cn.yusiwen.commons.mapper.annotation.NotColumn;
import cn.yusiwen.commons.mapper.annotation.PrimaryKey;
import cn.yusiwen.commons.mapper.annotation.Table;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 一个基础的 MyBatis Mapper 接口，提供了通用的数据库操作方法。
 * <p>
 * 此接口支持常见的操作如插入记录、根据主键查询等功能，同时为复杂 SQL 提供了定制化支持。
 * 开发者可以通过继承此接口，快速实现对实体类的基本 CRUD 操作，减少重复性代码。
 *
 * <p>
 * 特性：
 * <ul>
 *     <li>内置多种 SQL Provider 类，用于生成常用 SQL（如插入、查询等）。</li>
 *     <li>支持使用注解如 {@literal @InsertProvider} 和 {@literal @SelectProvider}
 *         定制 SQL 行为。</li>
 *     <li>通过泛型支持对不同实体类的扩展。</li>
 * </ul>
 *
 * <p>
 * 使用示例：
 * <pre>
 * public interface UserMapper extends BaseMapper&lt;UserEntity&gt; {
 * }
 * </pre>
 *
 * @param <S> 实体类类型，需继承自 {@link cn.yusiwen.commons.mapper.BaseEntity}
 * @author Siwen Yu (yusiwen@gmail.com)
 * @since 1.0
 */
public interface BaseMapper<S extends BaseEntity> {


    /**
     * 插入一个新的实体对象到数据库中。
     * <p>
     * 该方法会自动忽略实体类中标记为主键的字段，适用于使用数据库自增主键的表。
     * 插入成功后，数据库生成的主键值会自动回填到实体对象的id属性中。
     *
     * @param entity 要插入的实体对象，不能为null
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @InsertProvider(type = InsertWithoutPrimaryKeySqlProvider.class, method = "sql")
    void insert(S entity);

    /**
     * 根据主键ID查询单条记录。
     * <p>
     * 该方法通过指定的主键ID从数据库中查询对应的记录，并将结果映射为实体对象。
     * 如果未找到匹配的记录，则返回null。
     *
     * @param id 要查询的记录的主键ID
     * @return 匹配的实体对象，如果未找到则返回null
     */
    @SelectProvider(type = SelectOneSqlProvider.class, method = "sql")
    S queryById(Long id);

    /**
     * 插入provider
     */
    class InsertSqlProvider extends BaseSqlProviderSupport {

        /**
         * 构造一个新的插入SQL提供者实例。
         * <p>
         * 该构造函数创建一个用于生成INSERT SQL语句的提供者对象。
         * 它继承自BaseSqlProviderSupport，用于处理通用的SQL生成逻辑。
         */
        public InsertSqlProvider() {
            // this constructor is empty
        }

        /**
         * sql
         *
         * @param context context
         * @return sql
         */
        public String sql(ProviderContext context) {
            TableInfo table = tableInfo(context);

            return new SQL()
                    .INSERT_INTO(table.tableName)
                    .INTO_COLUMNS(table.columns)
                    .INTO_VALUES(Stream.of(table.fields).map(TableInfo::bindParameter).toArray(String[]::new))
                    .toString();

        }
    }

    /**
     * 不包含主键的插入SQL提供者类
     * <p>
     * 此类用于生成不包含主键字段的INSERT SQL语句。主要用于那些主键由数据库自动生成的表
     * （例如使用自增主键的表）。它会排除实体类中标记为主键的字段，只插入其他字段的值。
     */
    class InsertWithoutPrimaryKeySqlProvider extends BaseSqlProviderSupport {

        /**
         * 创建一个新的InsertWithoutPrimaryKeySqlProvider实例。
         * <p>
         * 此构造函数用于初始化一个不包含主键的SQL插入语句提供者。
         * 主要用于处理那些使用数据库自动生成主键的表的插入操作。
         */
        public InsertWithoutPrimaryKeySqlProvider() {
            // this constructor is empty
        }

        /**
         * 生成不包含主键的INSERT SQL语句
         *
         * @param context MyBatis提供的上下文对象，包含了Mapper接口的相关信息
         * @return 生成的INSERT SQL语句
         */
        public String sql(ProviderContext context) {
            TableInfo table = tableInfo(context);

            return new SQL()
                    .INSERT_INTO(table.tableName)
                    .INTO_COLUMNS(table.columnsWithoutPrimaryKey)
                    .INTO_VALUES(Stream.of(table.fieldsWithoutPrimaryKey)
                            .map(TableInfo::bindParameter).toArray(String[]::new))
                    .toString();

        }
    }

    /**
     * 单条数据查询
     */
    class SelectOneSqlProvider extends BaseSqlProviderSupport {

        /**
         * 创建一个新的SelectOneSqlProvider实例。
         * <p>
         * 此构造函数用于初始化单条数据查询的SQL提供者。
         * 主要用于生成查询单条记录的SQL语句。
         */
        public SelectOneSqlProvider() {
            // this constructor is empty
        }
        
        /**
         * sql
         *
         * @param context context
         * @return sql
         */
        public String sql(ProviderContext context) {
            TableInfo table = tableInfo(context);

            return new SQL()
                    .SELECT(table.selectColumns)
                    .FROM(table.tableName)
                    .WHERE(table.getPrimaryKeyWhere())
                    .toString();
        }
    }

    /**
     * 根据id列表查询
     */
    class SelectByPrimaryKeyInSqlProvider extends BaseSqlProviderSupport {

        /**
         * 创建一个新的SelectByPrimaryKeyInSqlProvider实例。
         * <p>
         * 此构造函数用于初始化批量主键查询的SQL提供者。
         * 主要用于生成根据ID列表进行批量查询的SQL语句。
         */
        public SelectByPrimaryKeyInSqlProvider() {
            // this constructor is empty
        }
        
        /**
         * 生成根据主键ID列表进行批量查询的SQL语句
         * <p>
         * 该方法用于构建一个SELECT语句，可以同时查询多个指定ID的记录。
         * 它将生成类似"SELECT ... FROM table WHERE id IN (1,2,3)"的SQL语句。
         *
         * @param params  包含查询参数的Map，其中"ids"键对应要查询的ID列表
         * @param context MyBatis提供的上下文对象，包含Mapper接口的相关信息
         * @return 生成的SELECT SQL语句
         */
        public String sql(Map<String, Object> params, ProviderContext context) {
            @SuppressWarnings("unchecked")
            List<Object> ids = (List<Object>) params.get("ids");
            TableInfo table = tableInfo(context);
            return new SQL()
                    .SELECT(table.selectColumns)
                    .FROM(table.tableName)
                    .WHERE(table.primaryKeyColumn
                            + " IN (" + String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new)) + ")")
                    .toString();
        }
    }

    /**
     * 基类
     */
    abstract class BaseSqlProviderSupport {

        /**
         * key -> mapper class   value -> tableInfo
         */
        private static final Map<Class<?>, TableInfo> TABLE_CACHE = new ConcurrentHashMap<>(8);

        /**
         * 构造一个新的BaseSqlProviderSupport实例。
         * <p>
         * 此构造函数用于初始化一个SQL提供者。
         * 主要用于处理通用的SQL生成逻辑。
         */
        protected BaseSqlProviderSupport() {
            // this constructor is empty
        }

        /**
         * 获取表信息结构
         *
         * @param context provider context
         * @return 表基本信息
         */
        protected TableInfo tableInfo(ProviderContext context) {
            // 如果不存在则创建
            return TABLE_CACHE.computeIfAbsent(context.getMapperType(), TableInfo::of);
        }
    }

    /**
     * table info
     */
    class TableInfo {

        /**
         * 主键名
         */
        private static final String DEFAULT_PRIMARY_KEY = "id";

        /**
         * 表名
         */
        private String tableName;

        /**
         * 主键列名
         */
        private String primaryKeyColumn;

        /**
         * 实体类型不含@NoColunm注解的field
         */
        private Field[] fields;

        /**
         * 不包含主键的field列表
         */
        private Field[] fieldsWithoutPrimaryKey;

        /**
         * 所有列名
         */
        private String[] columns;

        /**
         * 不包含主键字段的字段列表
         */
        private String[] columnsWithoutPrimaryKey;

        /**
         * 所有select sql的列名，有带下划线的将其转为aa_bb AS aaBb
         */
        private String[] selectColumns;

        private TableInfo() {
        }

        /**
         * 获取TableInfo的简单工厂
         *
         * @param mapperType mapper类型
         * @return {@link TableInfo}
         */
        public static TableInfo of(Class<?> mapperType) {
            Class<?> entityClass = entityType(mapperType);
            // 获取不含有@NotColumn注解的fields
            Field[] fields = excludeNotColumnField(entityClass);
            TableInfo tableInfo = new TableInfo();
            tableInfo.fields = fields;
            tableInfo.fieldsWithoutPrimaryKey = Arrays.stream(tableInfo.fields)
                    .filter(f -> !f.isAnnotationPresent(PrimaryKey.class))
                    .toArray(Field[]::new);
            tableInfo.tableName = tableName(entityClass);
            tableInfo.primaryKeyColumn = primaryKeyColumn(fields);
            tableInfo.columns = columns(fields);
            tableInfo.columnsWithoutPrimaryKey = Arrays.stream(tableInfo.columns)
                    .filter(s -> !s.equals(tableInfo.primaryKeyColumn))
                    .toArray(String[]::new);
            tableInfo.selectColumns = selectColumns(fields);
            return tableInfo;
        }

        /**
         * 获取BaseMapper接口中的泛型类型
         *
         * @param mapperType mapper类型
         * @return 实体类型
         */
        public static Class<?> entityType(Class<?> mapperType) {
            return Stream.of(mapperType.getGenericInterfaces())
                    .filter(ParameterizedType.class::isInstance)
                    .map(ParameterizedType.class::cast)
                    .filter(type -> BaseMapper.class.isAssignableFrom((Class<?>) type.getRawType()))
                    .findFirst()
                    .map(type -> type.getActualTypeArguments()[0])
                    .filter(Class.class::isInstance).map(Class.class::cast)
                    .orElseThrow(() -> new IllegalStateException("未找到BaseMapper的泛型类 " + mapperType.getName() + "."));
        }

        /**
         * 获取表名
         *
         * @param entityType 实体类型
         * @return 表名
         */
        public static String tableName(Class<?> entityType) {
            return Optional.ofNullable(entityType.getAnnotation(Table.class)).orElseThrow(IllegalStateException::new).value();
        }

        /**
         * 过滤含有@NotColumn注解或者是静态的field
         *
         * @param entityClass 实体类型
         * @return 不包含@NotColumn注解的fields
         */
        public static Field[] excludeNotColumnField(Class<?> entityClass) {
            Field[] allFields = ReflectUtil.getFields(entityClass);
            List<String> excludeColumns = getClassExcludeColumns(entityClass);
            return Stream.of(allFields)
                    //过滤掉类上指定的@NotCloumn注解的字段和字段上@NotColumn注解或者是静态的field
                    .filter(field -> !CollectionUtil.contains(excludeColumns, field.getName())
                            && !field.isAnnotationPresent(NotColumn.class) && !Modifier.isStatic(field.getModifiers()))
                    .toArray(Field[]::new);
        }

        /**
         * 获取实体类上标注的不需要映射的字段名
         *
         * @param entityClass 实体类
         * @return 不需要映射的字段名
         */
        public static List<String> getClassExcludeColumns(Class<?> entityClass) {
            List<String> excludeColumns = null;
            NotColumn classNoColumns = entityClass.getAnnotation(NotColumn.class);
            if (classNoColumns != null) {
                excludeColumns = Arrays.asList(classNoColumns.fields());
            }
            return excludeColumns;
        }

        /**
         * 获取查询对应的字段 (不包含pojo中含有@NoColumn主键的属性)
         *
         * @param fields p
         * @return 所有需要查询的查询字段
         */
        public static String[] selectColumns(Field[] fields) {
            return Stream.of(fields).map(TableInfo::selectColumnName).toArray(String[]::new);
        }

        /**
         * 获取所有pojo所有属性对应的数据库字段 (不包含pojo中含有@NoColumn主键的属性)
         *
         * @param fields entityClass所有fields
         * @return 所有的column名称
         */
        public static String[] columns(Field[] fields) {
            return Stream.of(fields).map(TableInfo::columnName).toArray(String[]::new);
        }

        /**
         * 如果fields中含有@Primary的字段，则返回该字段名为主键，否则默认'id'为主键名
         *
         * @param fields entityClass所有fields
         * @return 主键column(驼峰转为下划线)
         */
        public static String primaryKeyColumn(Field[] fields) {
            return Stream.of(fields).filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                    .findFirst()    //返回第一个primaryKey的field
                    .map(TableInfo::columnName)
                    .orElse(DEFAULT_PRIMARY_KEY);
        }

        /**
         * 获取单个属性对应的数据库字段（带有下划线字段将其转换为"字段 AS pojo属性名"形式）
         *
         * @param field 字段
         * @return 带有下划线字段将其转换为"字段 AS pojo属性名"形式
         */
        public static String selectColumnName(Field field) {
            String camel = columnName(field);
            return camel.contains("_") ? camel + " AS " + field.getName() : camel;
        }

        /**
         * 获取单个属性对应的数据库字段
         *
         * @param field entityClass中的field
         * @return 字段对应的column
         */
        public static String columnName(Field field) {
            return StrUtil.camel2Underscore(field.getName());
        }

        /**
         * 绑定参数
         *
         * @param field 字段
         * @return 参数格式
         */
        public static String bindParameter(Field field) {
            String value = "#{" + field.getName() + "}";
            return field.isAnnotationPresent(JSONColumn.class) ? value + "::JSONB" : value;
        }

        /**
         * 获取该字段的参数赋值语句，如 user_name = #{userName}
         *
         * @param field 字段
         * @return 参数赋值语句
         */
        public static String assignParameter(Field field) {
            return columnName(field) + " = " + bindParameter(field);
        }

        /**
         * 获取主键的where条件，如 id = #{id}
         *
         * @return 主键where条件
         */
        public String getPrimaryKeyWhere() {
            String pk = this.primaryKeyColumn;
            return pk + " = #{" + pk + "}";
        }
    }
}
