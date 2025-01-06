package org.dromara.easyes.spring.property;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.dromara.easyes.annotation.rely.FieldStrategy;
import org.dromara.easyes.annotation.rely.IdType;
import org.dromara.easyes.annotation.rely.RefreshPolicy;
import org.dromara.easyes.common.constants.BaseEsConstants;
import org.dromara.easyes.common.enums.ProcessIndexStrategyEnum;
import org.dromara.easyes.common.enums.SchemaEnum;
import org.dromara.easyes.common.property.GlobalConfig;
import org.dromara.easyes.common.utils.StringUtils;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import static org.dromara.easyes.common.constants.BaseEsConstants.EMPTY_STR;

/**
 * easy-es基础配置项 考虑到spring的场景，有些参数不是必须配置
 * 基本类型就会出现默认值的情况 所以为了要有null值出现，这里采用包装类型
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@Data
public class EasyEsProperty {

    /**
     * 是否开启easy-es 默认开启
     */
    private Boolean enable;
    /**
     * 是否开启easy-es LOGO BANNER的打印
     */
    private Boolean banner;
    /**
     * es client address es客户端地址
     */
    private String address;
    /**
     * schema 模式
     */
    private String schema;
    /**
     * username of es 用户名,可缺省
     */
    private String username;
    /**
     * password of es 密码,可缺省
     */
    private String password;
    /**
     * maxConnectTotal 最大连接数
     */
    private Integer maxConnTotal;
    /**
     * maxConnectPerRoute 最大连接路由数
     */
    private Integer maxConnPerRoute;
    /**
     * connectTimeout timeUnit:millis 连接超时时间 单位毫秒
     */
    private Integer connectTimeout;
    /**
     * socketTimeout timeUnit:millis  通讯超时时间 单位毫秒 默认10分钟 以免小白踩坑平滑模式下数据量过大迁移超时失败
     */
    private Integer socketTimeout;
    /***
     * 保持心跳时间 timeUnit:millis  单位毫秒 默认30秒 防止小白白出现服务首次调用不成时不知所措
     */
    private Integer keepAliveMillis;
    /**
     * connectionRequestTimeout timeUnit:millis 连接请求超时时间 单位毫秒
     */
    private Integer connectionRequestTimeout;
    /**
     * global config 全局配置
     */
    private GlobalConfig globalConfig;
    /**
     * 配置多动态数据源key datasource id
     */
    private Map<String, EasyEsProperty> datasource = new HashMap<>();

    public EasyEsProperty(Environment environment) {
        this.setEnable(environment.getProperty(BaseEsConstants.ENABLE_PREFIX, Boolean.class, true));
        this.setBanner(environment.getProperty(BaseEsConstants.ENABLE_BANNER, Boolean.class, true));
        this.setAddress(environment.getProperty("easy-es.address", "127.0.0.1:9200"));
        this.setSchema(environment.getProperty("easy-es.schema", SchemaEnum.http.name()));
        this.setUsername(environment.getProperty("easy-es.username"));
        this.setPassword(environment.getProperty("easy-es.password"));
        this.setMaxConnTotal(environment.getProperty("easy-es.max-conn-total", Integer.class));
        this.setMaxConnPerRoute(environment.getProperty("easy-es.max-conn-per-route", Integer.class));
        this.setConnectTimeout(environment.getProperty("easy-es.connect-timeout", Integer.class));
        this.setSocketTimeout(environment.getProperty("easy-es.socket-timeout", Integer.class, 600000));
        this.setKeepAliveMillis(environment.getProperty("easy-es.keep-alive-millis", Integer.class, 30000));
        this.setConnectionRequestTimeout(environment.getProperty("easy-es.connection-request-timeout", Integer.class));
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setPrintDsl(environment.getProperty("easy-es.global-config.print-dsl", Boolean.class, true));
        globalConfig.setIKunMode(environment.getProperty("easy-es.global-config.i-kun-mode", Boolean.class, false));
        globalConfig.setProcessIndexMode(environment.getProperty("easy-es.global-config.process-index-mode",
                ProcessIndexStrategyEnum.class, ProcessIndexStrategyEnum.MANUAL));
        globalConfig.setDistributed(environment.getProperty("easy-es.global-config.distributed", Boolean.class, true));
        globalConfig.setReindexTimeOutHours(environment.getProperty("easy-es.global-config.reindex-time-out-hours",
                Integer.class, 72));
        globalConfig.setAsyncProcessIndexBlocking(environment.getProperty("easy-es.global-config.async-process-index-blocking",
                Boolean.class, true));
        globalConfig.setActiveReleaseIndexMaxRetry(environment.getProperty("easy-es.global-config.active-release-index-max-retry",
                Integer.class, 4320));
        globalConfig.setActiveReleaseIndexFixedDelay(environment.getProperty("easy-es.global-config.active-release-index-fixed-delay",
                Integer.class, 60));
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIndexPrefix(environment.getProperty("easy-es.global-config.db-config.index-prefix", EMPTY_STR));
        dbConfig.setMapUnderscoreToCamelCase(environment.getProperty("easy-es.global-config.db-config.map-underscore-to-camel-case",
                Boolean.class, false));
        dbConfig.setIdType(environment.getProperty("easy-es.global-config.db-config.id-type", IdType.class, IdType.NONE));
        dbConfig.setFieldStrategy(environment.getProperty("easy-es.global-config.db-config.field-strategy",
                FieldStrategy.class, FieldStrategy.NOT_NULL));
        dbConfig.setEnableTrackTotalHits(environment.getProperty("easy-es.global-config.db-config.enable-track-total-hits",
                Boolean.class, true));
        dbConfig.setRefreshPolicy(environment.getProperty("easy-es.global-config.db-config.refresh-policy",
                RefreshPolicy.class, RefreshPolicy.NONE));
        dbConfig.setBatchUpdateThreshold(environment.getProperty("easy-es.global-config.db-config.batch-update-threshold",
                Integer.class, 10000));
        dbConfig.setSmartAddKeywordSuffix(environment.getProperty("easy-es.global-config.db-config.smart-add-keyword-suffix",
                Boolean.class, true));
        globalConfig.setDbConfig(dbConfig);
        this.setGlobalConfig(globalConfig);

        // 设置多数据源
    }

    public EasyEsProperty() {
    }

}
