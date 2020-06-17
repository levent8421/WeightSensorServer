package com.berrontech.dsensor.dataserver.tcpclient.action.handlers;

import com.berrontech.dsensor.dataserver.common.entity.DeviceConnection;
import com.berrontech.dsensor.dataserver.common.exception.BadRequestException;
import com.berrontech.dsensor.dataserver.service.general.DeviceConnectionService;
import com.berrontech.dsensor.dataserver.tcpclient.action.ActionHandler;
import com.berrontech.dsensor.dataserver.tcpclient.action.mapping.ActionHandlerMapping;
import com.berrontech.dsensor.dataserver.tcpclient.util.MessageUtils;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Message;
import com.berrontech.dsensor.dataserver.tcpclient.vo.Payload;
import com.berrontech.dsensor.dataserver.tcpclient.vo.data.DeviceConnectionAddParams;
import lombok.val;

import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notEmpty;
import static com.berrontech.dsensor.dataserver.common.util.ParamChecker.notNull;

/**
 * Create By Levent8421
 * Create Time: 2020/6/16 19:02
 * Class Name: ConnectionAddHandler
 * Author: Levent8421
 * Description:
 * 新增连接
 *
 * @author Levent8421
 */
@ActionHandlerMapping("connection.add")
public class ConnectionAddHandler implements ActionHandler {
    private final DeviceConnectionService deviceConnectionService;

    public ConnectionAddHandler(DeviceConnectionService deviceConnectionService) {
        this.deviceConnectionService = deviceConnectionService;
    }

    @Override
    public Message onMessage(Message message) {
        final DeviceConnectionAddParams params;
        try {
            params = MessageUtils.asObject(message.getData(), DeviceConnectionAddParams.class);
            notNull(params, BadRequestException.class, "Invalidate Params!");
            notEmpty(params.getType(), BadRequestException.class, "Type could not be blank!");
            notEmpty(params.getTarget(), BadRequestException.class, "Target could not be empty!");
            if (!(DeviceConnectionAddParams.TYPE_SERIAL.equals(params.getType()) || DeviceConnectionAddParams.TYPE_NET.equals(params.getType()))) {
                throw new BadRequestException("Invalidate Connection Type!");
            }
        } catch (Exception e) {
            val data = Payload.badRequest(e.getMessage());
            return MessageUtils.replyMessage(message, data);
        }
        final int type = asConnectionType(params.getType());
        final String target = params.getTarget();
        final DeviceConnection connection = new DeviceConnection();
        connection.setTarget(target);
        connection.setType(type);
        val saved = deviceConnectionService.save(connection);

        val data = Payload.ok(saved.getId());
        return MessageUtils.replyMessage(message, data);
    }

    /**
     * Convert string connection type to int enum connection type
     *
     * @param type string connection type
     * @return int enum connection type
     */
    private int asConnectionType(String type) {
        return DeviceConnectionAddParams.TYPE_SERIAL.equals(type) ?
                DeviceConnection.TYPE_SERIAL : DeviceConnectionAddParams.TYPE_NET.equals(type)
                ? DeviceConnection.TYPE_NET : -1;
    }
}
