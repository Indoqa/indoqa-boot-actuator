/*
 * Licensed to the Indoqa Software Design und Beratung GmbH (Indoqa) under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Indoqa licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indoqa.boot.actuator.keycloak;

import static jakarta.servlet.http.HttpServletResponse.*;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import com.indoqa.boot.actuate.health.AbstractHealthIndicator;
import com.indoqa.boot.actuate.health.Health.Builder;
import com.indoqa.boot.actuate.health.Status;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

public class KeycloakHealthIndicator extends AbstractHealthIndicator {

    private static final Logger LOGGER = getLogger(KeycloakHealthIndicator.class);

    private final String authServerUrl;
    private final String realm;
    private final String loginData;
    private final int checkIntervalInSeconds;
    private volatile LastCheck lastCheck;

    @SuppressWarnings("unused")
    public KeycloakHealthIndicator(String authServerUrl, String realm, String loginData) {
        this(authServerUrl, realm, loginData, 0);
    }

    public KeycloakHealthIndicator(String authServerUrl, String realm, String loginData, int checkIntervalInSeconds) {
        super();
        this.authServerUrl = StringUtils.appendIfMissing(authServerUrl, "/");
        this.realm = realm;
        this.loginData = loginData;
        this.checkIntervalInSeconds = checkIntervalInSeconds;
    }

    private static void consumeResponse(HttpURLConnection connection) {
        if (connection == null) {
            return;
        }

        if (connection.getErrorStream() != null) {
            try (InputStream errorStream = connection.getErrorStream()) {
                IOUtils.skip(errorStream, Integer.MAX_VALUE);
            } catch (IOException e) {
                // ignore
            }
            return;
        }

        try (InputStream inputStream = connection.getInputStream()) {
            IOUtils.skip(inputStream, Integer.MAX_VALUE);
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        int responseCode = this.performKeycloakLogin();
        if (responseCode == SC_OK) {
            builder.status(Status.UP);
        } else if (responseCode >= SC_INTERNAL_SERVER_ERROR) {
            builder.status(Status.DOWN);
        } else {
            builder.status("INVALID_CONFIGURATION").withDetail("status-code", responseCode);
        }
    }

    private int performKeycloakLogin() throws Exception {
        if (this.lastCheck != null && this.lastCheck.isValid()) {
            int previousResponseCode = this.lastCheck.getResponseCode();
            LOGGER.debug("Use cached Keycloak health check result: responseCode={}", previousResponseCode);
            return previousResponseCode;
        }

        byte[] data = this.loginData.getBytes(StandardCharsets.UTF_8);
        URL url = new URL(this.authServerUrl + "realms/" + this.realm + "/protocol/openid-connect/token");

        LOGGER.debug("Perform Keycloak health check: url={}, data={}", url.toURI().toASCIIString(), this.loginData);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Length", Integer.toString(data.length));

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(data);
            }

            int responseCode = connection.getResponseCode();
            this.lastCheck = new LastCheck(responseCode);
            return responseCode;
        } finally {
            if (connection != null) {
                connection.disconnect();
                consumeResponse(connection);
            }
        }
    }

    private class LastCheck {

        private final Date validUntil;
        private final int responseCode;

        LastCheck(int responseCode) {
            this.responseCode = responseCode;
            this.validUntil = DateUtils.addSeconds(new Date(), KeycloakHealthIndicator.this.checkIntervalInSeconds);
        }

        int getResponseCode() {
            return this.responseCode;
        }

        boolean isValid() {
            return this.validUntil.after(new Date());
        }
    }
}
