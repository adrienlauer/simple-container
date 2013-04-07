package org.alauer.simplecontainer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class HttpServerIT {
    static HttpServer server;

    @ClassRule
    public static TemporaryFolder testFolder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws Exception {
        // TODO setup server
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stopAndWait();
    }

    @Test
    public void http_server_can_start_a_webapp() {

    }
}
