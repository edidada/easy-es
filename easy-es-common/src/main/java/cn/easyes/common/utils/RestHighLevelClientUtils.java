package cn.easyes.common.utils;

import org.elasticsearch.client.RestHighLevelClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lyy
 * @date 2023年03月14日 16:05
 */
public class RestHighLevelClientUtils {

    public static final String DEFAULT_DS = "DEFAULT_DS";

    private static Map<String, RestHighLevelClient> restHighLevelClientMap = new ConcurrentHashMap<>();

    public RestHighLevelClientUtils() {
    }

    public static RestHighLevelClient getRestHighLevelClient(String restHighLevelClientId) {
        if(DEFAULT_DS.equals(restHighLevelClientId)){
            return restHighLevelClientMap.values().stream().findFirst().get();
        }
        RestHighLevelClient restHighLevelClient = restHighLevelClientMap.get(restHighLevelClientId);
        if(restHighLevelClient == null){
           ExceptionUtils.eee("找不到RestHighLevelClient,restHighLevelClientId:",restHighLevelClientId);

        }
        return restHighLevelClient;
    }

    public RestHighLevelClient getClient(String restHighLevelClientId) {
        return RestHighLevelClientUtils.getRestHighLevelClient(restHighLevelClientId);
    }

    public static void registerRestHighLevelClient(String restHighLevelClientId, RestHighLevelClient restHighLevelClient) {
        RestHighLevelClientUtils.restHighLevelClientMap.put(restHighLevelClientId, restHighLevelClient);
    }

}
