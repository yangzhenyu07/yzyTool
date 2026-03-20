package org.example.config;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.extension.incrementer.OracleKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiegaobing
 * @description:
 * @date 2023/4/4 6:41 下午
 */
@Configuration
public class MybatisPlusConfig {

    //主键生成策略
    @Bean
    public IKeyGenerator keyGenerator() {
        return new OracleKeyGenerator();
        //return new OracleKeyGenerator();
    }

    /**
     *   mybatis-plus分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        /**
         * PaginationInnerInterceptor 是 MyBatis-Plus 提供的一个内置分页拦截器，
         * 用于自动处理分页逻辑。它会在 SQL 执行前对 SQL 进行拦截，
         * 并自动修改查询 SQL 以适应分页查询。
         */
        // 使用分页拦截器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }


}
