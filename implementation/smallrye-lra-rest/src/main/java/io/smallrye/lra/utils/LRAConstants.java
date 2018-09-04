package io.smallrye.lra.utils;

public interface LRAConstants {

    String COORDINATOR = "/lra-coordinator";

    String START = "/start";
    String CLOSE = "/{LraId}/close";
    String CANCEL = "/{LraId}/cancel";

    String CLIENT_ID_PARAM = "ClientID";
    String TIMELIMIT_PARAM = "TimeLimit";
    String PARENT_LRA_PARAM = "ParentLRA";

    String UTF_8 = "UTF-8";
}
