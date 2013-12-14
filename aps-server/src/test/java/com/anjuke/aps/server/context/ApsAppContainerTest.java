package com.anjuke.aps.server.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.anjuke.aps.SimpleRequest;
import com.anjuke.aps.SimpleResponse;

public class ApsAppContainerTest {

    @Test
    public void test() throws Exception {
        String userdir = System.getProperty("user.dir");

        assertTrue(userdir.endsWith("/aps-server"));

        URL url = new URL("file:" + userdir
                + "/../aps-test-support/single-app/target/classes/");
        File f=new File(url.toURI());
        assertTrue(f.exists());
        assertTrue(f.isDirectory());
        ApsAppContainer container = new ApsAppContainer(f.getName(),
                "META-INF/aps-app.yaml", this.getClass().getClassLoader(), url);
        container.init();

        SimpleRequest request=new SimpleRequest();
        SimpleResponse response=new SimpleResponse();
        List<Object> data=Arrays.<Object>asList("test echo message");
        request.setRequestParams(data);
        container.handle(request, response);
        Object result=response.getResult();
        assertEquals(data,result);

        container.destroy();

        System.out.println(container);
    }
}
