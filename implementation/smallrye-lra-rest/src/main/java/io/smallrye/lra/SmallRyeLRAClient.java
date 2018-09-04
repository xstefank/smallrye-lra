package io.smallrye.lra;

import io.smallrye.lra.utils.LRALogger;
import org.eclipse.microprofile.lra.annotation.CompensatorStatus;
import org.eclipse.microprofile.lra.client.GenericLRAException;
import org.eclipse.microprofile.lra.client.LRAClient;
import org.eclipse.microprofile.lra.client.LRAInfo;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.smallrye.lra.utils.LRAConstants.UTF_8;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

public class SmallRyeLRAClient implements LRAClient {
    
    // TODO create instance programatically from coordinatorURI
    @Inject
    private LRACoordinatorClient lraCoordinatorClient;
    
    private URI coordinatorURI;
    private URI recoveryCoordinatorURI;
    
    @Override
    public void setCoordinatorURI(URI uri) {
        this.coordinatorURI = uri;
    }

    @Override
    public void setRecoveryCoordinatorURI(URI uri) {
        this.recoveryCoordinatorURI = uri;
    }

    @Override
    public void close() {
        
    }

    @Override
    public URL startLRA(URL parentLRA, String clientID, Long timeout, TimeUnit unit) throws GenericLRAException {
        clientID = clientID == null ? "" : clientID;
        timeout = timeout == null ? 0L : timeout;

        if (timeout < 0) {
            throw new GenericLRAException(parentLRA, Response.Status.BAD_REQUEST.getStatusCode(),
                    "Invalid timeout value: " + timeout, null);
        }

        LRALogger.LOGGER.tracef("Starting LRA for client %s with parent %s", clientID, parentLRA);

        Response response;
        URL lra;
        
        try {
            String encodedParentLRA = parentLRA == null ? "" : URLEncoder.encode(parentLRA.toString(), UTF_8);

            response = lraCoordinatorClient.startLRA(clientID, unit.toMillis(timeout), encodedParentLRA);

            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new GenericLRAException(null, INTERNAL_SERVER_ERROR.getStatusCode(),
                        "LRA start returned an unexpected status code: " + response.getStatus(), null);
            }

            Object lraId = response.getHeaders().getFirst(LRA_HTTP_HEADER);

            if (lraId == null) {
                throw new GenericLRAException(null, INTERNAL_SERVER_ERROR.getStatusCode(),
                        "LRA creation is null", null);
            }

            lra = new URL(URLDecoder.decode(lraId.toString(), UTF_8));

            LRALogger.LOGGER.tracef("LRA %s started", lra);

        } catch (UnsupportedEncodingException | MalformedURLException e) {
            throw new GenericLRAException(null, INTERNAL_SERVER_ERROR.getStatusCode(), e.getMessage(), null);
        } catch (Exception e) {
            if (e.getCause() != null && ConnectException.class.equals(e.getCause().getClass())) {
                throw new GenericLRAException(null, SERVICE_UNAVAILABLE.getStatusCode(),
                        String.format("Cannot connect to the LRA coordinator: %s (%s)", coordinatorURI, e.getCause().getMessage()), e);
            }

            throw new GenericLRAException(null, SERVICE_UNAVAILABLE.getStatusCode(), e.getMessage(), e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
            
        return lra;
    }

    @Override
    public URL startLRA(String clientID, Long timeout, TimeUnit unit) throws GenericLRAException {
        return startLRA(null, clientID, timeout, unit);
    }

    @Override
    public String closeLRA(URL lraURL) throws GenericLRAException {
        LRALogger.LOGGER.tracef("closing LRA %s", lraURL);

        Response response;
        try {
            Response response = lraCoordinatorClient.closeLRA(getLRAId(lraURL.toString()));
            validateEndResponse(response, lraURL);

            String responseData = response.readEntity(String.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        
    }

    @Override
    public String cancelLRA(URL lraId) throws GenericLRAException {
        return null;
    }

    @Override
    public List<LRAInfo> getActiveLRAs() throws GenericLRAException {
        return null;
    }

    @Override
    public List<LRAInfo> getAllLRAs() throws GenericLRAException {
        return null;
    }

    @Override
    public List<LRAInfo> getRecoveringLRAs() throws GenericLRAException {
        return null;
    }

    @Override
    public Optional<CompensatorStatus> getStatus(URL lraId) throws GenericLRAException {
        return Optional.empty();
    }

    @Override
    public Boolean isActiveLRA(URL lraId) throws GenericLRAException {
        return null;
    }

    @Override
    public Boolean isCompensatedLRA(URL lraId) throws GenericLRAException {
        return null;
    }

    @Override
    public Boolean isCompletedLRA(URL lraId) throws GenericLRAException {
        return null;
    }

    @Override
    public String joinLRA(URL lraId, Long timelimit, URL compensateUrl, URL completeUrl, URL forgetUrl, URL leaveUrl, URL statusUrl, String compensatorData) throws GenericLRAException {
        return null;
    }

    @Override
    public String joinLRA(URL lraId, Class<?> resourceClass, URI baseUri, String compensatorData) throws GenericLRAException {
        return null;
    }

    @Override
    public URL updateCompensator(URL recoveryUrl, URL compensateUrl, URL completeUrl, URL forgetUrl, URL statusUrl, String compensatorData) throws GenericLRAException {
        return null;
    }

    @Override
    public void leaveLRA(URL lraId, String body) throws GenericLRAException {

    }

    @Override
    public void renewTimeLimit(URL lraId, long limit, TimeUnit unit) {

    }

    @Override
    public URL getCurrent() {
        return null;
    }

    @Override
    public void setCurrentLRA(URL lraId) {

    }

    private String getLRAId(String lraId) {
        return lraId == null ? null : lraId.replaceFirst(".*/([^/?]+).*", "$1");
    }

    private boolean isExpectedResponseStatus(Response response, Response.Status... expected) {
        return Arrays.stream(expected).anyMatch(status -> status.getStatusCode() == response.getStatus());
    }

    private void validateEndResponse(Response response, URL lra) {
        if (!isExpectedResponseStatus(response, Response.Status.OK, Response.Status.ACCEPTED, Response.Status.NOT_FOUND)) {
            throw new GenericLRAException(lra, INTERNAL_SERVER_ERROR.getStatusCode(),
                    "LRA finished with an unexpected status code: " + response.getStatus(), null);
        }

        if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new NotFoundException(lra.toExternalForm());
        }
    }
}
