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
package com.indoqa.boot.actuator.solr;

import static com.indoqa.boot.actuate.health.Status.*;

import com.indoqa.boot.actuate.health.AbstractHealthIndicator;
import com.indoqa.boot.actuate.health.Health.Builder;
import com.indoqa.boot.actuate.health.Status;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.response.SolrPingResponse;

public class SolrHealthIndicator extends AbstractHealthIndicator {

    private final SolrClient solrClient;

    SolrHealthIndicator(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        SolrPingResponse pingResponse = this.solrClient.ping();
        int statusCode = pingResponse.getStatus();
        Status status = (statusCode == 0 ? UP : DOWN);
        builder.status(status).withDetail("status", statusCode);
    }
}
