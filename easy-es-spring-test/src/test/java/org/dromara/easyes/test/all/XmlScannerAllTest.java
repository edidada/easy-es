package org.dromara.easyes.test.all;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dromara.easyes.common.constants.BaseEsConstants;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.biz.OrderByParam;
import org.dromara.easyes.core.biz.SAPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.dromara.easyes.core.toolkit.EntityInfoHelper;
import org.dromara.easyes.core.toolkit.FieldUtils;
import org.dromara.easyes.test.TestEasyEsApplication;
import org.dromara.easyes.test.entity.Document;
import org.dromara.easyes.test.mapper.DocumentMapper;
import org.easysearch.action.search.SearchResponse;
import org.easysearch.client.HttpAsyncResponseConsumerFactory;
import org.easysearch.client.RequestOptions;
import org.easysearch.common.geo.GeoDistance;
import org.easysearch.common.geo.GeoPoint;
import org.easysearch.common.geo.ShapeRelation;
import org.easysearch.common.unit.DistanceUnit;
import org.easysearch.geometry.Circle;
import org.easysearch.geometry.Point;
import org.easysearch.geometry.Rectangle;
import org.easysearch.index.query.BoolQueryBuilder;
import org.easysearch.index.query.QueryBuilder;
import org.easysearch.index.query.QueryBuilders;
import org.easysearch.script.Script;
import org.easysearch.script.ScriptType;
import org.easysearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.easysearch.search.aggregations.bucket.terms.Terms;
import org.easysearch.search.aggregations.metrics.ParsedAvg;
import org.easysearch.search.aggregations.metrics.ParsedMax;
import org.easysearch.search.aggregations.metrics.ParsedMin;
import org.easysearch.search.aggregations.metrics.ParsedSum;
import org.easysearch.search.builder.SearchSourceBuilder;
import org.easysearch.search.sort.FieldSortBuilder;
import org.easysearch.search.sort.GeoDistanceSortBuilder;
import org.easysearch.search.sort.SortBuilders;
import org.easysearch.search.sort.SortOrder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.dromara.easyes.common.constants.BaseEsConstants.KEYWORD_SUFFIX;

/**
 * @author MoJie
 * @since 2.0
 */
@DisplayName("easy-es核心功能单元测试(xml方式)")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestEasyEsApplication.class)
public class XmlScannerAllTest {

    @Resource
    private DocumentMapper documentMapper;

    @BeforeAll
    static void setupClass(@Autowired DocumentMapper mapper) {
        // 创建索引
        String indexName = EntityInfoHelper.getEntityInfo(Document.class).getIndexName();
        Boolean exists = mapper.existsIndex(indexName);
        if (Boolean.TRUE.equals(exists)) {
            mapper.deleteIndex(indexName);
        }
        mapper.createIndex();
    }

    @BeforeEach
    void setupEach() {
        // 每个测试前准备22条标准数据
        prepareTestData();
    }

    @AfterEach
    void cleanupEach() {
        // 每个测试后清理数据（不删索引）
        cleanupTestData();
    }

    @AfterAll
    static void cleanupClass(@Autowired DocumentMapper mapper) {
        // 清理索引
        String indexName = EntityInfoHelper.getEntityInfo(Document.class).getIndexName();
        mapper.deleteIndex(indexName);
        mapper.deleteIndex(BaseEsConstants.LOCK_INDEX);
    }

