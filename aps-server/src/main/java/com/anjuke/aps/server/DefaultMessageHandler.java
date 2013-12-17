package com.anjuke.aps.server;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.Request;
import com.anjuke.aps.Response;
import com.anjuke.aps.exception.ApsException;
import com.anjuke.aps.message.MessageChannel;
import com.anjuke.aps.message.MessageFilter;
import com.anjuke.aps.message.MessageHandler;
import com.anjuke.aps.message.protocol.Protocol;
import com.anjuke.aps.message.protocol.ProtocolFactory;
import com.anjuke.aps.message.serializer.MessagePackSerializer;
import com.anjuke.aps.message.serializer.Serializer;
import com.anjuke.aps.server.processor.RequestProcessor;
import com.anjuke.aps.util.Asserts;

public class DefaultMessageHandler implements MessageHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(DefaultMessageHandler.class);

    private final List<MessageFilter> filterList = new ArrayList<MessageFilter>();



    private Serializer serializer = new MessagePackSerializer();
    private RequestProcessor processor;

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public RequestProcessor getProcessor() {
        return processor;
    }

    public void addFilter(MessageFilter filter) {
        filterList.add(filter);
    }

    public void setProcessor(RequestProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void init(ApsContext context) {
        Asserts.notNull(serializer, "Serializer must not be null");
        Asserts.notNull(processor, "Processor must not be null");
        for (MessageFilter filter : filterList) {
            try {
                filter.init(context);
            } catch (Exception e) {
                throw new ApsException("MessageHandlerFilter init Error", e);
            }
        }
        processor.init(context);
    }

    @Override
    public void destroy() {
        processor.destroy();
        for (MessageFilter filter : filterList) {
            try {
                filter.destroy();
            } catch (Exception e) {
                LOG.warn("Filter " + filter + " destory error", e);
            }
        }

    }

    @Override
    public void handlerMessage(MessageChannel channel) {

        Deque<byte[]> requestFrames = channel.receive();
        Protocol protocol = ProtocolFactory.parseProtocol(requestFrames,
                serializer);
        Request request = protocol
                .deserializeRequest(requestFrames, serializer);
        Response response = protocol.prepareResponse(request);

        for (MessageFilter filter : filterList) {
            boolean result = filter.beforeHandle(request, response);
            if (!result) {
                return;
            }
        }

        try {
            processor.process(request, response);
        } catch (Exception e) {
            response.setStatus(ApsStatus.INTENAL_SERVER_ERROR);
            response.setErrorMessage(e.getMessage());
            LOG.error(e.getMessage(), e);
        }

        for (ListIterator<MessageFilter> iterator = filterList
                .listIterator(filterList.size()); iterator.hasPrevious();) {
            MessageFilter filter = iterator.previous();
            filter.afterHandler(request, response);
        }

        Deque<byte[]> responseFrames = protocol.serializeResponse(response,
                serializer);
        channel.send(responseFrames);
    }

}
