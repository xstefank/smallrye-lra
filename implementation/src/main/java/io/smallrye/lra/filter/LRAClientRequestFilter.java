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
package io.smallrye.lra.filter;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.lra.client.LRAClient;

import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

import static io.smallrye.lra.model.LRAConstants.COORDINATOR_URL_PROP;
import static io.smallrye.lra.model.LRAConstants.RECOVERY_URL_PROP;

public class LRAClientRequestFilter implements ClientRequestFilter {

    private String coordinatorURL;
    private String recoveryURL;

    public LRAClientRequestFilter() {
        Config config = ConfigProvider.getConfig();
        coordinatorURL = config.getValue(COORDINATOR_URL_PROP, String.class);
        recoveryURL = config.getValue(RECOVERY_URL_PROP, String.class);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {

        String uri = requestContext.getUri().toString();
        if (uri.startsWith(coordinatorURL) || uri.startsWith(recoveryURL) ||
                requestContext.getHeaders().containsKey(LRAClient.LRA_HTTP_HEADER)) {
            return;
        }

        // necessary as RestEasy currently requires ClientRequestFilter
        // to be registered manually
        LRAClient lraClient = CDI.current().select(LRAClient.class).get();
        if (lraClient.getCurrent() != null) {
            requestContext.getHeaders().putSingle(LRAClient.LRA_HTTP_HEADER, lraClient.getCurrent());
        }
    }
}
