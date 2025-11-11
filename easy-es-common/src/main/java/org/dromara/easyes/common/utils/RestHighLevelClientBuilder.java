package org.dromara.easyes.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.easysearch.client.RestClientBuilder;
import org.easysearch.client.RestHighLevelClient;

import static org.dromara.easyes.common.constants.BaseEsConstants.UNKNOWN;

/**
 * Esasysearch 构造器
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestHighLevelClientBuilder {
    /**
     * 支持的版本 目前支持版本为 1.0.1
     */
    private final static String SUPPORTED_JAR_VERSION = "1.0.1";
    private final static String SUPPORTED_CLIENT_VERSION = "1";

    /**
     * 构建RestHighLevelClient
     *
     * @param builder 构建连接对象
     * @return es高级客户端
     */
    public static RestHighLevelClient build(RestClientBuilder builder) {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);
        // 检验es版本是否对应
        verify(restHighLevelClient);
        return restHighLevelClient;
    }

    /**
     * 校验es client版本及jar包版本
     *
     * @param restHighLevelClient es高级客户端
     */
    private static void verify(RestHighLevelClient restHighLevelClient) {
        // 校验jar包版本是否为推荐使用版本
        String jarVersion = EEVersionUtils.getJarVersion(restHighLevelClient.getClass());
        LogUtils.formatInfo("Easysearch jar version:%s", jarVersion);
        if (!jarVersion.equals(SUPPORTED_JAR_VERSION) && !UNKNOWN.equals(jarVersion)) {
            LogUtils.formatError("Easy-Es supported easysearch-client jar version is:%s ,Please resolve the dependency conflict!", SUPPORTED_JAR_VERSION);
        }
        String clientVersion = EEVersionUtils.getClientVersion(restHighLevelClient);
        LogUtils.formatInfo("Easysearch client version:%s", clientVersion);
        if (!clientVersion.startsWith(SUPPORTED_CLIENT_VERSION)) {
            LogUtils.formatWarn("Easy-Es supported easysearch client version is:%s.xx", SUPPORTED_CLIENT_VERSION);
        }
        if (!jarVersion.equals(clientVersion)) {
            LogUtils.formatWarn("Easysearch clientVersion:%s not equals jarVersion:%s, It does not affect your use, but we still recommend keeping it consistent!", clientVersion, jarVersion);
        }
    }
}
