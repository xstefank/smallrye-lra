package io.smallrye.lra;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.smallrye.lra.utils.LRAConstants.CANCEL;
import static io.smallrye.lra.utils.LRAConstants.CLIENT_ID_PARAM;
import static io.smallrye.lra.utils.LRAConstants.CLOSE;
import static io.smallrye.lra.utils.LRAConstants.COORDINATOR;
import static io.smallrye.lra.utils.LRAConstants.PARENT_LRA_PARAM;
import static io.smallrye.lra.utils.LRAConstants.START;
import static io.smallrye.lra.utils.LRAConstants.TIMELIMIT_PARAM;

@Path(COORDINATOR)
public interface LRACoordinatorClient {
    
    @POST
    @Path(START)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    Response startLRA(
            @QueryParam(CLIENT_ID_PARAM) @DefaultValue("") String clientID,
            @QueryParam(TIMELIMIT_PARAM) @DefaultValue("0") Long timelimit,
            @QueryParam(PARENT_LRA_PARAM) @DefaultValue("") String parentLRA);
    
    @PUT
    @Path(CLOSE)
    @Produces(MediaType.APPLICATION_JSON)
    Response closeLRA(@PathParam("LraId") String lraId) throws NotFoundException;

    @PUT
    @Path(CANCEL)
    @Produces(MediaType.APPLICATION_JSON)
    Response cancelLRA(@PathParam("LraId") String lraId) throws NotFoundException;
    
    
    
}
