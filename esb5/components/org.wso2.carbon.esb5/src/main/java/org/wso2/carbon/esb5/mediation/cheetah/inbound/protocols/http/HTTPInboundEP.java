/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.esb5.mediation.cheetah.inbound.protocols.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.esb5.mediation.cheetah.inbound.InboundEndpoint;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.Constants;


/**
 * HTTP Inbound Endpoint
 */
public class HTTPInboundEP extends InboundEndpoint {

    private String context;

    private int port;

    private static final Logger log = LoggerFactory.getLogger(InboundEndpoint.class);

    public HTTPInboundEP(String name, int port) {
        super(name);
        this.port = port;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getPort() {
        return port;
    }

    public boolean canReceive(CarbonMessage cMsg) {

        String uri = (String) cMsg.getProperty(Constants.TO);
        if (uri.startsWith(context)) {
            return true;
        }
        return false;
    }

    public boolean receive(CarbonMessage cMsg, CarbonCallback callback) {
        log.info("HTTP Endpoint Processing");
        super.receive(cMsg, callback);
        return true;
    }

}