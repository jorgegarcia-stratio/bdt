Feature: Get secrets from Vault

  Scenario: Upload secrets to Vault
    Given I run 'curl -k -H "X-Vault-Token:stratio" -H "Content-Type:application/json" -X POST -d "{\"type\":\"generic\",\"description\":\"testing\"}" http://${VAULT_URL:-vault.demo.stratio.com}:8200/v1/sys/mounts/userland' locally with exit status '0'
    And I run 'curl -k -H "X-Vault-Token:stratio" -H "Content-Type:application/json" -X POST -d "{\"type\":\"generic\",\"description\":\"testing\"}" http://${VAULT_URL:-vault.demo.stratio.com}:8200/v1/sys/mounts/ca-trust' locally with exit status '0'
    Then I run 'curl -k -H "X-Vault-Token:stratio" -H "Content-Type:application/json" -X POST -d "@src/test/resources/vault/test_cert.json" http://${VAULT_URL:-vault.demo.stratio.com}:8200/v1/userland/certificates/test' locally with exit status '0'
    And I run 'curl -k -H "X-Vault-Token:stratio" -H "Content-Type:application/json" -X POST -d "@src/test/resources/vault/test_ca.json" http://${VAULT_URL:-vault.demo.stratio.com}:8200/v1/ca-trust/certificates/ca' locally with exit status '0'
    And I run 'curl -k -H "X-Vault-Token:stratio" -H "Content-Type:application/json" -X POST -d "@src/test/resources/vault/test_keytab.json" http://${VAULT_URL:-vault.demo.stratio.com}:8200/v1/userland/kerberos/test' locally with exit status '0'
    And I run 'curl -k -H "X-Vault-Token:stratio" -H "Content-Type:application/json" -X POST -d "@src/test/resources/vault/test_password.json" http://${VAULT_URL:-vault.demo.stratio.com}:8200/v1/userland/passwords/test/testqa' locally with exit status '0'

  Scenario: Get public part from certificate
    Given I get certificate 'test-qa.qa' from path 'test' in PEM format
    When I save 'target/test-classes/test-qa.qa.pem' in variable 'pemFile'
    Then I run 'openssl x509 -in !{pemFile} -noout' locally with exit status '0'
    And I run 'openssl x509 -in !{pemFile} -noout -text | grep "Issuer: C=ES, O=Stratio, CN=Labs team CA"' locally
    And I run 'openssl x509 -in !{pemFile} -noout -text | grep "Subject: C=ES, O=Stratio, CN=test"' locally
    And I run 'openssl x509 -in !{pemFile} -noout -text | grep "DNS:test, DNS:test, DNS:test.qa"' locally
    Then I run 'rm -f !{pemFile}' locally

  Scenario: Get private part from certificate
    Given I get certificate 'test-qa.qa' from path 'test' in KEY format
    When I save 'target/test-classes/test-qa.qa.key' in variable 'keyFile'
    Then I run 'openssl rsa -in !{keyFile} -check | grep "RSA key ok"' locally
    Then I run 'rm -f !{keyFile}' locally

  Scenario: Get full certificate
    Given I get certificate 'test-qa.qa' from path 'test' in PEM/KEY format
    When I save 'target/test-classes/test-qa.qa.pem' in variable 'pemFile'
    And I save 'target/test-classes/test-qa.qa.key' in variable 'keyFile'
    Then I run 'openssl x509 -in !{pemFile} -noout' locally with exit status '0'
    And I run 'openssl x509 -in !{pemFile} -noout -text | grep "Issuer: C=ES, O=Stratio, CN=Labs team CA"' locally
    And I run 'openssl x509 -in !{pemFile} -noout -text | grep "Subject: C=ES, O=Stratio, CN=test"' locally
    And I run 'openssl x509 -in !{pemFile} -noout -text | grep "DNS:test, DNS:test, DNS:test.qa"' locally
    And I run 'openssl rsa -in !{keyFile} -check | grep "RSA key ok"' locally
    Then I run 'rm -f !{pemFile}' locally
    And I run 'rm -f !{keyFile}' locally

  Scenario: Get certificate in pkcs8 format
    Given I get certificate 'test-qa.qa' from path 'test' in PK8 format
    When I save 'target/test-classes/test-qa.qa.pk8' in variable 'pk8File'
    And I save 'target/test-classes/test-qa.qa.key' in variable 'keyFile'
    And I save 'target/test-classes/test-qa.qa_new.pem' in variable 'newPemFile'
    Then I run 'openssl pkcs8 -in !{pk8File} -inform DER -out !{newPemFile} -nocrypt' locally
    And I run 'openssl rsa -in !{newPemFile} -noout -modulus' locally and save the value in environment variable 'modulusNew'
    And I run 'openssl rsa -in !{keyFile} -noout -modulus' locally and save the value in environment variable 'modulusOrig'
    And '!{modulusNew}' matches '!{modulusNew}'
    Then I run 'rm -f !{pemFile}' locally
    And I run 'rm -f !{keyFile}' locally
    And I run 'rm -f !{pk8File}' locally
    And I run 'rm -f !{newPemFile}' locally

  Scenario: Get certificate in pkcs12 format
    Given I get certificate 'test-qa.qa' from path 'test' in P12 format and save the password in environment variable 'passP12'
    When I save 'target/test-classes/test-qa.qa.pem' in variable 'pemFile'
    And I save 'target/test-classes/test-qa.qa.key' in variable 'keyFile'
    And I save 'target/test-classes/test-qa.qa.p12' in variable 'p12File'
    Then I run 'openssl pkcs12 -in !{p12File} -passin pass:!{passP12} -passout pass:!{passP12} -clcerts -nokeys | grep "subject=/C=ES/O=Stratio/CN=test"' locally
    Then I run 'rm -f !{pemFile}' locally
    And I run 'rm -f !{keyFile}' locally
    And I run 'rm -f !{p12File}' locally

  Scenario: Get certificate in JKS
    Given I get certificate 'test-qa.qa' from path 'test' in JKS and save the password in environment variable 'passJKS'
    When I save 'target/test-classes/test-qa.qa.pem' in variable 'pemFile'
    And I save 'target/test-classes/test-qa.qa.key' in variable 'keyFile'
    And I save 'target/test-classes/test-qa.qa.p12' in variable 'p12File'
    And I save 'target/test-classes/test-qa.qa.jks' in variable 'jksFile'
    Then I run 'keytool -list -v -keystore !{jksFile} -storepass !{passJKS} | grep "CN=test, O=Stratio, C=ES"' locally
    Then I run 'rm -f !{pemFile}' locally
    And I run 'rm -f !{keyFile}' locally
    And I run 'rm -f !{p12File}' locally
    And I run 'rm -f !{jksFile}' locally

  Scenario: Get Truststore
    Given I get Trustore containing CA Bundle and save the password in environment variable 'passTrustore'
    When I save 'target/test-classes/ca.crt' in variable 'caFile'
    And I save 'target/test-classes/trustore.jks' in variable 'trustoreFile'
    Then I run 'keytool -list -v -keystore !{trustoreFile} -storepass !{passTrustore} | grep "CN=ca, O=Stratio, C=ES"' locally
    Then I run 'rm -f !{caFile}' locally
    And I run 'rm -f !{trustoreFile}' locally

  Scenario: Get CA bundle
    Given I get CA Bundle
    When I save 'target/test-classes/ca.crt' in variable 'caFile'
    Then I run 'openssl x509 -in !{caFile} -noout' locally with exit status '0'
    And I run 'openssl x509 -in !{caFile} -noout -text | grep "Issuer: C=ES, O=Stratio, CN=Labs team CA"' locally
    And I run 'openssl x509 -in !{caFile} -noout -text | grep "Subject: C=ES, O=Stratio, CN=ca"' locally
    And I run 'openssl x509 -in !{caFile} -noout -text | grep "DNS:ca, DNS:ca, DNS:ca.qa"' locally
    Then I run 'rm -f !{caFile}' locally

  Scenario: Get Keytab
    Given I get keytab 'test-qa.qa' from path 'test'
    When I save 'target/test-classes/test-qa.qa.keytab' in variable 'keytabFile'
    Then I run 'strings !{keytabFile} | grep -vE "^(>|<)"' locally and save the value in environment variable 'keytabContent'
    And I run 'echo !{keytabContent} | grep "DEMO.STRATIO.COM"' locally
    And I run 'echo !{keytabContent} | grep "test"' locally
    And I run 'echo !{keytabContent} | grep "test.qa"' locally
    Then I run 'rm -f !{keytabFile}' locally

  Scenario: Get Principal
    Given I get principal 'test-qa.qa' from path 'test' and save it in environment variable 'ppal'
    Then '!{ppal}' matches 'test/test.qa@DEMO.STRATIO.COM'

  Scenario: Get pass
    Given I get password from path 'test/testqa' and save it in environment variable 'pass'
    Then '!{pass}' matches '654321'

  Scenario: Get user
    Given I get user from path 'test/testqa' and save it in environment variable 'user'
    Then '!{user}' matches 'test_qa'

  Scenario: y to get secret with wrong path
    Given I get certificate 'secretnonexistant' from path 'test' in PEM format
    When I save 'target/test-classes/secretnonexistant.pem' in variable 'secretnonexistantFile'
    Then I run 'cat !{secretnonexistantFile} | grep BEGIN' locally with exit status '1'
