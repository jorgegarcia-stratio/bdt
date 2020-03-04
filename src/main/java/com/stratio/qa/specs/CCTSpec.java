/*
 * Copyright (C) 2014 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.qa.specs;

import com.ning.http.client.Response;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.java.en.Given;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.Future;

import static org.testng.Assert.fail;

/**
 * Generic Command Center Specs.
 *
 * @see <a href="CCTSpec-annotations.html">Command Center Steps</a>
 */
public class CCTSpec extends BaseGSpec {

    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public CCTSpec(CommonG spec) {
        this.commonspec = spec;
    }

    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I check in CCT that the service '(.+?)' with number of tasks '(\\d+)' is in '(healthy|unhealthy|running|stopped)' status$")
    public void checkServiceStatus(Integer timeout, Integer wait, String service, Integer numTasks, String expectedStatus) throws Exception {
        String endPoint = "/service/deploy-api/deployments/service?instanceName=" + service;
        boolean useMarathonServices = false;
        if (ThreadProperty.get("cct-marathon-services_id") != null) {
            endPoint = "/service/cct-marathon-services/v1/services/" + service;
            useMarathonServices = true;
        }

        boolean found = false;
        boolean isDeployed = false;
        for (int i = 0; (i <= timeout); i += wait) {
            try {
                Future<Response> response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null);
                commonspec.setResponse(endPoint, response.get());
                found = checkServiceStatusInResponse(expectedStatus, commonspec.getResponse().getResponse(), useMarathonServices);
                isDeployed = checkServiceDeployed(commonspec.getResponse().getResponse(), numTasks, useMarathonServices);

            } catch (Exception e) {
                commonspec.getLogger().debug("Error in request " + endPoint + " - " + e.toString());
            }
            if (found && isDeployed) {
                break;
            } else {
                if (!found) {
                    commonspec.getLogger().info(expectedStatus + " status not found or tasks  after " + i + " seconds for service " + service);
                } else if (!isDeployed) {
                    commonspec.getLogger().info("Tasks have not been deployed successfully after" + i + " seconds for service " + service);
                }
                if (i < timeout) {
                    Thread.sleep(wait * 1000);
                }
            }
        }
        if (!found) {
            fail(expectedStatus + " status not found after " + timeout + " seconds for service " + service);
        }
        if (!isDeployed) {
            fail("Tasks have not been deployed successfully after " + timeout + " seconds for service " + service);
        }
    }

    /**
     * Checks in Command Center response if the service has the expected status
     *
     * @param expectedStatus Expected status (healthy|unhealthy)
     * @param response Command center response
     * @param useMarathonServices True if cct-marathon-services is used in request, False if deploy-api is used in request
     * @return If service status has the expected status
     */
    private boolean checkServiceStatusInResponse(String expectedStatus, String response, boolean useMarathonServices) {
        if (useMarathonServices) {
            JSONObject cctJsonResponse = new JSONObject(response);
            String status = cctJsonResponse.getString("status");
            String healthiness = cctJsonResponse.getString("healthiness");
            switch (expectedStatus) {
                case "healthy":
                case "unhealthy":
                    return healthiness.equalsIgnoreCase(expectedStatus);
                case "running":     return status.equalsIgnoreCase("RUNNING");
                case "stopped":     return status.equalsIgnoreCase("SUSPENDED");
                default:
            }
        } else {
            switch (expectedStatus) {
                case "healthy":     return response.contains("\"healthy\":1");
                case "unhealthy":   return response.contains("\"healthy\":2");
                case "running":     return response.contains("\"status\":2");
                case "stopped":     return response.contains("\"status\":1");
                default:
            }
        }
        return false;
    }

    /**
     * Checks in Command Center response if the service tasks are deployed successfully
     *
     * @param response Command center response
     * @param numTasks Command center response
     * @param useMarathonServices True if cct-marathon-services is used in request, False if deploy-api is used in request
     * @return If service status has the expected status
     */
    private boolean checkServiceDeployed(String response, int numTasks, boolean useMarathonServices) {

        JSONObject deployment = new JSONObject(response);
        JSONArray tasks = (JSONArray) deployment.get("tasks");
        int numTasksRunning = 0;

        for (int i = 0; i < tasks.length(); i++) {
            if (useMarathonServices) {
                numTasksRunning = tasks.getJSONObject(i).get("status").equals("RUNNING") ? (numTasksRunning + 1) : numTasksRunning;
            } else if (!useMarathonServices) {
                numTasksRunning = tasks.getJSONObject(i).get("state").equals("TASK_RUNNING") ? (numTasksRunning + 1) : numTasksRunning;
            }
        }
        return numTasksRunning == numTasks ? true : false;
    }

}
