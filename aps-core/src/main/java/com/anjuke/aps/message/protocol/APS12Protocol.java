package com.anjuke.aps.message.protocol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import com.anjuke.aps.ExtraMessage;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.message.serializer.Serializer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

class APS12Protocol implements Protocol {

    private static final APS12RequestBuilder requestBuilder = new APS12RequestBuilder();

    private static final APS12RequestSerializer requestSerializer = new APS12RequestSerializer();

    private static final APS12ResponseBuilder responseBuilder = new APS12ResponseBuilder();
    private static final APS12ResponseSerializer responseSerializer = new APS12ResponseSerializer();

    @Override
    public Deque<byte[]> serializeRequest(Request request, Serializer serializer) {
        return requestSerializer.serializeRequest(request, serializer);
    }

    @Override
    public Response deserializeResponse(Deque<byte[]> frame,
            Serializer serializer) {
        return responseBuilder.buildResponse(frame, serializer);
    }

    @Override
    public Request prepareRequest(long sequence, String requestMethod,
            int expire, Object... params) {

        return new APS12Request(sequence, System.currentTimeMillis(), expire,
                requestMethod, Arrays.asList(params),
                ArrayListMultimap.<String, Object> create());
    }

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
            Collection<Object> msg=extraMap.get(ExtraMessage.ERROR_MESSAGE);
            if(msg==null||msg.isEmpty()){
                return null;
            }else{
                return Arrays.toString(msg.toArray());
            }
        }

        @Override
        public void setErrorMessage(String message) {
             this.extraMap.put(ExtraMessage.ERROR_MESSAGE, message);
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
        public Collection<Object> getExtra(String key) {
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

        @Override
        public void setExtra(String key, Object value) {
            extraMap.put(key, value);

        }

    }

    static class APS12RequestBuilder extends AbstractRequestBuilder {
        @Override
        Request createRequest(long sequence, double timestamp, double expire,
                String requestMethod, List<Object> params,
                Deque<byte[]> frames, Serializer serializer) {
            Multimap<String, Object> extraMap = ArrayListMultimap.create();
            ProtocolUtils.parseExtraFrames(extraMap, frames, serializer);
            return new APS12Request(sequence, (long) (timestamp * 1000),
                    (int) (expire * 1000), requestMethod, params, extraMap);
        }
    }

    static class APS12RequestSerializer extends AbstractRequestSerializer {

        @Override
        Deque<byte[]> appendFrames(Request request, Serializer serializer,
                Deque<byte[]> frames) {
            Multimap<String, Object> map = request.getExtraMap();
            return ProtocolUtils.appendExtraFrames(map, frames, serializer);
        }

        @Override
        Object getTimestamp(long timestamp) {
            return timestamp / 1000.0;
        }
    }

    static class APS12ResponseSerializer extends AbstractResponseSerializer {
        @Override
        Deque<byte[]> appendFrames(Response response, Serializer serializer,
                Deque<byte[]> frames) {
            Multimap<String, Object> map = response.getExtraMap();
            return ProtocolUtils.appendExtraFrames(map, frames, serializer);
        }

        @Override
        Object getTimestamp(long timestamp) {
            return timestamp / 1000.0;
        }
    }

    static class APS12ResponseBuilder extends AbstractResponseBuilder {
        @Override
        Response createResponse(long sequence, double timestamp, int status,
                Object result, Deque<byte[]> frames, Serializer serializer) {
            APS12Response response = new APS12Response(sequence);
            response.setResponseTimestamp((long) (timestamp * 1000));
            response.setStatus(status);
            response.setResult(result);
            ProtocolUtils.parseExtraFrames(response.extraMap, frames, serializer);
            return response;
        }
    }
}
