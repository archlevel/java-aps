#Java-APS

Java APS容器以及Client

* [Aps APP开发](docs/app_dev.md)
* Aps Client

##系统环境

* Jdk 1.6+
* Maven 3.0+

##Quick Start

build

```
git clone git@git.corp.anjuke.com:_aps/java-aps
cd java-aps
mvn clean install
```

启动样例

```
cd aps-server-assembly/target/aps-server-0.1.0-SNAPSHOT/aps-server-0.1.0-SNAPSHOT/

cp ../../../../aps-test-support/parent-lib/target/test-parent-lib-0.1.0-SNAPSHOT.jar context/

cp ../../../../aps-test-support/child-app/target/test-child-app-0.1.0-SNAPSHOT.tar.gz  aps_apps/

cp ../../../../aps-test-support/child-another/target/test-child-another-0.1.0-SNAPSHOT.tar.gz aps_apps


bin/startup.sh
```

##开发

```
git clone git@git.corp.anjuke.com:_aps/java-aps
cd java-aps
mvn clean install
```
由于一部分测试代码依赖了test-support项目里编译打包好的jar包，所以至少需要mvn package以后才能跑完所有测试

在eclipse里通过m2e插件导入项目即可