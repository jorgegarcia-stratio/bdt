# Changelog

Only listing significant user-visible, not internal code cleanups and minor bug fixes.

## 0.14.0 (upcoming)

* [GS-1830] Added scim type to HTTP requests
* [QATM-3165] Add gosecmanagement path to CucumberRunner
* [QATM-3177] Mejorar la obtención de secretos de Vault (kms_utils) en la BDT
* [QATM-3178] Mejora gestión /etc/hosts
* [QATM-3187] Nueva annotation para obtener info de bootstrap automáticamente
* [QATM-3183] Use Marathon API to obtain service versions

## 0.13.0 (February 12, 2020)

* [QATM-3140] Let step convert json schema save content to file and/or variable
* [QATM-3125] Fix error in Rocket SSO login
* Set dcosAuthCookie as local variable in step "I set sso token
* [QATM-3107] Keep SSH connections
* [GS-1730] Added step to include a group to a profile including the previous configuration
* [FRAM-4753] Change constraint private function
* [QATM-2770] Fix 'in less than xxx' steps to avoid timeout / unknownhost exceptions
* [QATM-3084] Empty default value for variables
* [FRAM-4753] Change for constraint private function 
* [FRAM-4720] Inclusion constraints scheduler
* [FRAM-4275] Add roles hdfs service
* [QATM-3073] Fix password creation without user
* [QATM-3073] New steps to generate keytabs, certificates and passwords 
* [CROSSDATA-2345] Modify Gosec policies methods to obtain ID when Gosec < 0.17
* [QATM-3071] Fix how to get Vault IP
* [GS-1714] Step to get json policy and save on env variable or policy
* [GS-1714] Changed the endpoint to get all policy details
* [GS-1707] Get REALM from bootstrap and save in variable EOS_REALM
* [LDG-1225] Added not-empty check cell in csv matcher
* [QATM-3028] Allow to choose user in governance header
* [QATM-3054] Performance improvements in obtain info from bootstrap step
* [CROSSDATA-2289] Allow remote execution in parallel script
* [CROSSDATA-2289] New steps to execute command / copy files in all nodes
* [QATM-3036] Use CA trusted for LDAP connection
* [QATM-3030] Add two more variables to basic information
* Set governance cookie
* [QATM-3006] Add X-RolesID header for governanceAPI 1.2
* [GS-1538] Allow response 204 deleting gosec resources
* [QATM-2975] Null support added in query result and new step to check if table is contained in query result
* [CROSSDATA-2232] Steps added to connect to Crossdata server through JDBC
* [QATM-2945] Include progloop tag
* [QATM-2932] Cambio version jdbc e introducción valor encriptacion
* [ROCK-612] Feature/no host verifier
* [QATM-2925] Fix NPE when we try to obtain a browser snapshot
* [QATM-1993] Upgrade Cucumber to 4.4.0
* [SECTY-1482] Added step to get profileId from profileName

## 0.12.0 (October 03, 2019)

* [QATM-2490] Save service response in environment variable without File parameters
* [QATM-2494] Fix Permission denied error when we try to copy a file and destination file name is not defined 
* [QATM-1896] Allow standalone-chrome usage without grid
* [QATM-2529] Fix code error in gtm_proxy step
* [QATM-2489] Added PATCH type to rest requests
* [QATM-2531] add new comparison options
* [QATM-2533] Allow local variables in runOnEnv and skipOnEnv tags
* [QATM-2541] new step for local command execution polling
* [QATM-2534] Fix Jira integration using @ignore @tillfixed(XXX)
* [QATM-2299] Add screenshot in cucumber report when selenium step fails
* [QATM-2299] Fix chromeFullScreenCapture method
* [QATM-2556] return multiline result in local execution as such
* [QATM-2555] Add ignite, k8s and etcd in check constraints step
* [QATM-2558] Add arangodb in check constraints step
* [QATM-2628] small fixes in local command and dcos step
* [QATM-2636] Fix constraints
* [QATM-2658] New step to obtain policyID
* [QATM-2701] Modify create/delete policy steps to obtain policyID
* [SPK-548] Enhanced SSH connection. Avoiding other methods than pem/password and setting Strict Host to false
* [QATM-2734] include X-UserID for gov request
* [SECTY-1314] Added step to create tenant if it does not exist
* [SECTY-1440] BDT refactor for gosec 1.1.1 new paths and ids
* [QATM-2786] New steps to set default values in tenant and gosec variables
* [QATM-2786] New step to include an user or group in existing tenant
* [QATM-2789] Support doesn't contain in SSH Command output
* [QATM-2792] Add steps for LDAP
* [QATM-2793] Use other SSH port rather than default 22
* [QATM-2367] New clear function with keys Control and Delete
* [QATM-2817] Add MarathonLB folder to classpath
* [QATM-2833] Parametrize TenantID Governance
* [SECTY-1482] Create step to get id from tag policies
* [QATM-2895] Modify step to retrieve info from bootstrap

