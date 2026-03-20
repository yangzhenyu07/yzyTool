package org.example.global;


import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.DemoDbTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Collections;


/**
 * @description: 代码生成
 * @author yangzhenyu
 * @date 2023/4/24 14:21
 * @version 1.0
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = DemoDbTool.class)
//@Slf4j
public class MybatisPlusGeneratorTest {
    @Autowired
    private DynamicRoutingDataSource dataSource;

    //代码生成
    //@Test
    public void test(){
        //数据源选择
        final String dbFlag = "master";
//        //实体包路径
//        String entity = StringUtils.join("entity",".",dbFlag);
//        //mapper 路径
//        String mapper = StringUtils.join("mapper",".",dbFlag);

        //实体包路径
        String entity = "entity";
        //mapper 路径
        String mapper = "mapper";
        String username = ((DruidDataSource)((ItemDataSource) dataSource.getDataSource(dbFlag)).getRealDataSource()).getUsername();
        String password = ((DruidDataSource)((ItemDataSource) dataSource.getDataSource(dbFlag)).getRealDataSource()).getPassword();
        String url = ((DruidDataSource)((ItemDataSource) dataSource.getDataSource(dbFlag)).getRealDataSource()).getUrl();

        //需要生成的表
        //String [] tables = new String[]{"t_param_config","t_scheduler_lock"};
        String [] tables = new String[]{"t_param_config"};
        //配置数据源
        FastAutoGenerator.create(url, username, password)
                //2、全局配置
                .globalConfig(builder -> {
                    builder.author("yangzhenyu") // 设置作者名
                            //设置输出路径：项目的 java 目录下
                            .outputDir(System.getProperty("user.dir") + "/src/main/java")
                            //注释日期
                            .commentDate("yyyy-MM-dd hh:mm:ss")
                            //定义生成的实体类中日期的类型 TIME_PACK=LocalDateTime;ONLY_DATE=Date;
                            .dateType(DateType.ONLY_DATE)
                            //覆盖之前的文件
                            .fileOverride()
                            //开启 swagger 模式
                            .enableSwagger()
                            //禁止打开输出目录，默认打开
                            .disableOpenDir();
                })
                //3、包配置
                .packageConfig(builder -> {
                    builder.parent("org") // 设置父包名
                            //设置模块包名
                            .moduleName("example")
                            //pojo 实体类包名
                            .entity(entity)
                            //Service 包名
                            .service("service")
                            // ***ServiceImpl 包名
                            .serviceImpl("service.impl")
                            //Mapper 包名
                            .mapper(mapper)
                            //Mapper XML 包名
                            .xml(mapper)
                            //Controller 包名
                            .controller("controller")
                            //自定义文件包名
                            .other("utils")
                            //配置 mapper.xml 路径信息：项目的 resources 目录下
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, StringUtils.join(System.getProperty("user.dir"),"/src/main/resources/mapper/"))) ;
                })
                //4、策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(tables) // 设置需要生成的数据表名
                            //.addTablePrefix("t_", "c_") // 设置过滤表前缀
                            //=============================
                            //Mapper策略配置
                            //=============================
                            .mapperBuilder()
                            //设置父类
                            .superClass(BaseMapper.class)
                            //格式化 mapper 文件名称
                            .formatMapperFileName("%sMapper")
                            //开启 @Mapper 注解
                            .enableMapperAnnotation()
                            //格式化 Xml 文件名称
                            .formatXmlFileName("%sXml")
                            //=============================
                            //servic类策略配置
                            //=============================
                            .serviceBuilder()
                            //格式化 service 接口文件名称，%s进行匹配表名，如 UserService
                            .formatServiceFileName("%sService")
                            //格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl
                            .formatServiceImplFileName("%sServiceImpl")
                            //=============================
                            //实体类策略配置
                            //=============================
                            .entityBuilder()
                            .enableLombok() //开启 Lombok
                            //不实现 Serializable 接口，不生产 SerialVersionUID
                            //.disableSerialVersionUID()
                            //逻辑删除字段名
                            .logicDeleteColumnName("deleted")
                            //数据库表映射到实体的命名策略：下划线转驼峰命
                            .naming(NamingStrategy.underline_to_camel)
                            //数据库表字段映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)
                            //添加表字段填充，"create_time"字段自动填充为插入时间，"modify_time"字段自动填充为插入修改时间
                            .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),
                                    new Column("modify_time", FieldFill.INSERT_UPDATE
                                    )
                            )
                            // 开启生成实体时生成字段注解
                            .enableTableFieldAnnotation()
                            //=============================
                            //Controller策略配置
                            //=============================
                            .controllerBuilder()
                            //格式化 Controller 类文件名称，%s进行匹配表名，如 UserController
                            .formatFileName("%sController")
                            //开启生成 @RestController 控制器
                            .enableRestStyle();

                })
                //模板
                .templateEngine(new VelocityTemplateEngine())
                //执行
                .execute();
    }


}