package com.anjuke.aps.message.protocol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.message.serializer.Serializer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

class APS12Protocol implements Protocol {

    private static final Logger LOG = LoggerFactory
            .getLogger(APS12Protocol.class);
    private static final APS12RequestBuilder requestBuilder = new APS12RequestBuilder();
    private static final APS12ResponseSerializer responseSerializer = new APS12ResponseSerializer();

    @Override
    public Request deserializeRequest(Deque<byte[]> frame, Serializer serializer) {
        return requestBuilder.buildRequest(frame, serializer);
    }

    @Override
    public Response prepareResponse(Request request) {
        return new APS12Response(request.getSequence());
    }

    @Override
    public Deque<byte[]> serializeResponse(Response response,
            Serializer serializer) {
        return responseSerializer.serializeResponse(response, serializer);
    }

    static class APS12Response implements Response {
        private final long sequence;
        private long responseTimestamp;
        private int status = 200;
        private Object result;
        private String errorMessage;

        private Multimap<String, Object> extraMap = ArrayListMultimap.create();

        private APS12Response(long sequence) {
            super();
            this.sequence = sequence;
        }

        @Override
        public String getVersion() {
            return ProtocolFactory.APS_12_VERSION;
        }

        @Override
        public long getSequence() {
            return sequence;
        }

        @Override
        public long getResponseTimestamp() {
            return responseTimestamp;
        }

        @Override
        public void setResponseTimestamp(long responseTimestamp) {
            this.responseTimestamp = responseTimestamp;
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void setStatus(int status) {
            this.status = status;

        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public void setResult(Object result) {
            this.result = result;
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public void setErrorMessage(String message) {
            this.errorMessage = message;
        }

        @Override
        public Multimap<String, Object> getExtraMap() {
            return extraMap;
        }

        @Override
        public void setExtra(String key, Object value) {
            extraMap.put(key, value);
        }

    }

    static class APS12Request implements Request {

        private final long sequence;
        private final long requestTimestamp;
        private final int expiry;
        private final String requestMethod;
        private final List<Object> requestParams;
        private final Multimap<String, Object> extraMap;

        private APS12Request(long sequence, long requestTimestamp, int expiry,
                String requestMethod, List<Object> requestParams,
                Multimap<String, Object> extraMap) {
            this.sequence = sequence;
            this.requestTimestamp = requestTimestamp;
            this.expiry = expiry;
            this.requestMethod = requestMethod;
            this.requestParams = requestParams;
            this.extraMap = extraMap;
        }

        @Override
        public String getVersion() {
            return ProtocolFactory.APS_10_VERSION;
        }

        @Override
        public long getSequence() {
            return sequence;
        }

        @Override
        public long getRequestTimestamp() {
            return requestTimestamp;
        }

        @Override
        public int getExpiry() {
            return expiry;
        }

        @Override
        public String getRequestMethod() {
            return requestMethod;
        }

        @Override
        public List<Object> getRequestParams() {
            return requestParams;
        }

        @Override
        public Object getExtra(String key) {
            return extraMap.get(key);
        }

        @Override
        public boolean hasExtra(String key) {

            return extraMap.containsKey(key);
        }

        @Override
        public Multimap<String, Object> getExtraMap() {
            return extraMap;
        }

    }

    static class APS12RequestBuilder extends AbstractRequestBuilder {
        @Override
        Request createRequest(long sequence, double timestamp, double expire,
                String requestMethod, List<Object> params,
                Deque<byte[]> frames, Serializer serializer) {
            Multimap<String, Object> extraMap = ArrayListMultimap.create();
            for (byte[] extra : frames) {
                try {
                    Object extraData = serializer.readValue(extra);
                    if (extraData == null) {
                        continue;
                    }
                    if (extraData instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<Object> extraList = (List<Object>) extraData;
                        if (extraList.isEmpty()) {
                            continue;
                        }

                        String key = String.valueOf(extraList.get(0));
                        int size = extraList.size();
                        if (size == 1) {
                            Object value = null;
                            extraMap.put(key, value);
                        } else if (size == 2) {
                            Object value = extraList.get(1);
                            extraMap.put(key, value);
                        } else {
                            List<Object> value = extraList.subList(1, size);
                            extraMap.putAll(key, value);
                        }


                    } else {
                        LOG.warn("Extra frame not a Array, Dropped. Value is "
                                + extraData);

                    }
                } catch (Exception e) {
                    LOG.warn("Unkown extra frame, dropped", e);
                }
            }

            return new APS12Request(sequence, (long) (timestamp * 1000),
                    (int) (expire * 1000), requestMethod, params, extraMap);
        }
    }

    static class APS12ResponseSerializer extends AbstractResponseSerializer {
        @Override
        Deque<byte[]> appendFrames(Response response, Serializer serializer,
                Deque<byte[]> frames) {
            String errorMessage = response.getErrorMessage();
            if (errorMessage != null) {
                frames.offer(serializer.writeValue(Arrays.asList("errorMsg",
                        errorMessage)));
            }
            Multimap<String, Object> map = response.getExtraMap();
            for (String key : map.keySet()) {
                List<Object> extra = Lists.newArrayListWithCapacity(3);
                extra.add(key);
                Collection<Object> value = map.get(key);
                extra.addAll(value);

                byte[] data = serializer.writeValue(extra);
                frames.offer(data);
            }

            return frames;
        }

        @Override
        Object getTimestamp(long timestamp) {
            return timestamp / 1000.0;
        }
    }
}
