# Easy-Es for elasticsearch 测试运行指南

## 快速开始
先确认 Easysearch 服务已经运行，并在 easy-es-spring-test/src/test/resources/spring-context.xml 里配置好连接参数。

### 运行完整测试套件

```bash
mvn clean test -pl easy-es-spring-test -am -DfailIfNoTests=false -Dtest=XmlScannerAllTest
```

### 运行单个测试方法

```bash
# 先编译依赖
mvn clean test-compile -pl easy-es-spring-test -am -q

# 运行指定测试
mvn test -pl easy-es-spring-test -Dtest=XmlScannerAllTest#testInsert
```

### 查看文本报告

```bash
# 查看简洁报告
cat easy-es-spring-test/target/surefire-reports/org.dromara.easyes.test.all.XmlScannerAllTest.txt
```
