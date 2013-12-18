

##APS Client使用

TODO


##APS Java App开发

这里默认App采用Spring框架开发

可以参考[child-app](http://git.corp.anjuke.com/_aps/java-aps/browse/master/aps-test-support/child-app)

###业务逻辑开发

基本无需关心Aps相关的问题，原有的项目可以直接使用

###Maven配置


```

	<!-- pom.xml 配置加入-->
	<dependencies>
		<dependency>
   			<groupId>com.anjuke.aps</groupId>
    		<artifactId>aps-spring-api</artifactId>
    		<version>0.1.0-SNAPSHOT</version>
    		<scope>provided</scope>
		</dependency>
	<dependencies>

	<build>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>false</filtering>
                <excludes>
                    <exclude>META-INF/aps/version</exclude>
                </excludes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
                <includes>
                    <include>META-INF/aps/version</include>
                </includes>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>2.4</version>
                <dependencies>
                    <dependency>
                        <groupId>com.anjuke.aps</groupId>
                        <artifactId>aps-assembly-descriptor</artifactId>
                        <version>0.1.0-SNAPSHOT</version>
                    </dependency>
                </dependencies>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>aps_assemblies</descriptorRef>
                    </descriptorRefs>
                    <appendAssemblyId>false</appendAssemblyId>
                </configuration>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

aps-spring-api是aps的api包<br>
resoures配置用来处理SP的Version，本文后面会具体介绍<br>
assembly plugin用来打包成aps app所需要的tar.gz文件

###APS配置接口

```
import com.anjuke.aps.spring.ApsMethod;
import com.anjuke.aps.spring.ApsModule;
import com.anjuke.aps.test.parent.ParentBean;

@ApsModule(name = "testChildSupport")
public interface TestModule {

    @ApsMethod(bean = "childBeanInject", method = "echo")
    public String echo(String message);

    @ApsMethod(bean = "childBeanInject", method = "parentBean")
    public ParentBean parentBean();

    @ApsMethod(bean = "childBeanXmlConf", method = "echo",targetMethodName="echo")
    public String aaa(String message);

    @ApsMethod(bean = "childBeanXmlConf", method = "parentBean",targetMethodName="parentBean")
    public ParentBean bbb();
}
```
@ApsModule表明这个interface是APS的配置接口，其中的name="testChildSupport"需要和<b>在KT中注册的SP保持一致</b><br>

@ApsMethod则定义一个APS的URL以及对应的处理类，bean是对应的Spring Bean ID, method是该方法对外暴露的方法名。targetMethodName表示对应的bean上的method name，空值表示和method的值一样。方法的返回类型以及参数类型需要和bean上的method保持完全一致，否则在启动时会报错。interface里本身定义的方法名称可以忽略，不对容器产生具体作用

@ApsModule和@ApsMethod的共同使用下，容器会自动生成一个APS url，例如：testChildSupport.childBeanInject.echo，客户端使用该url访问即可

###META-INF配置
在src/main/resources/META-INF/aps里放置两个文件

* version
* aps-app.yaml

version是一个纯文本文件，其内容为${aps.module.version}，在上面的maven配置中，version在maven build的时候会被处理，只需要在build脚本里加入-Daps.module.version=20xxxxxx的版本号，就会被替换成给定的参数的值。APS容器在启动的时候会使用这个文件，向KT注册时，使用此版本号来对应SP的版本

aps-app.yaml是aps的配置文件，类似于servlet里的web.xml。里面的内容为

```
aps.request.handler: !!com.anjuke.aps.server.spring.SpringRequestHandler {
    "contextLocation":"classpath:META-INF/aps/apsSpringContext.xml",
    "parentContextKey":"parentContext"
  }

```

parentContextKey为该Spring容器对应的parentContext的id，类似Spring在Servlet里的配置

contextLocation为该app的Spring配置文件，建议和App本身的配置文件分开。这里在META-INF/aps/ 下单独新建了一个配置文件，内容如下

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"

    xmlns="http://www.springframework.org/schema/beans" xmlns:context="http://www.springframework.org/schema/context"
    xmlns:aps="http://schema.corp.anjuke.com/ajf/aps"
    xsi:schemaLocation="
     http://www.springframework.org/schema/beans
     http://www.springframework.org/schema/beans/spring-beans.xsd
     http://www.springframework.org/schema/context
     http://www.springframework.org/schema/context/spring-context.xsd
     http://schema.corp.anjuke.com/ajf/aps
     http://schema.corp.anjuke.com/ajf/aps/aps.xsd
     ">

    <import resource="classpath:applicationContext.xml"/>
    <aps:service class="com.anjuke.test.child.TestModule"></aps:service>
</beans>


```
该文件引入了改App本来的Spring配置文件。通过<aps:service>来注册之前建立的@ApsModule的接口


###打包

<pre>
mvn clean install -Daps.module.version=20xxxxxx
</pre>
完成后target/*.tar.gz文件就是APS APP打包后的文件，放入APS容器中即可使用

