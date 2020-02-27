@rest
Feature: /etc/hosts management tests

  Scenario: Modify /etc/hosts in remote SSH connection
    # Check we can add entry
    Given I open a ssh connection to '${SSH}' with user 'root' and password 'stratio'
    And I run 'cat /etc/hosts' in the ssh connection and save the value in environment variable 'initialHostsFile'
    When I save host 'bdt.stratio.com' with ip '1.1.1.1' in /etc/hosts in the ssh connection
    And I run 'cat /etc/hosts' in the ssh connection
    Then the command output contains '1.1.1.1   bdt.stratio.com'
    # Check backup file and lock file have been created
    When I obtain java pid and save the value in environment variable 'javaPID'
    And I run 'ls -al /etc | grep hosts' in the ssh connection
    Then the command output contains 'hosts.bdt'
    And the command output contains 'hosts.lock.!{javaPID}'
    # Check backup has not been modified
    When I run 'cat /etc/hosts.bdt' in the ssh connection and save the value in environment variable 'hostsFileBackup'
    Then '!{initialHostsFile}' is '!{hostsFileBackup}'
    Then the command output does not contain '1.1.1.1   bdt.stratio.com'

  Scenario: Modify /etc/hosts in remote SSH connection when we have lock from previous modification
    # Check we can add entry
    Given I save host 'bdt2.stratio.com' with ip '2.2.2.2' in /etc/hosts in the ssh connection
    When I run 'cat /etc/hosts' in the ssh connection
    Then the command output contains '1.1.1.1   bdt.stratio.com'
    And the command output contains '2.2.2.2   bdt2.stratio.com'
    # Check backup file and lock file have been created
    When I obtain java pid and save the value in environment variable 'javaPID'
    And I run 'ls -al /etc | grep hosts' in the ssh connection
    Then the command output contains 'hosts.bdt'
    And the command output contains 'hosts.lock.!{javaPID}'
    # Check backup has not been modified
    When I run 'cat /etc/hosts.bdt' in the ssh connection
    Then '!{initialHostsFile}' is '!{hostsFileBackup}'

  Scenario: Restore /etc/hosts in remote SSH connection
    # Check /etc/hosts file is restored
    Given I restore /etc/hosts in the ssh connection
    When I run 'cat /etc/hosts' in the ssh connection and save the value in environment variable 'restoredHostsFile'
    And the command output does not contain '1.1.1.1   bdt.stratio.com'
    And the command output does not contain '2.2.2.2   bdt2.stratio.com'
    And '!{initialHostsFile}' is '!{restoredHostsFile}'
    # Check backup and lock file have been removed
    When I run 'ls -al /etc | grep hosts' in the ssh connection
    Then the command output does not contain 'hosts.bdt'
    And the command output does not contain 'hosts.lock.!{javaPID}'

#  Scenario: Modify /etc/hosts locally
#    # Check we can add entry
#    Given I run 'cat /etc/hosts' locally and save the value in environment variable 'initialHostsFile'
#    When I save host 'bdt.stratio.com' with ip '1.1.1.1' in /etc/hosts
#    And I run 'cat /etc/hosts' locally
#    Then the command output contains '1.1.1.1   bdt.stratio.com'
#    # Check backup file and lock file have been created
#    When I obtain java pid and save the value in environment variable 'javaPID'
#    And I run 'ls -al /etc | grep hosts' locally
#    Then the command output contains 'hosts.bdt'
#    And the command output contains 'hosts.lock.!{javaPID}'
#    # Check backup has not been modified
#    When I run 'cat /etc/hosts.bdt' locally and save the value in environment variable 'hostsFileBackup'
#    Then '!{initialHostsFile}' is '!{hostsFileBackup}'
#    Then the command output does not contain '1.1.1.1   bdt.stratio.com'
#
#  Scenario: Modify /etc/hosts locally when we have lock from previous modification
#    # Check we can add entry
#    Given I save host 'bdt2.stratio.com' with ip '2.2.2.2' in /etc/hosts
#    When I run 'cat /etc/hosts' locally
#    Then the command output contains '1.1.1.1   bdt.stratio.com'
#    And the command output contains '2.2.2.2   bdt2.stratio.com'
#    # Check backup file and lock file have been created
#    When I obtain java pid and save the value in environment variable 'javaPID'
#    And I run 'ls -al /etc | grep hosts' locally
#    Then the command output contains 'hosts.bdt'
#    And the command output contains 'hosts.lock.!{javaPID}'
#    # Check backup has not been modified
#    When I run 'cat /etc/hosts.bdt' locally
#    Then '!{initialHostsFile}' is '!{hostsFileBackup}'
#
#  Scenario: Restore /etc/hosts locally
#    # Check /etc/hosts file is restored
#    Given I restore /etc/hosts
#    When I run 'cat /etc/hosts' locally and save the value in environment variable 'restoredHostsFile'
#    And the command output does not contain '1.1.1.1   bdt.stratio.com'
#    And the command output does not contain '2.2.2.2   bdt2.stratio.com'
#    And '!{initialHostsFile}' is '!{restoredHostsFile}'
#    # Check backup and lock file have been removed
#    When I run 'ls -al /etc | grep hosts' locally
#    Then the command output does not contain 'hosts.bdt'
#    And the command output does not contain 'hosts.lock.!{javaPID}'