    /**
     * 准备测试数据 - 创建22条测试文档
     */
    private void prepareTestData() {
        List<Document> documentList = new ArrayList<>();
        
        // 第一条数据（特殊数据）
        Document document1 = new Document();
        document1.setEsId("1");
        document1.setCaseTest("Test");
        document1.setTitle("测试文档1");
        document1.setContent("测试内容1我是大家发达酸辣粉家里都是测试阿拉拉肥发的是就测试时风刀霜剑阿凯刘非打死了交付率及时点法律就反倒是测测试初拉力肌肥大来极氪吉利发送代理费逻辑逻辑发骚鸡,测试发力了哦哦我");
        document1.setCreator("老汉1");
        document1.setIpAddress("192.168.1.1");
        document1.setLocation("40.171975,116.587105");
        document1.setGmtCreate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        document1.setCustomField("自定义字段1");
        document1.setNullField("id为1的数据不是null,除此之外其它都是");
        Rectangle rectangle = new Rectangle(39.084509D, 41.187328D, 70.610461D, 20.498353D);
        document1.setGeoLocation(rectangle.toString());
        document1.setStarNum(1);
        document1.setMultiField("葡萄糖酸钙口服溶液");
        document1.setEnglish("Calcium Gluconate");
        document1.setBigNum(new BigDecimal("66.66"));
        document1.setVector(new double[]{0.39684247970581666, 0.768707156181666, 0.5145490765571666});
        documentList.add(document1);
        
        // 2-22条数据
        for (int i = 2; i <= 22; i++) {
            Document document = new Document();
            document.setEsId(Integer.toString(i));
            document.setTitle("测试文档" + i);
            document.setContent("测试内容" + i);
            document.setCreator("老汉" + i);
            document.setGmtCreate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            document.setCustomField("自定义字段" + i);
            Point point = new Point(13.400544 + i, 52.530286 + i);
            document.setGeoLocation(point.toString());
            document.setStarNum(i);
            document.setVector(new double[]{35.89684247970581666, 86.268707156181666, 133.1145490765571666});

            // 针对个别数据 造一些差异项 方便测试不同场景
            if (i == 2) {
                document.setLocation("40.17836693398477,116.64002551005981");
                document.setStarNum(1);
            } else if (i == 3) {
                document.setLocation("40.19103839805197,116.5624013764374");
            } else if (i == 4) {
                document.setLocation("40.13933715136454,116.63441990026217");
            }
            documentList.add(document);
        }
        
        documentMapper.insertBatch(documentList);
        // 刷新索引确保数据立即可查
        documentMapper.refresh();
    }

