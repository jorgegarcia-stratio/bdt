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
import com.stratio.qa.assertions.Assertions;
import com.stratio.qa.utils.ThreadProperty;
import cucumber.api.java.en.Given;
import io.cucumber.datatable.DataTable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
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

    /**
     * Checks in Command Center service status
     *
     * @param timeout
     * @param wait
     * @param service
     * @param numTasks
     * @param expectedStatus Expected status (healthy|unhealthy|running|stopped)
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I check in CCT that the service '(.+?)'( with number of tasks '(\\d+)')? is in '(healthy|unhealthy|running|stopped)' status$")
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
                if (numTasks != null) {
                    isDeployed = checkServiceDeployed(commonspec.getResponse().getResponse(), numTasks, useMarathonServices);
                }
            } catch (Exception e) {
                commonspec.getLogger().debug("Error in request " + endPoint + " - " + e.toString());
            }
            if ((found && (numTasks == null)) || (found && (numTasks != null) && isDeployed)) {
                break;
            } else {
                commonspec.getLogger().info(expectedStatus + " status not found after " + i + " seconds for service " + service);
                if (!found) {
                    commonspec.getLogger().info(expectedStatus + " status not found or tasks  after " + i + " seconds for service " + service);
                } else if (numTasks != null && !isDeployed) {
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
        if ((numTasks != null) && !isDeployed) {
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
            } else {
                numTasksRunning = tasks.getJSONObject(i).get("state").equals("TASK_RUNNING") ? (numTasksRunning + 1) : numTasksRunning;
            }
        }
        return numTasksRunning == numTasks;
    }

}

    /**
     * Get info from centralized configuration
     *
     * @param path
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get info from global config with path '(.*?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void infoFromGlobalConfig(String path, String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/central";
        String json = null;
        int timeout = 30;
        int wait = 5;
        Future<Response> response;

        String pathEndpoint = path != null ? "?path=" + path.replaceAll("/", "%2F") : "";
        endPoint = endPoint.concat(pathEndpoint);

        for (int i = 0; (i <= timeout); i += wait) {
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
            commonspec.setResponse("GET", response.get());

            if (commonspec.getResponse().getStatusCode() == 200) {
                i = timeout + 1;
            } else if (i == timeout) {
                throw new Exception("Request failed after " + timeout + " seconds");
            }
        }

        json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get global configuration from centralized configuration
     *
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get global configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getGlobalConfig(String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/central/config";
        String json = null;
        int timeout = 30;
        int wait = 5;
        Future<Response> response;

        for (int i = 0; (i <= timeout); i += wait) {
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
            commonspec.setResponse("GET", response.get());

            if (commonspec.getResponse().getStatusCode() == 200) {
                i = timeout + 1;
            } else if (i == timeout) {
                throw new Exception("Request failed after " + timeout + " seconds");
            }
        }

        json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get schema from global configuration
     *
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get schema from global configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getSchemaGlobalConfig(String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/central/schema";
        String json = null;
        int timeout = 30;
        int wait = 5;
        Future<Response> response;

        for (int i = 0; (i <= timeout); i += wait) {
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
            commonspec.setResponse("GET", response.get());

            if (commonspec.getResponse().getStatusCode() == 200) {
                i = timeout + 1;
            } else if (i == timeout) {
                throw new Exception("Request failed after " + timeout + " seconds");
            }
        }

        json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get info for network Id
     *
     * @param networkId
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get network '(.*?)'( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getNetworkById(String networkId, String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network/" + networkId;
        String json = null;
        int timeout = 30;
        int wait = 5;
        Future<Response> response;

        for (int i = 0; (i <= timeout); i += wait) {
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
            commonspec.setResponse("GET", response.get());

            if (commonspec.getResponse().getStatusCode() == 200) {
                i = timeout + 1;
            } else if (i == timeout) {
                throw new Exception("Request failed after " + timeout + " seconds");
            }
        }

        json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get info for all networks
     *
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get all networks( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getAllNetworks(String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network/all";
        String json = null;
        int timeout = 30;
        int wait = 5;
        Future<Response> response;

        for (int i = 0; (i <= timeout); i += wait) {
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
            commonspec.setResponse("GET", response.get());

            if (commonspec.getResponse().getStatusCode() == 200) {
                i = timeout + 1;
            } else if (i == timeout) {
                throw new Exception("Request failed after " + timeout + " seconds");
            }
        }

        json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get pool list
     *
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get pool list( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getPoolList(String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network/all";
        String json = null;
        int timeout = 30;
        int wait = 5;
        Future<Response> response;

        for (int i = 0; (i <= timeout); i += wait) {
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
            commonspec.setResponse("GET", response.get());

            if (commonspec.getResponse().getStatusCode() == 200) {
                i = timeout + 1;
            } else if (i == timeout) {
                throw new Exception("Request failed after " + timeout + " seconds");
            }
        }

        json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Get Mesos configuration
     *
     * @param path
     * @param envVar
     * @param fileName
     * @throws Exception
     */
    @Given("^I get path '(.*?)' from Mesos configuration( and save it in environment variable '(.*?)')?( and save it in file '(.*?)')?$")
    public void getMesosConfiguration(String path, String envVar, String fileName) throws Exception {

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/mesos";
        String pathEndpoint = path != null ? "?path=" + path.replaceAll("/", "%2F") : "";
        endPoint = endPoint.concat(pathEndpoint);

        String json = null;
        int timeout = 30;
        int wait = 5;
        Future<Response> response;

        for (int i = 0; (i <= timeout); i += wait) {
            response = commonspec.generateRequest("GET", false, null, null, endPoint, "", null, "");
            commonspec.setResponse("GET", response.get());

            if (commonspec.getResponse().getStatusCode() == 200) {
                i = timeout + 1;
            } else if (i == timeout) {
                throw new Exception("Request failed after " + timeout + " seconds");
            }
        }

        json = commonspec.getResponse().getResponse();

        if (envVar != null) {
            ThreadProperty.set(envVar, json);
        }

        if (fileName != null) {
            writeInFile(json, fileName);
        }
    }

    /**
     * Create/Update calico network
     *
     * @param timeout
     * @param wait
     * @param baseData      path to file containing the schema to be used
     * @param type          element to read from file (element should contain a json)
     * @param modifications DataTable containing the modifications to be done to the
     *                      base schema element. Syntax will be:
     *                      {@code
     *                      | <key path> | <type of modification> | <new value> |
     *                      }
     *                      where:
     *                      key path: path to the key to be modified
     *                      type of modification: DELETE|ADD|UPDATE
     *                      new value: in case of UPDATE or ADD, new value to be used
     *                      for example:
     *                      if the element read is {"key1": "value1", "key2": {"key3": "value3"}}
     *                      and we want to modify the value in "key3" with "new value3"
     *                      the modification will be:
     *                      | key2.key3 | UPDATE | "new value3" |
     *                      being the result of the modification: {"key1": "value1", "key2": {"key3": "new value3"}}
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I (create|update) calico network '(.+?)' so that the response( does not)? contains '(.+?)' based on '([^:]+?)'( as '(json|string|gov)')? with:$")
    public void calicoNetworkTimeout(Integer timeout, Integer wait, String operation, String networkId, String contains, String responseVal, String baseData, String type, DataTable modifications) throws Exception {

        // Retrieve data
        String retrievedData = commonspec.retrieveData(baseData, type);

        // Modify data
        commonspec.getLogger().debug("Modifying data {} as {}", retrievedData, type);
        String modifiedData = commonspec.modifyData(retrievedData, type, modifications);

        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network";
        String requestType = operation.equals("create") ? "PUT" : "POST";

        Boolean searchUntilContains;
        if (contains == null || contains.isEmpty()) {
            searchUntilContains = Boolean.TRUE;
        } else {
            searchUntilContains = Boolean.FALSE;
        }
        Boolean found = !searchUntilContains;
        AssertionError ex = null;

        Future<Response> response;

        Pattern pattern = CommonG.matchesOrContains(responseVal);

        for (int i = 0; (i <= timeout); i += wait) {
            if (found && searchUntilContains) {
                break;
            }
            try {
                commonspec.getLogger().debug("Generating request {} to {} with data {} as {}", requestType, endPoint, modifiedData, type);
                response = commonspec.generateRequest(requestType, false, null, null, endPoint, modifiedData, type);
                commonspec.getLogger().debug("Saving response");
                commonspec.setResponse(requestType, response.get());
                commonspec.getLogger().debug("Checking response value");

                if (searchUntilContains) {
                    assertThat(commonspec.getResponse().getResponse()).containsPattern(pattern);
                    found = true;
                    timeout = i;
                } else {
                    assertThat(commonspec.getResponse().getResponse()).doesNotContain(responseVal);
                    found = false;
                    timeout = i;
                }
            } catch (AssertionError | Exception e) {
                if (!found) {
                    commonspec.getLogger().info("Response value not found after " + i + " seconds");
                } else {
                    commonspec.getLogger().info("Response value found after " + i + " seconds");
                }
                Thread.sleep(wait * 1000);
                if (e instanceof AssertionError) {
                    ex = (AssertionError) e;
                }
            }
            if (!found && !searchUntilContains) {
                break;
            }
        }
        if ((!found && searchUntilContains) || (found && !searchUntilContains)) {
            throw (ex);
        }
        if (searchUntilContains) {
            commonspec.getLogger().info("Success! Response value found after " + timeout + " seconds");
        } else {
            commonspec.getLogger().info("Success! Response value not found after " + timeout + " seconds");
        }
    }

    /**
     * Delete calico network
     *
     * @param timeout
     * @param wait
     * @param networkId
     * @throws Exception
     */
    @Given("^in less than '(\\d+)' seconds, checking each '(\\d+)' seconds, I delete calico network '(.+?)' so that the response( does not)? contains '(.+?)'$")
    public void deleteCalicoNetworkTimeout(Integer timeout, Integer wait, String networkId, String contains, String responseVal) throws Exception {

        if (networkId.equals("logs") || networkId.equals("stratio") || networkId.equals("metrics") || networkId.equals("stratio-shared")) {
            throw new Exception("It is not possible deleting networks stratio, metrics, logs or stratio-shared");
        }
        String endPoint = "/service/" + ThreadProperty.get("configuration_api_id") + "/network/" + networkId;
        String requestType = "DELETE";

        Boolean searchUntilContains;
        if (contains == null || contains.isEmpty()) {
            searchUntilContains = Boolean.TRUE;
        } else {
            searchUntilContains = Boolean.FALSE;
        }
        Boolean found = !searchUntilContains;
        AssertionError ex = null;

        Future<Response> response;

        Pattern pattern = CommonG.matchesOrContains(responseVal);

        for (int i = 0; (i <= timeout); i += wait) {
            if (found && searchUntilContains) {
                break;
            }
            try {
                commonspec.getLogger().debug("Generating request {} to {} with data {} as {}", requestType, endPoint, null, null);
                response = commonspec.generateRequest(requestType, false, null, null, endPoint, null, null);
                commonspec.getLogger().debug("Saving response");
                commonspec.setResponse(requestType, response.get());
                commonspec.getLogger().debug("Checking response value");

                if (searchUntilContains) {
                    assertThat(commonspec.getResponse().getResponse()).containsPattern(pattern);
                    found = true;
                    timeout = i;
                } else {
                    assertThat(commonspec.getResponse().getResponse()).doesNotContain(responseVal);
                    found = false;
                    timeout = i;
                }
            } catch (AssertionError | Exception e) {
                if (!found) {
                    commonspec.getLogger().info("Response value not found after " + i + " seconds");
                } else {
                    commonspec.getLogger().info("Response value found after " + i + " seconds");
                }
                Thread.sleep(wait * 1000);
                if (e instanceof AssertionError) {
                    ex = (AssertionError) e;
                }
            }
            if (!found && !searchUntilContains) {
                break;
            }
        }
        if ((!found && searchUntilContains) || (found && !searchUntilContains)) {
            throw (ex);
        }
        if (searchUntilContains) {
            commonspec.getLogger().info("Success! Response value found after " + timeout + " seconds");
        } else {
            commonspec.getLogger().info("Success! Response value not found after " + timeout + " seconds");
        }
    }

    private void writeInFile(String json, String fileName) throws Exception {

        // Create file (temporary) and set path to be accessible within test
        File tempDirectory = new File(System.getProperty("user.dir") + "/target/test-classes/");
        String absolutePathFile = tempDirectory.getAbsolutePath() + "/" + fileName;
        commonspec.getLogger().debug("Creating file {} in 'target/test-classes'", absolutePathFile);
        // Note that this Writer will delete the file if it exists
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(absolutePathFile), StandardCharsets.UTF_8));
        try {
            out.write(json);
        } catch (Exception e) {
            commonspec.getLogger().error("Custom file {} hasn't been created:\n{}", absolutePathFile, e.toString());
        } finally {
            out.close();
        }

        Assertions.assertThat(new File(absolutePathFile).isFile());
    }

}
