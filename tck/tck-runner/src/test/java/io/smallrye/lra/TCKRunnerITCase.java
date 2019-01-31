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
package io.smallrye.lra;

import org.junit.Test;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.spi.JsonProvider;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.StringReader;

import static java.util.Collections.singletonMap;

public class TCKRunnerITCase {

    private static final long DESTROY_TIMEOUT = 5000;

    @Test
    public void runTCK() throws Exception {
        ProcessBuilder coordinatorPb = new ProcessBuilder("java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9001", "-jar", "lra-coordinator-swarm.jar");
        coordinatorPb.inheritIO();
        coordinatorPb.directory(new File("."));
        coordinatorPb.redirectOutput(new File("target/coodinator-output.txt"));
        System.out.println("Starting LRA coordinator...");
        Process coordinatorProcess = coordinatorPb.start();

        ProcessBuilder TCKClientPb = new ProcessBuilder("java",
                "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=9000",
                "-jar",
                "tck-client-thorntail.jar", "-Dswarm.port.offset=100",
                "-Dlra.coordinator.url=http://localhost:8080",
                "-Dlra.recovery.url=http://localhost:8080",
                "-Dservice.http.port=8180",
                "-Dlra.http.port=8080");
        TCKClientPb.inheritIO();
        TCKClientPb.directory(new File("../tck-client/target"));
        System.out.println("Starting LRA TCK client...");
        Process TCKClientProcess = TCKClientPb.start();

        waitForTCK();

        System.out.println("Executing TCK run...");
        WebTarget target = ClientBuilder.newClient().target("http://localhost:8180/tck/all");
        Response response = target.request().put(null);

        destroyProcess(coordinatorProcess);
        destroyProcess(TCKClientProcess);

        System.out.printf("%n------- TEST RESULTS -------%n");
        final JsonProvider provider = JsonProvider.provider();
        try (final JsonReader reader = Json.createReader(new StringReader(response.readEntity(String.class)))) {
            try (final JsonWriter writer = provider.createWriterFactory(singletonMap("javax.json.stream.JsonGenerator.prettyPrinting", "true")).createWriter(System.out)) {
                writer.write(reader.read());
            }
        }
    }

    private void waitForTCK() throws InterruptedException {
        Thread.sleep(5000);

        Client client = ClientBuilder.newClient();
        WebTarget coordinatorTarget = client.target("http://localhost:8080/lra-coordinator");
        WebTarget tckTarget = client.target("http://localhost:8180/ping");

        boolean coordinatorUp = false;
        boolean tckClientUp = false;
        Response response = null;

        while (!coordinatorUp || !tckClientUp) {
            try {
                response = coordinatorTarget.request().get();
                if (response.getStatus() == 200) coordinatorUp = true;
            } catch (ProcessingException e) {
                // ok
            }

            try {
                response = tckTarget.request().get();
                if (response.getStatus() == 200) tckClientUp = true;
            } catch (ProcessingException e) {
                // ok
            }

            Thread.sleep(1000);
        }

        response.close();
    }

    private void destroyProcess(Process process) throws InterruptedException {
        process.destroy();
        Thread.sleep(DESTROY_TIMEOUT);
        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }
}
