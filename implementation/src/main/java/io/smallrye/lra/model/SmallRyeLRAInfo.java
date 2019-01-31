/*
 * Copyright 2019 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.smallrye.lra.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import org.eclipse.microprofile.lra.annotation.CompensatorStatus;
import org.eclipse.microprofile.lra.client.LRAInfo;

public class SmallRyeLRAInfo implements LRAInfo {

    private String lraId;
    private String clientId;
    private String status;
    private boolean complete;
    private boolean compensated;
    private boolean recovering;
    private boolean active;
    private boolean topLevel;
    
    public SmallRyeLRAInfo() {
    }

    public SmallRyeLRAInfo(String lraId, String clientId, String status, boolean isTopLevel) {
        this.lraId = lraId;
        this.clientId = clientId;
        this.status = status;
        this.complete = status.equals(CompensatorStatus.Completed.name());
        this.compensated = status.equals(CompensatorStatus.Compensated.name());
        this.recovering = status.equals(CompensatorStatus.FailedToComplete.name()) || status.equals(CompensatorStatus.FailedToCompensate.name());
        this.active = status.equals(CompensatorStatus.Completing.name()) || status.equals(CompensatorStatus.Compensating.name());
        this.topLevel = isTopLevel;
    }

    @Override
    public String getLraId() {
        return lraId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean isCompensated() {
        return compensated;
    }

    @Override
    public boolean isRecovering() {
        return recovering;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isTopLevel() {
        return topLevel;
    }

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        // Ignore
    }

}
