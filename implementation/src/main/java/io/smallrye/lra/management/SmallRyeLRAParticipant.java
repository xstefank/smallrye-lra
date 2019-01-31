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
package io.smallrye.lra.management;

import org.eclipse.microprofile.lra.annotation.CompensatorStatus;
import org.eclipse.microprofile.lra.participant.LRAParticipant;
import org.eclipse.microprofile.lra.participant.TerminationException;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class SmallRyeLRAParticipant {
    
    private LRAParticipant delegate;
    private Map<String, LRAResult> associatedResults = new HashMap<>();

    public SmallRyeLRAParticipant(LRAParticipant delegate) {
        this.delegate = delegate;
    }

    public Response complete(URL lraId) {
        return endParticipantExecution(lraId, true);
    }

    public Response compensate(URL lraId) {
        return endParticipantExecution(lraId, false);
    }

    public CompensatorStatus getStatus(URL lraId) {
        LRAResult lraResult = associatedResults.get(lraId.toExternalForm());

        if (lraResult == null) {
            return null;
        }

        Future<Void> result = lraResult.getResult();

        if (lraResult.isCompleting()) {
            return getCompensatorStatusForResult(result, true);
        } else {
            return getCompensatorStatusForResult(result, false);
        }
    }

    private CompensatorStatus getCompensatorStatusForResult(Future<Void> result, boolean completing) {
        if (result.isDone()) {
            return completing ? CompensatorStatus.Completed : CompensatorStatus.Compensated;
        } else if (result.isCancelled()) {
            return completing ? CompensatorStatus.FailedToComplete : CompensatorStatus.FailedToCompensate;
        } else {
            return completing ? CompensatorStatus.Completing : CompensatorStatus.Compensating;
        }
    }

    private Response endParticipantExecution(URL lraId, boolean completing) {
        Response response = null;
        Future<Void> result;

        try {
            if (completing) {
                result = delegate.completeWork(lraId);
            } else {
                result = delegate.compensateWork(lraId);
            }

            if (result == null) {
                response = Response.ok(completing ? CompensatorStatus.Completed.name() : CompensatorStatus.Completing.name()).build();
            } else {
                associatedResults.put(lraId.toExternalForm(), new LRAResult(result, completing));
                response = Response.accepted(completing ? CompensatorStatus.Completing.name() : CompensatorStatus.Compensating.name()).build();
            }
        } catch (TerminationException | NotFoundException e) {
            response = Response.ok(completing ? CompensatorStatus.FailedToComplete.name() : CompensatorStatus.FailedToCompensate.name()).build();
        }

        return response;
    }
    
    private static final class LRAResult {
        private Future<Void> result;
        private boolean completing;

        LRAResult(Future<Void> result, boolean completing) {
            this.result = result;
            this.completing = completing;
        }

        public Future<Void> getResult() {
            return result;
        }

        public boolean isCompleting() {
            return completing;
        }
    }

}