## 0.11.0 (June 11, 2019)

* [QATM-2484] Fix copyTo and copyFrom methods for dirs/files handling
* [QATM-1986] Upgrade Cucumber to 3.0.2
* [QATM-2349] Add exception info when one step fails and hide next steps of scenario
* [QATM-2344] new steps to gather info from bootstrap
* [QATM-2303] refactor of steps due to new cucumber version
* [QATM-2353] Fix infinite loop in some undefined steps
* [QATM-2355] Do not flat template file when it's not a JSON
* [QATM-2352] New catch added in Selenium click step
* [QATM-2359] Fix error in sendRequestTimeout method
* [QATM-2364] Add EOS_ACCESS_POINT in returned info from bootstrap
* [QATM-2363] Add step typing textx if the element exists using selenium
* [QATM-2365] Add retrieve value js
* [QATM-2488] Add tag to avoid execution when environment variables not defined
* [QATM-2541] New step local command execution polling

## 0.10.0 (May 23, 2019)

* [QATM-2137] Fix array json governance
* [QATM-2139] add new tests for modifyData with arrays
* [QATM-2181] new steps for sftp module
* [QATM-2187] Add ignite specs path to cucumber glue
* [LDG-123] Refactor resultMustBeCSV and added test
* [QATM-1901] Add charset=UTF-8 in content-type headers
* [QATM-2178] New step to check if a role of a service complies the established constraints
* [LDG-240] Improve CSV file matcher for random values
* [QATM-2118] New proxy steps added
* [SPARTA-2328] New selenium features (for Intell and Sparta)
* [QATM-2273] Add screenshot to cucumber-report
* [QATM-2302] Fix NPE when module has custom specs
* [QATM-2303] Create user,policy and groups if they don exist previously
* [QATM-2333] Ignore pre-release suffix in versions sent as parameter

## 0.9.0 (February 25, 2019)

* [QATM-1899] Upgrade Cucumber to 2.4.0
* [QATM-1981] Refactor specs and tests
* [QATM-2049] Fix for incorrect step after specs refactor
* [QATM-1992] Fix include aspect error when we have tags in the next scenario after scenario included
* [QATM-2054] add new headers in case of governance request
* [QATM-2058] new step to check REST request does not contain text and fixes for gov requests
* [QATM-2071] fix step to check services distribution in datacenters
* [QATM-1974] RunOnEnv / SkipOnEnv improvements
* [QATM-2067] @always tag allows execute a test even if an important scenario failed previously
* [QATM-2099] Try/catch added in RunOnTagAspect

## 0.8.0 (February 07, 2019)

