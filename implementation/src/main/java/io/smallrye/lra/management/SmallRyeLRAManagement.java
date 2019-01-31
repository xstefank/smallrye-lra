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

import io.smallrye.lra.SmallRyeLRAClient;
import io.smallrye.lra.utils.Utils;
import org.eclipse.microprofile.lra.client.GenericLRAException;
import org.eclipse.microprofile.lra.participant.JoinLRAException;
import org.eclipse.microprofile.lra.participant.LRAManagement;
import org.eclipse.microprofile.lra.participant.LRAParticipant;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class SmallRyeLRAManagement implements LRAManagement {
    
    private static final Map<String, SmallRyeLRAParticipant> participants = new HashMap<>();
    
    @Inject
    private SmallRyeLRAClient lraClient;
    
    @Inject
    private UriInfo uriInfo;
    
    @Override
    public URL joinLRA(LRAParticipant participant, URL lraId, Long timeLimit, ChronoUnit unit) throws JoinLRAException {
        URL recoveryUrl;
        String participantId = UUID.randomUUID().toString();
        
        try {
            String baseUrl = uriInfo.getBaseUri().toString() + "/lra-participant/" + Utils.extractLraId(lraId) + "/" + participantId;
            recoveryUrl = lraClient.joinLRA(lraId, Duration.of(timeLimit, unit).toMillis(),
                    new URL(baseUrl + "/compensate"),
                    new URL(baseUrl + "/complete"),
                    new URL(baseUrl), new URL(baseUrl),
                    new URL(baseUrl + "/status"), null);
            participants.put(participantId, new SmallRyeLRAParticipant(participant));
        } catch (GenericLRAException e) {
            throw new JoinLRAException(lraId, e.getStatusCode(), e.getMessage(), e);
        } catch (MalformedURLException e) {
            throw new JoinLRAException(lraId, -1, "Unable to join LRA", e);
        }
        
        return recoveryUrl;
    }

    @Override
    public URL joinLRA(LRAParticipant participant, URL lraId) throws JoinLRAException {
        return joinLRA(participant, lraId, 0L, ChronoUnit.SECONDS);
    }
    

    public SmallRyeLRAParticipant getParticipant(String participantId, URL lraId, byte[] data) {
        return participants.get(participantId);
    }
}
