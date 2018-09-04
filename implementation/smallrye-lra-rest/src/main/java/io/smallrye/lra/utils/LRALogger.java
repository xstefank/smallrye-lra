package io.smallrye.lra.utils;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

import javax.ws.rs.core.Response;

import static org.jboss.logging.Logger.Level.ERROR;

@MessageLogger(projectCode = "LRA")
public interface LRALogger {
    
    Logger LOGGER = Logger.getLogger("io.smallrye.lra");

    @LogMessage(level = ERROR)
    @Message(id = 25003, value = "LRA is null on creation, coordinator response '%s'")
    void nullLraOnCreation(Response response);
}
