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

package org.wso2.carbon.ibus.mediation.cheetah.inbound.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.ibus.mediation.cheetah.inbound.InboundEndpoint;
import org.wso2.carbon.ibus.mediation.cheetah.inbound.protocols.http.HTTPInboundEP;
import org.wso2.carbon.ibus.mediation.cheetah.inbound.protocols.http.HTTPSInboundEP;
import org.wso2.carbon.messaging.TransportListener;
import org.wso2.carbon.messaging.TransportListenerManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An InboundEndpoint Manager class
 */
public class InboundEndpointManager implements TransportListenerManager {

    private Map<String, TransportListener> listenerMap = new ConcurrentHashMap<>();

    private Map<String, InboundEndpoint> earlyInbounds = new ConcurrentHashMap<>();

    private Map<String, InboundEndpoint> deployedInbounds = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(InboundEndpointManager.class);

    private static InboundEndpointManager inboundEndpointDeployer = new InboundEndpointManager();

    public static InboundEndpointManager getInstance() {
        return inboundEndpointDeployer;
    }

    private InboundEndpointManager() {
    }

    @Override
    public TransportListener getTransportListener(String s) {
        return null;
    }

    @Override
    public synchronized void registerTransportListener(String id, TransportListener transportListener) {
        listenerMap.put(id, transportListener);
        for (Map.Entry entry : earlyInbounds.entrySet()) {
            //TODO check relevant mapping listneres
            deploy((InboundEndpoint) entry.getValue());
            earlyInbounds.remove(entry.getKey());
        }
    }

    public synchronized void deploy(InboundEndpoint inboundEndpoint) {
        if (listenerMap.size() == 0) {
            earlyInbounds.put(inboundEndpoint.getName(), inboundEndpoint);
            return;
        }
        if (inboundEndpoint instanceof HTTPInboundEP) {
            int port = ((HTTPInboundEP) inboundEndpoint).getPort();
            String host = ((HTTPInboundEP) inboundEndpoint).getHost();
            String name = inboundEndpoint.getName();
            TransportListener transportListener = listenerMap.get("netty-gw");
            if (transportListener != null) {
                InboundEndpoint deployedInbound = deployedInbounds.get(name);

                if (deployedInbound != null) {
                    //if already deployed and updating port or host
                    if (!((((HTTPInboundEP) deployedInbound).getHost().equals(host)) &&
                          ((HTTPInboundEP) deployedInbound).getPort() == port)) {
                        transportListener.stopListening(((HTTPInboundEP) deployedInbound).getHost(),
                                                        ((HTTPInboundEP) deployedInbound).getPort());
                        deployedInbounds.remove(name);
                    } else {
                        // if not updating port or host no need to update transport listener
                        deployedInbounds.put(name, inboundEndpoint);
                        return;
                    }
                } else {
                    //reusing already open ports
                    for (Map.Entry entry : deployedInbounds.entrySet()) {
                        if (port == (((HTTPInboundEP) entry.getValue()).getPort()) &&
                            (((HTTPInboundEP) entry.getValue()).getHost().equals(host))) {
                            deployedInbounds.put(name, inboundEndpoint);
                            logger.info("Reusing already open port " + port + " in host " + host +
                                        " for " + " Inbound Endpoint " + name);
                            return;
                        }
                    }
                }

                if (inboundEndpoint instanceof HTTPSInboundEP) {
                    transportListener.listen(host, port, ((HTTPSInboundEP) inboundEndpoint).getParMap());
                } else {
                    transportListener.listen(host, port);
                }

                deployedInbounds.put(name, inboundEndpoint);
            } else {
                earlyInbounds.put(inboundEndpoint.getName(), inboundEndpoint);
            }
        }
    }

    public void undeploy(InboundEndpoint inboundEndpoint) {
        deployedInbounds.remove(inboundEndpoint.getName());
        TransportListener transportListener = listenerMap.get("netty-gw");
        if (inboundEndpoint instanceof HTTPInboundEP && transportListener != null) {
            transportListener.stopListening(((HTTPInboundEP) inboundEndpoint).getHost(),
                                            ((HTTPInboundEP) inboundEndpoint).getPort());
        }
    }
}
