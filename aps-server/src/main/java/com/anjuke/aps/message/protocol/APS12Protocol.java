package com.anjuke.aps.message.protocol;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.message.serializer.Serializer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

class APS12Protocol implements Protocol {

    private static final Logger LOG = LoggerFactory
            .getLogger(APS12Protocol.class);
    private static final APS12RequestBuilder builder = new APS12RequestBuilder();

    @Override
    public Request deserializeRequest(Deque<byte[]> frame, Serializer serializer) {
        return builder.buildRequest(frame, serializer);
    }

    @Override
    public Response prepareResponse(Request request) {
        return new APS12Response(request.getSequence());
    }

    @Override
    public Deque<byte[]> serializeResponse(Response response,
            Serializer serializer) {
        Deque<byte[]> frames = new LinkedList<byte[]>();
        frames.push(ProtocolFactory.APS_10_VERSION.getBytes());
        int status = response.getStatus();
        List<? extends Object> header = Arrays
                .<Object> asList(response.getSequence(),
                        response.getResponseTimestamp(), status);
        byte[] headerBytes = serializer.writeValue(header);
        frames.push(headerBytes);

        if (status == ApsStatus.SUCCESS) {
            frames.push(serializer.writeValue(response.getResult()));
        } else {
            String errorMessage = response.getErrorMessage();
            if (errorMessage != null) {
                frames.push(serializer.writeString(errorMessage));
            }
        }
        return frames;
    }

    static class APS12Response implements Response {
        private final long sequence;
        private double responseTimestamp;
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
        public double getResponseTimestamp() {
            return responseTimestamp;
        }

        @Override
        public void setResponseTimestamp(double responseTimestamp) {
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
        private final double requestTimestamp;
        private final int expiry;
        private final String requestMethod;
        private final List<Object> requestParams;
        private final Multimap<String, Object> extraMap;

        private APS12Request(long sequence, double requestTimestamp,
                int expiry, String requestMethod, List<Object> requestParams,
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
        public double getRequestTimestamp() {
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
        Request createRequest(long sequence, double timestamp, int expire,
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
                        Object value;
                        if (size == 1) {
                            value = null;
                        } else if (size == 2) {
                            value = extraList.get(1);
                        } else {
                            value = extraList.subList(1, size);
                        }

                        extraMap.put(key, value);

                    } else {
                        LOG.warn("Extra frame not a Array, Dropped. Value is "
                                + extraData);

                    }
                } catch (Exception e) {
                    LOG.warn("Unkown extra frame, dropped", e);
                }
            }

            return new APS12Request(sequence, timestamp, expire, requestMethod,
                    params, extraMap);
        }
    }
}
