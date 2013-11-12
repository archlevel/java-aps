package com.anjuke.aps.message.protocol;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.anjuke.aps.message.serializer.Serializer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

class APS10Protocol implements Protocol {

    private static final APS10RequestBuilder builder = new APS10RequestBuilder();

    @Override
    public Request deserializeRequest(Deque<byte[]> frame, Serializer serializer) {
        return builder.buildRequest(frame, serializer);
    }

    @Override
    public Response prepareResponse(Request request) {
        return new APS10Response(request.getSequence());
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

    static class APS10Response implements Response {
        private final long sequence;
        private double responseTimestamp;
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
            return ImmutableMultimap.of();
        }

        @Override
        public void setExtra(String key, Object value) {
        }

    }

    static class APS10Request implements Request {

        private final long sequence;
        private final double requestTimestamp;
        private final int expiry;
        private final String requestMethod;
        private final List<Object> requestParams;

        private APS10Request(long sequence, double requestTimestamp,
                int expiry, String requestMethod, List<Object> requestParams) {
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

    }

    static class APS10RequestBuilder extends AbstractRequestBuilder {
        @Override
        Request createRequest(long sequence, double timestamp, int expire,
                String requestMethod, List<Object> params,
                Deque<byte[]> frames, Serializer serializer) {

            return new APS10Request(sequence, timestamp, expire, requestMethod,
                    params);
        }
    }
}