    /**
     * 清理测试数据
     */
    private void cleanupTestData() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.matchAllQuery();
        documentMapper.delete(wrapper);
        documentMapper.refresh();
    }

    @Test
    public void testCreateIndex() {
        // 测试索引是否已创建（由@BeforeAll创建）
        String indexName = EntityInfoHelper.getEntityInfo(Document.class).getIndexName();
        Boolean exists = documentMapper.existsIndex(indexName);
        Assertions.assertTrue(exists, "索引应该存在");
    }

    @Test
    public void testInsert() {
        // 测试插入新数据（ID=100避免与准备数据冲突）
        Document document = new Document();
        document.setEsId("100");
        document.setCaseTest("Test");
        document.setTitle("测试文档100");
        document.setContent("测试内容100我是大家发达酸辣粉家里都是测试阿拉拉肥发的是就测试时风刀霜剑阿凯刘非打死了交付率及时点法律就反倒是测测试初拉力肌肥大来极氪吉利发送代理费逻辑逻辑发骚鸡,测试发力了哦哦我");
        document.setCreator("老汉100");
        document.setIpAddress("192.168.1.100");
        document.setLocation("40.171975,116.587105");
        document.setGmtCreate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        document.setCustomField("自定义字段100");
        document.setNullField("id为100的数据不是null");
        Rectangle rectangle = new Rectangle(39.084509D, 41.187328D, 70.610461D, 20.498353D);
        document.setGeoLocation(rectangle.toString());
        document.setStarNum(100);
        document.setMultiField("葡萄糖酸钙口服溶液");
        document.setEnglish("Calcium Gluconate");
        document.setBigNum(new BigDecimal("66.66"));
        document.setVector(new double[]{0.39684247970581666, 0.768707156181666, 0.5145490765571666});
        int successCount = documentMapper.insert(document);
        Assertions.assertEquals(successCount, 1);
    }

    @Test
    public void testBatchInsert() {
        // 测试批量插入新数据（ID=101-110避免与准备数据冲突）
        List<Document> documentList = new ArrayList<>();
        for (int i = 101; i < 111; i++) {
            Document document = new Document();
            document.setEsId(Integer.toString(i));
            document.setTitle("测试文档" + i);
            document.setContent("测试内容" + i);
            document.setCreator("老汉" + i);
            document.setGmtCreate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            document.setCustomField("自定义字段" + i);
            Point point = new Point(13.400544 + i, 52.530286 + i);
            document.setGeoLocation(point.toString());
            document.setStarNum(i);
            document.setVector(new double[]{35.89684247970581666, 86.268707156181666, 133.1145490765571666});
            documentList.add(document);
        }
        int count = documentMapper.insertBatch(documentList);
        Assertions.assertEquals(documentList.size(), count);
        
        // 手动刷新索引，确保数据立即可查询
        documentMapper.refresh();
    }

    // 2.修改
    @Test
    public void testUpdateById() {
        Document document = new Document();
        document.setEsId("1");
        document.setTitle("测试文档1标题被更新了");
        int count = documentMapper.updateById(document);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void testUpdateByWrapper() {
        LambdaEsUpdateWrapper<Document> wrapper = new LambdaEsUpdateWrapper<>();
        wrapper.eq(Document::getTitle, "测试文档2");
        wrapper.set(Document::getContent, "测试文档内容2的内容被更新了");

        int count = documentMapper.update(null, wrapper);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void testUpdateByChainWrapper() {
        int count = EsWrappers.lambdaChainUpdate(documentMapper)
                .eq(Document::getTitle, "测试文档3")
                .set(Document::getContent, "测试文档内容3的内容被修改了")
                .update();
        Assertions.assertEquals(1, count);
    }

    @Test
    public void testUpdateBySetSearchSourceBuilder() {
        LambdaEsUpdateWrapper<Document> wrapper = new LambdaEsUpdateWrapper<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery(FieldUtils.val(Document::getTitle) + KEYWORD_SUFFIX, "测试文档2"));
        wrapper.setSearchSourceBuilder(searchSourceBuilder);

        Document document = new Document();
        document.setContent("测试文档内容2的内容再次被更新了");
        int count = documentMapper.update(document, wrapper);
        Assertions.assertEquals(1, count);
    }

    @Test
    public void testUpdateByWrapperAndEntity() {
        LambdaEsUpdateWrapper<Document> wrapper = new LambdaEsUpdateWrapper<>();
        wrapper.match(Document::getCreator, "老汉");
        Document document = new Document();
        document.setCustomField("被更新的自定义字段");
        int count = documentMapper.update(document, wrapper);
        Assertions.assertEquals(22, count);
    }

    // 3.查询

    @Test
    public void testSQL() {
        // 注意 sql中的from后面跟的是要被查询的索引名,也可以是索引别名(效果一样) 由于索引名可能会变,所以此处我采用别名ee_default_alias进行查询
        String sql = "select count(*) from ee_default_alias where star_num > 0";
        String jsonResult = documentMapper.executeSQL(sql);
        System.out.println(jsonResult);
        Assertions.assertNotNull(jsonResult);
    }

    @Test
    public void testDSL() {
        String dsl = "{\"query\":{\"bool\":{\"must\":[{\"term\":{\"title.keyword\":{\"value\":\"测试文档3\",\"boost\":1.0}}}],\"adjust_pure_negative\":true,\"boost\":1.0}},\"track_total_hits\":2147483647,\"highlight\":{\"pre_tags\":[\"<em>\"],\"post_tags\":[\"</em>\"],\"fragment_size\":2,\"fields\":{\"content\":{\"type\":\"unified\"}}}}";
        String jsonResult = documentMapper.executeDSL(dsl);
        System.out.println(jsonResult);
        Assertions.assertNotNull(jsonResult);
    }

    @Test
    public void testUpdateAndSelectOne() {
        // 先更新
        Document document = new Document();
        document.setEsId("1");
        document.setTitle("测试文档1标题被更新了");
        int count = documentMapper.updateById(document);
        Assertions.assertEquals(1, count);
        
        // 再查询验证
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "内容")
                .orderByAsc(Document::getStarNum, Document::getEsId)
                .limit(1);
        Document result = documentMapper.selectOne(wrapper);
        Assertions.assertEquals("测试文档1标题被更新了", result.getTitle());
    }

    @Test
    public void testUpdateAndOne() {
        // 先更新
        int count = EsWrappers.lambdaChainUpdate(documentMapper)
                .eq(Document::getTitle, "测试文档3")
                .set(Document::getContent, "测试文档内容3的内容被修改了")
                .update();
        Assertions.assertEquals(1, count);
        
        // 链式调用查询验证
        Document document = EsWrappers.lambdaChainQuery(documentMapper).eq(Document::getTitle, "测试文档3").one();
        Assertions.assertEquals(document.getContent(), "测试文档内容3的内容被修改了");
    }

    @Test
    public void testDefaultMethod(){
        List<Document> documents = documentMapper.testDefaultMethod();
        Assertions.assertEquals(documents.size(),1);
    }

    @Test
    public void testSelectById() {
        Document document = documentMapper.selectById(1);
        Assertions.assertEquals("1", document.getEsId());
        Assertions.assertEquals("老汉1", document.getCreator());
    }

    @Test
    public void testSelectBatchIds() {
        List<Document> documents = documentMapper.selectBatchIds(Arrays.asList("1", "2"));
        Assertions.assertEquals(2, documents.size());
    }

    @Test
    public void testSelectList() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCustomField, "字段");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testIgnoreCase() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getCaseTest, "test");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    public void testIp() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getIpAddress, "192.168.0.0/16");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(documents.size(), 1);
    }

    @Test
    public void testSelectCount() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCustomField, "字段");
        Long count = documentMapper.selectCount(wrapper);
        Assertions.assertEquals(22L, count);
    }

    @Test
    public void testSelectCountDistinct() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCustomField, "字段");
        wrapper.distinct(Document::getStarNum);
        Long count = documentMapper.selectCount(wrapper, true);
        Assertions.assertEquals(21L, count);
    }

    @Test
    public void testConditionAllEq() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        Map<String, Object> map = new HashMap<>();
        map.put("title", "测试文档3");
        map.put("creator.keyword", "老汉3");
        map.put("starNum", 3);
        wrapper.allEq(map);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
        Assertions.assertEquals("测试文档3", documents.get(0).getTitle());
    }

    @Test
    public void testConditionEq() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, "测试文档10");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
        Assertions.assertEquals("测试文档10", documents.get(0).getTitle());
    }

    @Test
    public void testConditionGt() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.gt(Document::getStarNum, 20);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(2, documents.size());
    }

    @Test
    public void testConditionGe() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.ge(Document::getStarNum, 20);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(3, documents.size());
    }

    @Test
    public void testConditionLt() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.lt(Document::getStarNum, 3);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(2, documents.size());
    }

    @Test
    public void testConditionLe() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.le(Document::getStarNum, 3);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(3, documents.size());
    }

    @Test
    public void testConditionBetween() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.between(Document::getStarNum, 1, 10);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(10, documents.size());
    }

    @Test
    public void testConditionLike() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.like(Document::getTitle, "试文档");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testConditionLikeLeft() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.likeLeft(Document::getTitle, "文档10");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    public void testConditionLikeRight() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.likeRight(Document::getTitle, "测试文");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testConditionIsNotNull() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.isNotNull(Document::getNullField);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    public void testConditionExists() {
        // exists等价于isNotNull 在es中更推荐此种语法
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.exists(Document::getNullField);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
    }


    @Test
    public void testConditionIn() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.in(Document::getEsId, "1", "2", "3");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(3, documents.size());

        LambdaEsQueryWrapper<Document> wrapper1 = new LambdaEsQueryWrapper<>();
        wrapper1.in(Document::getStarNum, 7, 8);
        List<Document> documents1 = documentMapper.selectList(wrapper1);
        Assertions.assertEquals(2, documents1.size());
    }

    @Test
    public void testConditionGroupBy() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "测试")
                .groupBy(Document::getStarNum);
        SearchResponse response = documentMapper.search(wrapper);
        ParsedLongTerms parsedLongTerms = response.getAggregations()
                .get("starNumTerms");
        Terms.Bucket bucket = parsedLongTerms.getBuckets().get(0);
        Assertions.assertTrue(bucket.getKey().equals(1L) && bucket.getDocCount() == 2L);
    }

    @Test
    public void testConditionMax() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "测试")
                .max(Document::getStarNum);
        SearchResponse response = documentMapper.search(wrapper);
        ParsedMax parsedMax = response.getAggregations()
                .get("starNumMax");
        Assertions.assertTrue(parsedMax.getValue() > 21);
    }

    @Test
    public void testConditionMin() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "测试")
                .min(Document::getStarNum);
        SearchResponse response = documentMapper.search(wrapper);
        ParsedMin parsedMin = response.getAggregations()
                .get("starNumMin");
        Assertions.assertTrue(parsedMin.getValue() > 0 && parsedMin.getValue() < 2);
    }

    @Test
    public void testConditionSum() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "测试")
                .sum(Document::getStarNum);
        SearchResponse response = documentMapper.search(wrapper);
        ParsedSum parsedSum = response.getAggregations()
                .get("starNumSum");
        Assertions.assertTrue(parsedSum.getValue() >= 252);
    }


    @Test
    public void testConditionAvg() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "测试")
                .avg(Document::getStarNum);
        SearchResponse response = documentMapper.search(wrapper);
        ParsedAvg parsedAvg = response.getAggregations()
                .get("starNumAvg");
        Assertions.assertTrue(parsedAvg.getValue() > 11 && parsedAvg.getValue() < 12);
    }


    @Test
    public void testConditionDistinct() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "测试")
                .distinct(Document::getStarNum);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(21, documents.size());
    }

    @Test
    public void testConditionLimit() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉")
                .limit(2, 5);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(5, documents.size());
    }

    @Test
    public void testConditionFromAndSize() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉")
                .from(20)
                .size(2);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(2, documents.size());
    }

    @Test
    public void testConditionIndex() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉")
                .index(EntityInfoHelper.getEntityInfo(Document.class).getIndexName());
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testSetSearchSourceBuilder() {
        // 测试混合查询的另一种方式
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(FieldUtils.val(Document::getCreator), "老汉"));
        Optional.ofNullable(EntityInfoHelper.getEntityInfo(Document.class))
                .flatMap(i -> Optional.ofNullable(i.getMaxResultWindow()))
                .ifPresent(searchSourceBuilder::size);
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.setSearchSourceBuilder(searchSourceBuilder);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testConditionAnd() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.in(Document::getStarNum, 1, 2, 3, 4, 10, 11)
                .and(w -> w.eq(Document::getTitle, "测试文档10").match(Document::getCreator, "老汉"));
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    public void testConditionOr() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.in(Document::getStarNum, 1, 10, 12, 13)
                .or(i -> i.eq(Document::getTitle, "测试文档11").eq(Document::getTitle, "测试文档10"));
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(5, documents.size());
    }

    @Test
    public void testConditionOrInner() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, "测试文档10")
                .or()
                .eq(Document::getTitle, "测试文档20");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(2, documents.size());
    }

    @Test
    public void testConditionFilter() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getStarNum, 10)
                .filter().eq(Document::getTitle, "测试文档10");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    public void testConditionNot() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.in(Document::getStarNum, 10, 11, 12, 13)
                .and(i -> i.not().eq(Document::getTitle, "测试文档10").not().eq(Document::getTitle, "测试文档11"));
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(2, documents.size());
    }


    @Test
    public void testPageQuery() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉");
        EsPageInfo<Document> pageInfo = documentMapper.pageQuery(wrapper, 1, 5);
        Assertions.assertEquals(5, pageInfo.getSize());
        Assertions.assertEquals(22, pageInfo.getTotal());
    }

    @Test
    public void testChainPage() {
        // 链式
        EsPageInfo<Document> pageInfo = EsWrappers.lambdaChainQuery(documentMapper)
                .match(Document::getCreator, "老汉")
                .page(1, 5);
        Assertions.assertEquals(5, pageInfo.getSize());
        Assertions.assertEquals(22, pageInfo.getTotal());
    }


    @Test
    public void testSearchAfter() {
        LambdaEsQueryWrapper<Document> lambdaEsQueryWrapper = EsWrappers.lambdaQuery(Document.class);
        lambdaEsQueryWrapper.size(10);
        lambdaEsQueryWrapper.orderByDesc(Document::getEsId, Document::getStarNum);
        SAPageInfo<Document> saPageInfo = documentMapper.searchAfterPage(lambdaEsQueryWrapper, null, 10);
        //第一页
        System.out.println(saPageInfo);
        Assertions.assertEquals(10, saPageInfo.getList().size());

        //获取下一页
        List<Object> nextSearchAfter = saPageInfo.getNextSearchAfter();
        SAPageInfo<Document> next = documentMapper.searchAfterPage(lambdaEsQueryWrapper, nextSearchAfter, 10);
        Assertions.assertEquals(10, next.getList().size());
    }

    @Test
    public void testFilterField() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, "测试文档10")
                .select(Document::getEsId, Document::getContent);
        Document document = documentMapper.selectOne(wrapper);
        Assertions.assertNotNull(document.getContent());
        Assertions.assertNotNull(document.getEsId());
        Assertions.assertNull(document.getTitle());
    }

    @Test
    public void testNotFilterField() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq(Document::getTitle, "测试文档10")
                .notSelect(Document::getEsId, Document::getContent);
        Document document = documentMapper.selectOne(wrapper);
        Assertions.assertNull(document.getContent());
        Assertions.assertNull(document.getEsId());
        Assertions.assertNotNull(document.getTitle());
    }

    @Test
    public void testOrderByDesc() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉");
        wrapper.orderByDesc(Document::getStarNum);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("22", documents.get(0).getEsId());
        Assertions.assertEquals("21", documents.get(1).getEsId());
    }

    @Test
    public void testOrderByAsc() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉");
        wrapper.orderByAsc(Document::getStarNum, Document::getEsId);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("1", documents.get(0).getEsId());
        Assertions.assertEquals("22", documents.get(21).getEsId());
    }

    @Test
    public void testOrderBy() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉");
        List<OrderByParam> orderByParams = new ArrayList<>();
        OrderByParam orderByParam = new OrderByParam();
        orderByParam.setOrder("star_num");
        orderByParam.setSort("DESC");
        orderByParams.add(orderByParam);
        wrapper.orderBy(orderByParams);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("22", documents.get(0).getEsId());
        Assertions.assertEquals("21", documents.get(1).getEsId());
    }

    @Test
    public void testOrderByDistanceAsc() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        GeoPoint centerPoint = new GeoPoint(41.0, 116.0);
        wrapper.match(Document::getCreator, "老汉")
                .geoDistance(Document::getLocation, 168.8, centerPoint)
                .orderByDistanceAsc(Document::getLocation, centerPoint);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("3", documents.get(0).getEsId());
        Assertions.assertEquals("4", documents.get(3).getEsId());
    }

    @Test
    public void testOrderByDistanceDesc() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        GeoPoint centerPoint = new GeoPoint(41.0, 116.0);
        wrapper.match(Document::getCreator, "老汉")
                .geoDistance(Document::getLocation, 168.8, centerPoint)
                .orderByDistanceDesc(Document::getLocation, centerPoint);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("4", documents.get(0).getEsId());
        Assertions.assertEquals("3", documents.get(3).getEsId());
    }

    @Test
    public void testOrderByDistanceMulti() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        GeoPoint centerPoint = new GeoPoint(41.0, 116.0);
        GeoPoint centerPoint1 = new GeoPoint(42.0, 118.0);
        wrapper.match(Document::getCreator, "老汉")
                .geoDistance(Document::getLocation, 168.8, centerPoint)
                .orderByDistanceDesc(Document::getLocation, centerPoint)
                .orderByDistanceDesc(Document::getLocation, centerPoint1);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("4", documents.get(0).getEsId());
        Assertions.assertEquals("3", documents.get(3).getEsId());
    }

    @Test
    public void testSortByScore() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉11");
        wrapper.sortByScore();
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("11", documents.get(0).getEsId());
    }

    @Test
    public void testSort() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉");
        FieldSortBuilder fieldSortBuilder = SortBuilders.
                fieldSort(FieldUtils.getRealField(
                        FieldUtils.val(Document::getStarNum),
                        EntityInfoHelper.getEntityInfo(Document.class).getMappingColumnMap()));
        fieldSortBuilder.order(SortOrder.DESC);
        wrapper.sort(fieldSortBuilder);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals("22", documents.get(0).getEsId());
        Assertions.assertEquals("21", documents.get(1).getEsId());
    }

    @Test
    public void testMatch() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testMatchPhrase() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.matchPhrase(Document::getContent, "测试");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertTrue(documents.size() > 0);

        LambdaEsQueryWrapper<Document> wrapper1 = new LambdaEsQueryWrapper<>();
        wrapper1.matchPhrase(Document::getContent, "内容测试");
        List<Document> documents1 = documentMapper.selectList(wrapper1);
        Assertions.assertTrue(documents1.size() <= 0);
    }

    @Test
    public void testMatchAllQuery() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.matchAllQuery();
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testMatchPhrasePrefixQuery() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.matchPhrasePrefixQuery(Document::getContent, "测试");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testMultiMatchQuery() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.multiMatchQuery("老汉", Document::getContent, Document::getCreator, Document::getCustomField);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());

        // 先更新数据，插入包含"更新"关键字的内容
        Document document = new Document();
        document.setEsId("2");
        document.setContent("测试文档内容2的内容再次被更新了");
        documentMapper.updateById(document);
        documentMapper.refresh();

        LambdaEsQueryWrapper<Document> wrapper1 = new LambdaEsQueryWrapper<>();
        wrapper1.multiMatchQuery("更新", Document::getContent, Document::getCreator);

        List<Document> documents1 = documentMapper.selectList(wrapper1);
        Assertions.assertEquals(1, documents1.size());
    }

    @Test
    public void testQueryStringQuery() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.queryStringQuery("老汉");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testPrefixQuery() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.prefixQuery(Document::getContent, "测试");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testHighLight() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "测试")
                .match(Document::getCustomField,"字段");
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertTrue(documents.get(0).getHighlightContent().contains("测试"));
    }

    @Test
    public void testGeoBoundingBox() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        GeoPoint leftTop = new GeoPoint(41.187328D, 115.498353D);
        GeoPoint bottomRight = new GeoPoint(39.084509D, 117.610461D);
        wrapper.geoBoundingBox(Document::getLocation, leftTop, bottomRight);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(4, documents.size());
    }


    @Test
    public void testGeoDistance() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        GeoPoint geoPoint = new GeoPoint(41.0, 116.0);
        wrapper.geoDistance(Document::getLocation, 168.8, DistanceUnit.KILOMETERS, geoPoint);
        GeoDistanceSortBuilder geoDistanceSortBuilder = SortBuilders.geoDistanceSort(FieldUtils.val(Document::getLocation), geoPoint)
                .unit(DistanceUnit.KILOMETERS)
                .geoDistance(GeoDistance.ARC)
                .order(SortOrder.DESC);

        wrapper.sort(geoDistanceSortBuilder);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(4, documents.size());
    }

    @Test
    public void testGeoPolygon() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        List<GeoPoint> geoPoints = new ArrayList<>();
        GeoPoint geoPoint = new GeoPoint(40.178012, 116.577188);
        GeoPoint geoPoint1 = new GeoPoint(40.169329, 116.586315);
        GeoPoint geoPoint2 = new GeoPoint(40.178288, 116.591813);
        geoPoints.add(geoPoint);
        geoPoints.add(geoPoint1);
        geoPoints.add(geoPoint2);
        wrapper.geoPolygon(Document::getLocation, geoPoints);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(1, documents.size());
    }

    @Test
    public void testGeoShape() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        Circle circle = new Circle(13, 14, 100);
        wrapper.geoShape(Document::getGeoLocation, circle, ShapeRelation.DISJOINT);
        List<Document> documents = documentMapper.selectList(wrapper);
        Assertions.assertEquals(22, documents.size());
    }

    @Test
    public void testMultiFieldSelect() {
        // 药品 中文名叫葡萄糖酸钙口服溶液 英文名叫 Calcium Gluconate 汉语拼音为 putaotangsuangaikoufurongye
        // 用户可以通过模糊检索,例如输入 Calcium 或 葡萄糖 或 putaotang时对应药品均可以被检索到
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match("english", "Calcium")
                .or()
                .match("multi_field.zh", "葡萄糖")
                .or()
                .match("multi_field.pinyin", "putaotang");
        List<Document> documents = documentMapper.selectList(wrapper);
        System.out.println(documents);
    }

    @Test
    public void testVector() {
        // 向量查询, 查询条件构造
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

    @Test
    public void testSetRequestOptions() {
        // 可设置自定义请求参数,覆盖默认配置, 解决报错 entity content is too long [168583249] for the configured buffer limit [104857600]
        RequestOptions.Builder options = RequestOptions.DEFAULT.toBuilder();
        options.setHttpAsyncResponseConsumerFactory(
                new HttpAsyncResponseConsumerFactory.HeapBufferedResponseConsumerFactory(4 * 104857600));
        final RequestOptions requestOptions = options.build();
        Boolean success = documentMapper.setRequestOptions(requestOptions);
        Assertions.assertTrue(success);
    }

    // 4.删除
    @Test
    public void testDeleteById() {
        int count = documentMapper.deleteById("1");
        Assertions.assertEquals(1, count);
    }

    @Test
    public void testDeleteBatchIds() {
        List<String> idList = Arrays.asList("2", "3", "4");
        int count = documentMapper.deleteBatchIds(idList);
        Assertions.assertEquals(3, count);
    }

    @Test
    public void testDeleteByWrapper() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getCreator, "老汉");

        int count = documentMapper.delete(wrapper);
        Assertions.assertEquals(22, count);
    }

    @Test
    public void testDeleteIndex() {
        // 测试索引存在（实际删除由@AfterAll完成）
        String indexName = EntityInfoHelper.getEntityInfo(Document.class).getIndexName();
        Boolean exists = documentMapper.existsIndex(indexName);
        Assertions.assertTrue(exists);
    }

    @Test
    public void testComplex() {
        // SQL写法
        // where business_type = 1 and (state = 9 or (state = 8 and bidding_sign = 1)) or (business_type = 2 and state in (2,3))

        // RestHighLevelClient写法
        List<Integer> values = Arrays.asList(2, 3);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("business_type", 1));
        boolQueryBuilder.must(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("state", 9))
                .should(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("state", 8)).must(QueryBuilders.termQuery("bidding_sign", 1))));
        boolQueryBuilder.should(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("business_type", 2)).must(QueryBuilders.termsQuery("state", values)));

        System.out.println(boolQueryBuilder);
        System.out.println("--------------------");

        // MP及EE写法
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.eq("business_type", 1)
                .and(a -> a.eq("state", 9).or(b -> b.eq("state", 8).eq("bidding_sign", 1)))
                .or(i -> i.eq("business_type", 2).in("state", 2, 3));
        documentMapper.selectList(wrapper);
    }

}
