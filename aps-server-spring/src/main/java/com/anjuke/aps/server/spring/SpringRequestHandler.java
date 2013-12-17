package com.anjuke.aps.server.spring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.context.ApplicationContext;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.ModuleVersion;
import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
import com.anjuke.aps.exception.ApsException;
import com.anjuke.aps.spring.ApsMethod;
import com.anjuke.aps.spring.ApsModule;
import com.anjuke.aps.spring.config.ApsServiceInstance;
import com.anjuke.aps.util.Asserts;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class SpringRequestHandler implements RequestHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(SpringRequestHandler.class);

    private static final String VERSION_PATH = "META-INF/aps/version";

    private static final String DEFAULT_VERSION = new SimpleDateFormat(
            "yyyyMMdd").format(new Date());

    private String contextLocation = "classpath*:applicationContext.xml";

    private String parentContextKey;

    private ClassPathXmlApplicationContext applicationContext;

    private ObjectMapper objectMapper;

    private Map<String, ApsMethodInvoker> methodBeanCache;

    private Set<ModuleVersion> modules = Sets.newHashSet();

    private BeanFactoryReference parentReference;

    public String getParentContextKey() {
        return parentContextKey;
    }

    public void setParentContextKey(String parentContextKey) {
        this.parentContextKey = parentContextKey;
    }

    public String getContextLocation() {
        return contextLocation;
    }

    public void setContextLocation(String contextLocation) {
        this.contextLocation = contextLocation;
    }

    @Override
    public void init() {
        Asserts.notNull(contextLocation,
                "Spring context file location not be null");

        objectMapper = new ObjectMapper();
        objectMapper.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,
                false);

        if (parentContextKey != null && !parentContextKey.isEmpty()) {
            BeanFactoryLocator locator = ContextSingletonBeanFactoryLocator
                    .getInstance();

            parentReference = locator.useBeanFactory(parentContextKey);
            ApplicationContext parentContext = (ApplicationContext) parentReference
                    .getFactory();

            applicationContext = new ClassPathXmlApplicationContext(
                    new String[] { contextLocation }, parentContext);
        } else {
            applicationContext = new ClassPathXmlApplicationContext(
                    contextLocation);
        }

        methodBeanCache = Maps.newHashMap();
        Map<String, ApsServiceInstance> instancesMap = applicationContext
                .getBeansOfType(ApsServiceInstance.class);

        for (ApsServiceInstance instance : instancesMap.values()) {
            Class<?> clazz = instance.getServiceClass();
            ApsModule moduleAnnotation = clazz.getAnnotation(ApsModule.class);
            if (moduleAnnotation == null) {
                LOG.warn(clazz + " not annotated by @ApsModule, register skip");
                continue;
            }
            String contextName = moduleAnnotation.name();
            Method[] methodArray = clazz.getDeclaredMethods();
            for (Method method : methodArray) {
                ApsMethod apsMethod = method.getAnnotation(ApsMethod.class);
                if (apsMethod == null) {
                    continue;
                }
                String beanName = apsMethod.bean();
                String methodName = apsMethod.method();
                String url = contextName + "." + beanName + "." + methodName;

                Object bean = applicationContext.getBean(beanName);
                if (bean == null) {
                    throw new NullPointerException("bean " + beanName
                            + " in context " + contextName + " not found");
                }
                Class<?>[] parameterClasses = method.getParameterTypes();

                String targetMethodName = StringUtils.isEmpty(apsMethod
                        .targetMethodName()) ? method.getName() : apsMethod
                        .targetMethodName();
                try {
                    Method targetMethod = bean.getClass().getDeclaredMethod(
                            targetMethodName, parameterClasses);
                    Object o = methodBeanCache.put(url, new ApsMethodInvoker(
                            bean, targetMethod, method));
                    if (o != null) {
                        throw new IllegalStateException(
                                "duplicate aps url regestered: " + url);
                    }
                } catch (SecurityException e) {
                    LOG.error(e.getMessage(), e);
                    throw e;
                } catch (NoSuchMethodException e) {
                    String msg = "target method " + targetMethodName
                            + "in bean " + beanName + " in context "
                            + contextName + " not found, " + e.getMessage();
                    throw new ApsException(msg, e);
                }
            }
            modules.add(new ModuleVersion(contextName, getVersion()));
        }
    }

    private String getVersion() {
        InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(VERSION_PATH);
        if (is == null) {
            return DEFAULT_VERSION;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            return br.readLine();
        } catch (IOException ioe) {
            LOG.warn("Read version file error", ioe);
            return DEFAULT_VERSION;
        }

    }

    @Override
    public Set<String> getRequestMethods() {
        return methodBeanCache.keySet();
    }

    @Override
    public void destroy() {
        methodBeanCache.clear();
        try {
            applicationContext.close();
        } finally {
            parentReference.release();
        }
    }

    @Override
    public Set<ModuleVersion> getModules() {
        return modules;
    }

    public void handle(Request request, Response response) {
        String method = request.getRequestMethod();
        ApsMethodInvoker invoker = methodBeanCache.get(method);
        if (invoker == null) {
            throw new IllegalStateException();
        }

        List<Object> parameters = request.getRequestParams();

        Object[] convertedParams = convertParameters(parameters,
                invoker.getGenericParameterTypes());
        try {
            Object result = invoker.invoke(convertedParams);
            response.setResult(objectMapper.convertValue(result, Object.class));
            response.setStatus(ApsStatus.SUCCESS);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            response.setErrorMessage(e.getMessage());
            response.setStatus(ApsStatus.INTENAL_SERVER_ERROR);
        }

    };

    private Object[] convertParameters(List<Object> parameter,
            Type[] expectedTypes) {
        int size = parameter.size();
        if (size != expectedTypes.length) {
            throw new IllegalStateException("Aps request parameter length is "
                    + size + " and declaired method parameter length is"
                    + expectedTypes.length);
        }

        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            JavaType javaType = objectMapper.getTypeFactory().constructType(
                    expectedTypes[i]);
            result[i] = objectMapper.convertValue(parameter.get(i), javaType);
        }
        return result;
    }

}