* [LDG-96] New kafka steps
* [QASAI-33] New step to check if a cookie has been saved correctly
* [QATM-967] Updated Selenium version to 3.9.1
* [PIT-68] Updated commons-lang3 version to 3.7
* [QATM-938] Add capability for getting raw HTML code
* [QATM-1201] Added method to convert json to yaml
* [QATM-1362] allow boolean expressions in runOnEnv and skipOnEnv tags
* [QATM-1362] modify regular expression for boolean conditions in runOnEnv and skipOnEnv
* [QATM-1181] Add glue for PGBouncer
* [QATM-1414] Add changes for search by text in selenium
* [QATM-1411] fix dcos-cli problems with ssh connection
* [QATM-324] Step to retrieve secrets from Vault
* [QATM-1267] Capability to operate with headers on selenium
* [QATM-1267] Changes to include keyboard generated events and delete non editable fields
* [QATM-1421] Changes to include parameter type Long in json files
* [QATM-1412] Step created to connect postgres with tls
* [QATM-1431] Add sql steps to bdt
* [QATM-1431] Refactor close database method name
* [QATM-1264] Add method to obtain dcos-cookie to bdt
* [QATM-1437] fix method to add a new label to framework
* [QATM-1464] set tenant to retrieve auth_token 
* [QATM-1464] fix error in condition to add tenant
* [QATM-1497] Changes to adapt step for different cases
* [QATM-1481] add kibana framework to glue file
* [QATM-1638] Include BODY in DELETE method
* [QATM-1644] fix to add label in DC/OS 1.11
* [QATM-1643] Change regex to match for loop and multiloop
* [QATM-1646] Add Postgres Database resource
* [QATM-1593] new steps for command center integration
* [QATM-1689] Replace securely by security type in DB connection
* [QATM-1593] new steps for command center integration
* [QASAI-33] New step to check if a cookie has benn saved correctly
* [QASAI-4] Step for Double click for selenium
* [QATM-1827] Add < and > in RunOnEnv or SkipOnEnv aspects
* [LDG-96] Added new kafka steps
* [QATM-1678] Change on replacement aspect
* [QATM-1842] Fix error changing result --> result in checkParams having logical operators
* [QASAI-34] Fix JUnitReport
* [QATM-1873] Fix Selenium tests
* [QATM-1871] New command center steps 
* [QATM-1870] add CucumberRunner for CommandCenter
* [QATM-1559] Connect to Cassandra with ssl
* [QATM-1878] Fix error in report when step is passed and it has text like a variable
* [QATM-1933] fix in function to convert jsonschema to json for CCT
* [QATM-1934] fix when converting jsonschema to json handling strings
* [QATM-1935] Allow empty string as default value in variables
* [QATM-1948] retrieve DCOS_AUTH_COOKIE and save in thread variable
* [QATM-1968] Fix to avoid false errors when we check a query response
* [QATM-1950] Create step to check resources after uninstall
* [QATM-1976] Print response if status code returned is different that expected
* [QATM-1977] New step to create metabase cookie
* [QATM-1979] New step to obtain metabase session id and generate cookie

## 0.7.0 (April 05, 2018)

* [CROSSDATA-1550] Add @multiloop tag
* [QATM-1061] Add step to send requests to endpoint with timeout and datatable

## 0.6.0 (February 22, 2018)

* [QATM-236] Quit after tagged scenario fails
* [QATM-70] New background tag
* [QA-189] Removed extra dot in 'service response status' step. Old step removed.
* [QA-152] New aspect merging 'include' and 'loop'. Old aspects removed.
* [QATM-74] New step to store text in a webElement in environment variable.
* [QATM-73] New step to read file, modify according to parameters and store in environment variable.
* [AR-732] Add Command Center - Configuration API Glue

## 0.5.1 (July 05, 2017)

* [QATM-78] Fix public releasing in maven central

## 0.5.0 (June 12, 2017)

* [QA-342] New cucumber tag @loop to multiple scenario executions

## 0.4.0 (March 06, 2017)

* [QA-272] better classes packaging
* [QA-298] Apache2 license. Step definitions redefined

## 0.3.0 (January 26, 2017)

* CukesGHooks will invoke the logger with each step

## 0.2.0 (June 2016)

* Ignored scenarios will fail if ignore cause was an already done jira ticket
* No more a submodule project
* Added new aspect to force the browser name and version in tests development ,using FORCE_BROWSER (even availableUniqueBrowsers factory is defined).

  Eg: mvn -U verify -DSELENIUM_GRID=jenkins.stratio.com:4444 **-DFORCE_BROWSER=chrome_33**

