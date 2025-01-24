package org.dromara.easyes.solon;

import org.dromara.easyes.common.constants.BaseEsConstants;
import org.dromara.easyes.common.utils.LogUtils;
import org.dromara.easyes.solon.config.DynamicEsProperties;
import org.dromara.easyes.solon.config.EasyEsConfigProperties;
import org.dromara.easyes.solon.config.EsAutoConfiguration;
import org.dromara.easyes.solon.config.GeneratorConfiguration;
import org.dromara.easyes.solon.factory.IndexStrategyFactory;
import org.dromara.easyes.solon.register.EsMapperScan;
import org.dromara.easyes.solon.register.MapperScannerRegister;
import org.dromara.easyes.solon.service.impl.AutoProcessIndexNotSmoothlyServiceImpl;
import org.dromara.easyes.solon.service.impl.AutoProcessIndexSmoothlyServiceImpl;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * solon插件实现
 * @author MoJie
 * @since 2.0
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) throws Throwable {
        boolean enable = context.cfg().getBool(BaseEsConstants.ENABLE_PREFIX, true);
        if (!enable) {
            LogUtils.info("===> Easy-Es is not enabled");
            return;
        }
        Object dynamicEsProperties = context.cfg().getOrDefault("easy-es.dynamic", new DynamicEsProperties());
        context.wrapAndPut(DynamicEsProperties.class, dynamicEsProperties);
        context.beanMake(EasyEsConfigProperties.class);
        context.beanMake(EsAutoConfiguration.class);
        context.beanMake(GeneratorConfiguration.class);
        context.beanMake(IndexStrategyFactory.class);
        context.beanMake(AutoProcessIndexSmoothlyServiceImpl.class);
        context.beanMake(AutoProcessIndexNotSmoothlyServiceImpl.class);
        // 扫描EsMapperScan的包并注册到容器
        context.beanBuilderAdd(EsMapperScan.class, new MapperScannerRegister(context));
    }
}
