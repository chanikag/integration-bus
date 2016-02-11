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

package org.wso2.carbon.esb5.mediation.cheetah.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

/**
 * Engine Callback
 */
public class FlowControllerCallback implements CarbonCallback {

    CarbonCallback parentCallback;
    Mediator mediator;

    private static final Logger log = LoggerFactory.getLogger(FlowControllerCallback.class);

    public FlowControllerCallback(CarbonCallback parentCallback, Mediator mediator) {
        this.parentCallback = parentCallback;
        this.mediator = mediator;
    }

    @Override
    public void done(CarbonMessage carbonMessage) {

        if (mediator.getNext() != null) { // If Mediator has a sibling after this
            try {
                mediator.getNext().receive(carbonMessage, parentCallback);
            } catch (Exception e) {
                log.error("Error while mediating", e);
            }
        } else if (parentCallback instanceof FlowControllerCallback) {
            //If no siblings handover message to the requester
            parentCallback.done(carbonMessage);
        }

    }

    public Mediator getMediator() {
        return mediator;
    }

    public CarbonCallback getParentCallback() {
        return parentCallback;
    }

    public void setParentCallback(CarbonCallback parentCallback) {
        this.parentCallback = parentCallback;
    }
}