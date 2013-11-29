/*
 * Copyright 2013 The FIX.io Project
 *
 * The FIX.io Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package fixio.netty.pipeline;

import fixio.fixprotocol.FixMessage;
import fixio.handlers.AdminEventHandler;
import fixio.handlers.FixMessageHandler;
import fixio.netty.codec.FixMessageDecoder;
import fixio.netty.codec.FixMessageEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * FixChannelInitializer
 *
 * @author Konstantin Pavlov
 */
public abstract class FixChannelInitializer<C extends Channel> extends ChannelInitializer<C> {

    private final EventLoopGroup workerGroup;
    private final AdminEventHandler adminEventHandler;
    private final FixMessageHandler[] appMessageHandlers;
    private final TestRequestHandler testRequestHandler = new TestRequestHandler();

    protected FixChannelInitializer(EventLoopGroup workerGroup, AdminEventHandler adminEventHandler, FixMessageHandler... appMessageHandlers) {
        this.workerGroup = workerGroup;
        this.adminEventHandler = adminEventHandler;
        this.appMessageHandlers = appMessageHandlers;
    }

    @Override
    public void initChannel(C ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new FixMessageDecoder(), new FixMessageEncoder()); // codec
        pipeline.addLast("logging", new LoggingHandler("fix", LogLevel.DEBUG));//logging
        pipeline.addLast("session", createSessionHandler()); // handle fix session
        pipeline.addLast("testRequest", testRequestHandler); // process test requests
        if (adminEventHandler != null) {
            pipeline.addLast(workerGroup, "admin", adminEventHandler); // process admin events
        }
        if (appMessageHandlers != null && appMessageHandlers.length > 0) {
            pipeline.addLast(workerGroup, appMessageHandlers);
        }
    }

    protected abstract MessageToMessageCodec<FixMessage, FixMessage> createSessionHandler();
}