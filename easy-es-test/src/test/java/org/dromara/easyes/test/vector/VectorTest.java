package org.dromara.easyes.test.vector;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.test.TestEasyEsApplication;
import org.dromara.easyes.test.entity.Document;
import org.dromara.easyes.test.mapper.DocumentMapper;
import org.easysearch.index.query.QueryBuilder;
import org.easysearch.index.query.QueryBuilders;
import org.easysearch.script.Script;
import org.easysearch.script.ScriptType;
import org.easysearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向量测试
 **/
@Disabled
@SpringBootTest(classes = TestEasyEsApplication.class)
public class VectorTest {
    @Resource
    private DocumentMapper documentMapper;


    @Test
    public void testVectorInsert() {
        // 测试插入数据
        Document document = new Document();
        document.setEsId("35f6fff8-1d3b-48b6-a765-028ec81b1437");
        document.setContent("测试插入数据");
        document.setVector(new double[]{0.39684247970581055, 0.7687071561813354, 0.5145490765571594});
        int successCount = documentMapper.insert(document);
        Assertions.assertEquals(1, successCount);
    }

    @Test
    public void testVectorUpdate() {
        // 测试插入数据
        Document document = new Document();
        document.setEsId("35f6fff8-1d3b-48b6-a765-028ec81b1437");
        document.setContent("测试更新向量数据");
        document.setVector(new double[]{0.39684247970581666, 0.768707156181666, 0.5145490765571666});
        int successCount = documentMapper.updateById(document);
        Assertions.assertEquals(1, successCount);
    }

    @Test
    public void testVectorSearch() {
        String dsl = "{\n" +
                "  \"query\": {\n" +
                "    \"knn_nearest_neighbors\": {\n" +
                "      \"field\": \"vector\",\n" +
                "      \"vec\": {\n" +
                "        \"values\": [\n" +
                "          0.39684247970581055,\n" +
                "          0.7687071561813354,\n" +
                "          0.5145490765571594\n" +
                "        ]\n" +
                "      },\n" +
                "      \"model\": \"exact\",\n" +
                "      \"similarity\": \"cosine\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String jsonResult = documentMapper.executeDSL(dsl);

        JSONObject result = JSON.parseObject(jsonResult);
        JSONArray hits = result.getJSONObject("hits").getJSONArray("hits");

        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < hits.size(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            String source = hit.getString("_source");
            Document doc = JSON.parseObject(source, Document.class);
            doc.setScore(hit.getFloat("_score"));
            documents.add(doc);
        }
        Assertions.assertFalse(documents.isEmpty());
    }

}