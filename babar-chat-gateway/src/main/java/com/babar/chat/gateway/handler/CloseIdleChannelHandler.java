package com.babar.chat.gateway.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@ChannelHandler.Sharable
@Component
public class CloseIdleChannelHandler extends ChannelDuplexHandler {

    @Autowired
    private WebsocketRouterHandler websocketRouterHandler;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                log.info("Connector not receiving ping packet from client, will close channel:{}", ctx.channel());
                websocketRouterHandler.cleanUserChannel(ctx.channel());
                ctx.close();
            }
        }
    }
}
