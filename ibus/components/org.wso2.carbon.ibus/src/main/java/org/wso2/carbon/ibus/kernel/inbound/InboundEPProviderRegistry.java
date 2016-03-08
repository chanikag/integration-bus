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

package org.wso2.carbon.ibus.kernel.inbound;

import org.wso2.carbon.messaging.TransportListenerManager;

import java.util.HashMap;
import java.util.Map;

public class InboundEPProviderRegistry {

    private Map<String, InboundEPProvider> inboundEndpointProviders = new HashMap<>();

    private static InboundEPProviderRegistry instance = new InboundEPProviderRegistry();

    private InboundEPProviderRegistry() {}

    public static InboundEPProviderRegistry getInstance() {
        return instance;
    }

    public void registerInboundEPProvider(InboundEPProvider inboundEndpointProvider) {
        inboundEndpointProviders.put(inboundEndpointProvider.getProtocol(), inboundEndpointProvider);
    }

    public void unregisterInboundEPProvider(InboundEPProvider inboundEndpointProvider) {
        inboundEndpointProviders.remove(inboundEndpointProvider.getProtocol());
    }

    public InboundEPProvider getProvider(String protocol) {
        return inboundEndpointProviders.get(protocol);
    }


}