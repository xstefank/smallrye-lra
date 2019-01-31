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
package io.smallrye.lra.utils;

import java.net.URL;
import java.time.temporal.ChronoUnit;

public class Utils {

    public static String extractLraId(URL lra) {
        return lra != null ? lra.toExternalForm().replaceFirst(".*/([^/?]+).*", "$1") : null;
    }

    public static String getFormattedString(URL parentLRA, String clientID, Long timeout, ChronoUnit unit) {
        return String.format("[parentLRA = %s, clientID = %s, timeout = %s, unit = %s]",
                parentLRA, clientID, timeout, unit);
    }
}
