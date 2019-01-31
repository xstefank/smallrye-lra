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
package io.smallrye.lra.api;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.smallrye.lra.model.LRAConstants.LRA_ID_PATH_PARAM;
import static io.smallrye.lra.model.LRAConstants.RECOVERY_ID_PATH_PARAM;

@Path("/lra-recovery-coordinator")
@RegisterRestClient
public interface LRARecoveryCoordinator {

    @GET
    @Path("/recovery")
    @Produces(MediaType.APPLICATION_JSON)
    Response getRecoveringLRAs();
    
    @GET
    @Path("/{" + LRA_ID_PATH_PARAM + "}/{" + RECOVERY_ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getCompensator(@PathParam(LRA_ID_PATH_PARAM) String lraId, @PathParam(RECOVERY_ID_PATH_PARAM) String recoveryId);

    @PUT
    @Path("/{" + LRA_ID_PATH_PARAM + "}/{" + RECOVERY_ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_JSON)
    Response updateCompensator(@PathParam(LRA_ID_PATH_PARAM) String lraId, @PathParam(RECOVERY_ID_PATH_PARAM) String recoveryId, String body);
}
