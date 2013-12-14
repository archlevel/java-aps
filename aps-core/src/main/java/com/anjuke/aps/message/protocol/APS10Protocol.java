package com.anjuke.aps.message.protocol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.message.serializer.Serializer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

class APS10Protocol implements Protocol {

    private static final APS10RequestBuilder requestBuilder = new APS10RequestBuilder();

    private static final APS10ResponseBuilder responseBuilder = new APS10ResponseBuilder();
    private static final APS10ResponseSerializer responseSerializer = new APS10ResponseSerializer();

    @Override
    public Deque<byte[]> serializeRequest(Request request, Serializer serializer) {
        return null;
    }

    @Override
    public Request prepareRequest(long sequence, String requestMethod,
            int expire, Object... params) {
        APS10Request request = new APS10Request(sequence,
                System.currentTimeMillis(), (int) expire, requestMethod,
                Arrays.asList(params));
        return request;
    }

    @Override
    public Request deserializeRequest(Deque<byte[]> frame, Serializer serializer) {
        return requestBuilder.buildRequest(frame, serializer);
    }

    @Override
    public Response prepareResponse(Request request) {
        return new APS10Response(request.getSequence());
    }

    @Override
    public Deque<byte[]> serializeResponse(Response response,
            Serializer serializer) {
        return responseSerializer.serializeResponse(response, serializer);
    }

    @Override
    public Response deserializeResponse(Deque<byte[]> frame,
            Serializer serializer) {
        return responseBuilder.buildResponse(frame, serializer);
    }

    static class APS10Response implements Response {
        private final long sequence;
        private long responseTimestamp;
        private int status = 200;
        private Object result;
        private String errorMessage;

        private APS10Response(long sequence) {
            super();
            this.sequence = sequence;
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
            return ImmutableMultimap.of();
        }

        @Override
        public void setExtra(String key, Object value) {
        }

    }

    static class APS10Request implements Request {

        private final long sequence;
        private final long requestTimestamp;
        private final int expiry;
        private final String requestMethod;
        private final List<Object> requestParams;

        private APS10Request(long sequence, long requestTimestamp, int expiry,
                String requestMethod, List<Object> requestParams) {
            this.sequence = sequence;
            this.requestTimestamp = requestTimestamp;
            this.expiry = expiry;
            this.requestMethod = requestMethod;
            this.requestParams = requestParams;
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
            return null;
        }

        @Override
        public boolean hasExtra(String key) {
            return false;
        }

        @Override
        public Multimap<String, Object> getExtraMap() {
            return ImmutableMultimap.of();
        }

        @Override
        public void setExtra(String key, Object value) {

        }

    }

    static class APS10RequestBuilder extends AbstractRequestBuilder {
        @Override
        Request createRequest(long sequence, double timestamp, double expire,
                String requestMethod, List<Object> params,
                Deque<byte[]> frames, Serializer serializer) {

            return new APS10Request(sequence, (long) timestamp, (int) expire,
                    requestMethod, params);
        }
    }

    static class APS10RequestSerialzer extends AbstractRequestSerializer {
        @Override
        Deque<byte[]> appendFrames(Request request, Serializer serializer,
                Deque<byte[]> frames) {
            return frames;
        }

        @Override
        Object getTimestamp(long timestamp) {
            return timestamp;
        }
    }

    static class APS10ResponseBuilder extends AbstractResponseBuilder {

        @Override
        Response createResponse(long sequence, double timestamp, int status,
                Object result, Deque<byte[]> frames, Serializer serializer) {
            APS10Response response = new APS10Response(sequence);
            response.setResponseTimestamp((long) timestamp);
            response.setStatus(status);
            response.setResult(result);
            if (!frames.isEmpty()) {
                String errorMessage = new String(frames.poll());
                response.setErrorMessage(errorMessage);
            }
            return response;
        }
    }

    static class APS10ResponseSerializer extends AbstractResponseSerializer {
        @Override
        Deque<byte[]> appendFrames(Response response, Serializer serializer,
                Deque<byte[]> frames) {
            String errorMessage = response.getErrorMessage();
            if (errorMessage != null) {
                frames.offer(serializer.writeString(errorMessage));
            }
            return frames;
        }

        @Override
        Object getTimestamp(long timestamp) {
            return timestamp;
        }
    }

}
