/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 * <p>
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.esb5.mediation.cheetah.flow.mediators.call;

import org.wso2.carbon.esb5.ServiceContextHolder;
import org.wso2.carbon.esb5.mediation.cheetah.config.CheetahConfigRegistry;
import org.wso2.carbon.esb5.mediation.cheetah.flow.mediators.Mediator;
import org.wso2.carbon.esb5.mediation.cheetah.outbound.OutboundEndpoint;
import org.wso2.carbon.esb5.mediation.cheetah.outbound.protocol.http.HTTPOutboundEndpoint;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;
import org.wso2.carbon.messaging.Constants;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class responsible for send msg out from engine to transport sender
 */
public class CallMediator implements Mediator {

    private String key;

    public CallMediator(String key) {
        this.key = key;
    }

    @Override
    public CarbonCallback receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback)
            throws Exception {
        processRequest(carbonMessage);
        CarbonCallback callback = new CallMediatorCallBack(carbonCallback);
        ServiceContextHolder.getInstance().getSender().send(carbonMessage, callback);
        return callback;
    }

    private void setCarbonHeadersToBackendRequest(CarbonMessage request, String host, int port, String urls) {


        if (request != null) {

            request.setProperty(Constants.HOST, host);
            request.setProperty(Constants.PORT, port);


            request.setProperty(Constants.TO, urls);


            if (port != 80) {
                request.getHeaders().put(Constants.HTTP_HOST, host + ":" + port);
            } else {
                request.getHeaders().put(Constants.HTTP_HOST, host);
            }

        }
    }

    private void processRequest(CarbonMessage carbonMessage) throws MalformedURLException {
        OutboundEndpoint outboundEndpoint = CheetahConfigRegistry.getInstance().getOutboundEndpoint(key);

        if (outboundEndpoint != null) {
            if (outboundEndpoint instanceof HTTPOutboundEndpoint) {
                //TODO: Implement this properly at the endpoint level.
                //TODO: Call mediator is not suppose to handle protocol

                URL url = new URL(((HTTPOutboundEndpoint) outboundEndpoint).getUri());
                String host = url.getHost();
                int port = (url.getPort() == -1) ? 80 : url.getPort();
                String urls = url.getPath();
                setCarbonHeadersToBackendRequest(carbonMessage, host, port, urls);
            }
        }
    }

}
