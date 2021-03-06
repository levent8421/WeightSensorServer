package com.berrontech.dsensor.dataserver.tcpclient.action.mapping;

import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.common.exception.InternalServerErrorException;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Create By Levent8421
 * Create Time: 2020/6/12 15:16
 * Class Name: HandlerAutoMapping
 * Author: Levent8421
 * Description:
 * Action Auto Mapping
 *
 * @author Levent8421
 */
@Component
@Slf4j
public class HandlerAutoMapping implements ApplicationContextAware {
    private final Map<String, ActionHandler> handlerTable = new HashMap<>(16);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ActionHandlerMapping.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            final String beanName = entry.getKey();
            final Object bean = entry.getValue();
            if (!(bean instanceof ActionHandler)) {
                throw new IllegalArgumentException(String.format("Class [%s][%s] must implement [%s]",
                        bean, beanName, ActionHandler.class));
            }
            final String action = bean.getClass().getAnnotation(ActionHandlerMapping.class).value();
            handlerTable.put(action, (ActionHandler) bean);
        }
        log.info("Mapped Actions [{}]", handlerTable.keySet());
    }

    /**
     * handle Message
     *
     * @param message message
     * @return reply message
     */
    public Message handle(Message message) {
        final String action = message.getAction();
        if (handlerTable.containsKey(action)) {
            final ActionHandler handler = handlerTable.get(action);
            try {
                return handler.onMessage(message);
            } catch (BadRequestException e) {
                final Payload<?> res = Payload.badRequest(e.getMessage(), null);
                log.warn("Error On Handle Message, BadRequest[{}]", message.getSeqNo(), e);
                return MessageUtils.replyMessage(message, res);
            } catch (InternalServerErrorException e) {
                final Payload<?> res = Payload.error(e.getMessage(), null);
                log.warn("Error On Handle Message, InternalServerError[{}]", message.getSeqNo(), e);
                return MessageUtils.replyMessage(message, res);
            } catch (Exception e) {
                final Payload<?> res = Payload.error("Error:" + e.getClass().getSimpleName() + "[" + e.getMessage() + "]");
                log.warn("Error On Handle Message, Unknown Exception[{}]", message.getSeqNo(), e);
                return MessageUtils.replyMessage(message, res);
            }
        }
        final Payload<?> badRequestPayload = Payload.badRequest("Invalidate Action!");
        return MessageUtils.replyMessage(message, badRequestPayload);
    }
}
