package org.dromara.easyes.test.join;


import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.dromara.easyes.core.toolkit.EntityInfoHelper;
import org.dromara.easyes.core.toolkit.FieldUtils;
import org.dromara.easyes.test.TestEasyEsApplication;
import org.dromara.easyes.test.entity.Comment;
import org.dromara.easyes.test.entity.Document;
import org.dromara.easyes.test.mapper.CommentMapper;
import org.dromara.easyes.test.mapper.DocumentMapper;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 父子类型测试
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@DisplayName("easy-es父子类型相关功能单元测试")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = TestEasyEsApplication.class)
public class JoinTest {
    @Resource
    private DocumentMapper documentMapper;
    @Resource
    private CommentMapper commentMapper;

    @Test
    @Order(0)
    public void testCreateIndex() {
        // 0.前置操作 创建索引 需确保索引托管模式处于manual手动挡,若为自动挡则会冲突.
        boolean success = documentMapper.createIndex();
        Assertions.assertTrue(success);
    }

    @Test
    @Order(1)
    public void testInsert() throws InterruptedException {
        // 测试新增父子文档,此处开启自动挡模式,父子类型索引已被自动处理
        // 新新增父文档,然后再插入子文档
        String parentId = "doc-1";
        Document document = new Document();
        // 此处id为自定义id,也可去掉注解@IndexId(type = IdType.CUSTOMIZE),让es自行生成id(推荐), insert完成后 es生成的id直接从document中取获取
        document.setEsId(parentId);
        document.setTitle("我是父文档的标题");
        document.setContent("我是父文档的内容 father content ");
        documentMapper.insert(document);

        // 插入子文档
        Comment comment = new Comment();
        comment.setId("comment-1");
        comment.setCommentContent("test1");

        // 这里特别注意,子文档必须指定其路由为父亲的id,否则傻儿子找不到爹别怪我没提醒 (es语法如此,非框架限制)
        commentMapper.insert(comment, parentId);

        // 插入子文档2
        Comment comment1 = new Comment();
        comment1.setId("comment-2");
        comment1.setCommentContent("test2");
        commentMapper.insert(comment1, parentId);

        // es写入数据有延迟 适当休眠 保证后续查询结果正确
        Thread.sleep(2000);
    }

    @Test
    @Order(2)
    public void testSelect() {
        // 温馨提示,下面wrapper中的type实际上就是JoinField字段注解@TableField中指定的parentName和childName,与原生语法是一致的
        // case1: hasChild查询,返回的是相关的父文档 所以查询用父文档实体及其mapper
        LambdaEsQueryWrapper<Document> documentWrapper = new LambdaEsQueryWrapper<>();
        documentWrapper.hasChild("comment", w -> w.eq(FieldUtils.val(Comment::getCommentContent), "test1"));
        List<Document> documents = documentMapper.selectList(documentWrapper);
        System.out.println(documents);

        // case2: hasParent查询,返回的是相关的子文档 所以查询用子文档实体及其mapper
        LambdaEsQueryWrapper<Comment> commentWrapper = new LambdaEsQueryWrapper<>();
        // 字段名称你也可以不用FieldUtils.val,直接传入字符串也行
        commentWrapper.hasParent("document", w -> w.match("content", "father").or().match("content", "content"));
        List<Comment> comments = commentMapper.selectList(commentWrapper);
        System.out.println(comments);

        // case3: parentId查询,返回的是相关的子文档,与case2类似,所以查询用子文档实体及其mapper
        commentWrapper = new LambdaEsQueryWrapper<>();
        commentWrapper.parentId("doc-1", "comment");
        List<Comment> commentList = commentMapper.selectList(commentWrapper);
        System.out.println(commentList);
    }

    @Test
    @Order(3)
    public void testUpdate() {
        // case1: 父文档/子文档 根据各自的id更新
        Document document = new Document();
        document.setEsId("doc-1");
        document.setTitle("我是隔壁老王标题");
        documentMapper.updateById(document);

        // case2: 父文档/子文档 根据各自条件更新
        Comment comment = new Comment();
        comment.setCommentContent("我是隔壁老王的评论");
        LambdaEsUpdateWrapper<Comment> wrapper = new LambdaEsUpdateWrapper<>();
        wrapper.match(Comment::getCommentContent, "comment");
        commentMapper.update(comment, wrapper);
    }

    @Test
    @Order(4)
    public void testDelete() {
        // case1: 父文档/子文档 根据各自的id删除
        documentMapper.deleteById("doc-1");

        //case2: 父文档/子文档 根据各自条件删除
        LambdaEsQueryWrapper<Comment> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Comment::getCommentContent, "comment");
        commentMapper.delete(wrapper);
    }

    @Test
    @Order(5)
    public void testDeleteIndex() {
        // 测试完成,删除索引
        boolean deleted = documentMapper.deleteIndex(EntityInfoHelper.getEntityInfo(Document.class).getIndexName());
        Assertions.assertTrue(deleted);
    }

}
