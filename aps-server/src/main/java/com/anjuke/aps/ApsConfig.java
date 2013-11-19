package com.anjuke.aps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.anjuke.aps.message.MessageFilter;
import com.anjuke.aps.server.processor.RequestHandler;

public class ApsConfig {
    public static final String CONFIG_PATH_KEY = "aps.config.file";

    private static final Logger LOG = LoggerFactory.getLogger(ApsConfig.class);
    private static final ApsConfig INSTANCE = new ApsConfig();

    private final Map<String, Object> confMap = new HashMap<String, Object>();

    private ApsConfig() {
        String confFile = System.getProperty(CONFIG_PATH_KEY);

        try {
            readConf(confFile);
        } catch (IOException e) {
            throw new IllegalStateException("Read config error", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void readConf(String confFile) throws IOException {
        InputStream is;
        if (confFile == null) {
            LOG.info("Load APS config from classpath:/aps.yaml");
            is = ApsConfig.class.getResourceAsStream("/aps.yaml");
        } else {
            LOG.info("Load APS config from {}", confFile);
            is = new URL(confFile).openStream();
        }
        Yaml yaml = new Yaml();
        Map<String, Object> conf = (Map<String, Object>) yaml.load(is);
        confMap.putAll(conf);
        LOG.debug("Config content: {}", conf);
        is.close();
    }

    public static ApsConfig getInstance() {
        return INSTANCE;
    }

    public int getPort() {
        return ((Number) confMap.get("aps.zmq.server.port")).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<RequestHandler> getRequestHandler() {
        return (List<RequestHandler>) confMap.get("aps.server.request.handler");
    }

    @SuppressWarnings("unchecked")
    public List<MessageFilter> getMessageFilter() {
        return (List<MessageFilter>) confMap.get("aps.server.message.filter");
    }

    public static void main(String[] args) {
        ApsConfig.getInstance();
    }

}
