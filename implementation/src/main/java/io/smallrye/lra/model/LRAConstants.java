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

public final class LRAConstants {

    private LRAConstants() {}

    public static final long DEFAULT_TIMELIMIT = 0L;

    public static final String CLIENT_ID = "ClientId";
    public static final String PARENT_LRA = "ParentLRA";
    public static final String TIMELIMIT = "TimeLimit";
    public static final String LRA_ID_PATH_PARAM = "LraId";
    public static final String RECOVERY_ID_PATH_PARAM = "RecoveryId";
    public static final String STATUS = "Status";
    public static final String PARTICIPANT_ID_PATH_PARAM = "participantId";

    // config
    public static final String COORDINATOR_URL_PROP = "lra.coordinator.url";
    public static final String RECOVERY_URL_PROP = "lra.recovery.url";

}
