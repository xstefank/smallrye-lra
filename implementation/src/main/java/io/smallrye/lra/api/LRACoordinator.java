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

import org.eclipse.microprofile.lra.client.LRAClient;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.smallrye.lra.model.LRAConstants.CLIENT_ID;
import static io.smallrye.lra.model.LRAConstants.LRA_ID_PATH_PARAM;
import static io.smallrye.lra.model.LRAConstants.PARENT_LRA;
import static io.smallrye.lra.model.LRAConstants.STATUS;
import static io.smallrye.lra.model.LRAConstants.TIMELIMIT;

@Path("/lra-coordinator")
@RegisterRestClient
public interface LRACoordinator {

    @POST
    @Path("/start")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    Response startLRA(@QueryParam(PARENT_LRA) @DefaultValue("") String parentLRA,
                      @QueryParam(CLIENT_ID) @DefaultValue("") String clientID,
                      @QueryParam(TIMELIMIT) @DefaultValue("0") Long timelimit);

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getAllLRAs();

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    Response getAllLRAs(@QueryParam(STATUS) String status);

    @GET
    @Path("/status/{" + LRA_ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_JSON)
    Response isActiveLRA(@PathParam(LRA_ID_PATH_PARAM) String lraId);

    @PUT
    @Path("/{" + LRA_ID_PATH_PARAM + "}/close")
    @Produces(MediaType.APPLICATION_JSON)
    Response closeLRA(@PathParam(LRA_ID_PATH_PARAM) String lraId);

    @PUT
    @Path("/{" + LRA_ID_PATH_PARAM + "}/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    Response cancelLRA(@PathParam(LRA_ID_PATH_PARAM) String lraId);

    @GET
    @Path("/{" + LRA_ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getLRA(@PathParam(LRA_ID_PATH_PARAM) String lraId);

    @PUT
    @Path("/{" + LRA_ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_JSON)
    Response joinLRA(@PathParam(LRA_ID_PATH_PARAM) String lraId,
                     @QueryParam(TIMELIMIT) @DefaultValue("0") Long timelimit,
                     @HeaderParam(LRAClient.LRA_HTTP_HEADER) String lraHeader,
                     @HeaderParam("Link") String linkHeader,
                     String compensatorData);

    @PUT
    @Path("/{" + LRA_ID_PATH_PARAM + "}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    Response leaveLRA(@PathParam(LRA_ID_PATH_PARAM) String lraId, String body);

    @PUT
    @Path("/{" + LRA_ID_PATH_PARAM + "}/timelimit")
    @Produces(MediaType.APPLICATION_JSON)
    Response renewTimeLimit(@PathParam(LRA_ID_PATH_PARAM) String lraId, @QueryParam(TIMELIMIT) @DefaultValue("0") Long timelimit);
}
