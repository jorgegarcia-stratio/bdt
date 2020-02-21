@rest
Feature: Testing

  Scenario: Some simple request
    And I set sso token using host 'bootstrap.nightlyforward.labs.stratio.com' with user 'admin' and password '1234' and tenant 'NONE'
    And I securely send requests to 'bootstrap.nightlyforward.labs.stratio.com:443'
    And I get info from global config and save it in environment variable 'etcd'
    And I get info from global config and save it in file 'etcd.json'