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

import javax.ws.rs.core.Response;
import java.net.URL;

class LRAContextBuilder {

    private URL lraId;
    private boolean newlyStarted;
    private URL suspendedLRA;
    private Response.Status[] cancelOn;
    private Response.Status.Family[] cancelOnFamily;

    LRAContextBuilder lraId(URL lraId) {
        this.lraId = lraId;
        return this;
    }

    LRAContextBuilder newlyStarted(boolean newlyStarted) {
        this.newlyStarted = newlyStarted;
        return this;
    }

    LRAContextBuilder suspend(URL lraId) {
        this.suspendedLRA = lraId;
        return this;
    }

    LRAContextBuilder cancelOn(Response.Status[] cancelOn) {
        this.cancelOn = cancelOn;
        return this;
    }

    LRAContextBuilder cancelOnFamily(Response.Status.Family[] cancelOnFamily) {
        this.cancelOnFamily = cancelOnFamily;
        return this;
    }
    
    LRAContext build() {
        return new LRAContext(lraId, newlyStarted, suspendedLRA, cancelOn, cancelOnFamily);
    }
}
