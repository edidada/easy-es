package cn.easyes.test.interceptor;


import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.springframework.stereotype.Component;

import cn.easyes.annotation.Intercepts;
import cn.easyes.annotation.Signature;
import cn.easyes.core.conditions.LambdaEsQueryWrapper;
import cn.easyes.core.conditions.interfaces.BaseEsMapper;
import cn.easyes.extension.context.Interceptor;
import cn.easyes.extension.context.Invocation;

/**
 * 测试方法使用正则表达式
 * @author huangjy
 */
@Intercepts(
    {
        @Signature(type = BaseEsMapper.class, method = "select.*", args = {LambdaEsQueryWrapper.class}, useRegexp = true),
        @Signature(type = BaseEsMapper.class, method = "search", args = {SearchRequest.class, RequestOptions.class}),
        @Signature(type = BaseEsMapper.class, method = "insert|update", args = {Object.class}, useRegexp = true),
        @Signature(type = BaseEsMapper.class, method = ".*ById", args = {Object.class}, useRegexp = true),
    }
)
// @Component
public class TenantLineInnerInterceptor implements Interceptor {


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // Object[] args = invocation.getArgs();
        // Object arg = args[0];
        // if (arg instanceof LambdaEsQueryWrapper) {
        //     LambdaEsQueryWrapper wrapper = ((LambdaEsQueryWrapper) args[0]);
        //     wrapper.eq("tenantId", "1");
        //     return invocation.proceed();
        // }
        System.out.println("增则拦截方法");
        return invocation.proceed();
    }
}
