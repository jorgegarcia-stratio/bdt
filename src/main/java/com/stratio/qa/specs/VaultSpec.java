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

import cucumber.api.java.en.Given;


/**
 * Generic Vault Specs.
 *
 * @see <a href="VaultSpec-annotations.html">Vault Steps &amp; Matching Regex</a>
 */
public class VaultSpec extends BaseGSpec {

    /**
     * Generic constructor.
     *
     * @param spec object
     */
    public VaultSpec(CommonG spec) {
        this.commonspec = spec;

    }

    /**
     * Get both PEM and KEY from specified certificate. The ouput files are:
     *
     *    target/test-classes/<value>.pem
     *    target/test-classes/<value>.key
     *
     * @param value specific certificate's entry
     * @param path certificate's path in Vault
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' from path '(.+?)' in PEM/KEY format$")
    public void getCertificate(String value, String path) throws Exception {
        commonspec.getVaultUtils().getPEMKEYCertificate(path, value);
    }

    /**
     * Get PEM from specified certificate. The ouput file is:
     *
     *    target/test-classes/<value>.pem
     *
     * @param value specific certificate's entry
     * @param path certificate's path in Vault
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' from path '(.+?)' in PEM format$")
    public void getPubCertificate(String value, String path) throws Exception {
        commonspec.getVaultUtils().getPEMCertificate(path, value);
    }

    /**
     * Get KEY from specified certificate. The ouput file is:
     *
     *    target/test-classes/<value>.key
     *
     * @param value specific certificate's entry
     * @param path certificate's path in Vault
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' from path '(.+?)' in KEY format$")
    public void getKeyCertificate(String value, String path) throws Exception {
        commonspec.getVaultUtils().getKEYCertificate(path, value);
    }

    /**
     * Get CA Bundle from cluster. The ouput file is:
     *
     *    target/test-classes/ca.crt
     *
     * @throws Exception
     */
    @Given("^I get CA Bundle$")
    public void getCA() throws Exception {
        commonspec.getVaultUtils().getCABundle();
    }

    /**
     * Get P12 from specified certificate. The ouput file is:
     *
     *    target/test-classes/<value>.p12
     *
     * @param value specific certificate's entry
     * @param path certificate's path in Vault
     * @param envVar environment variable to save the P12 password
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' from path '(.+?)' in P12 format and save the password in environment variable '(.+?)'$")
    public void getP12Certificate(String value, String path, String envVar) throws Exception {
        commonspec.getVaultUtils().getPKCS12Certificate(path, value, envVar);
    }

    /**
     * Get JKS from specified certificate. The ouput file is:
     *
     *    target/test-classes/<value>.jks
     *
     * @param value specific certificate's entry
     * @param path certificate's path in Vault
     * @param envVar environment variable to save the P12 password
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' from path '(.+?)' in JKS and save the password in environment variable '(.+?)'$")
    public void getJKSCertificate(String value, String path, String envVar) throws Exception {
        commonspec.getVaultUtils().getKeystore(path, value, envVar);
    }

    /**
     * Get PK8 from specified certificate. The ouput file is:
     *
     *    target/test-classes/<value>.pk8
     *
     * @param value specific certificate's entry
     * @param path certificate's path in Vault
     * @throws Exception
     */
    @Given("^I get certificate '(.+?)' from path '(.+?)' in PK8 format$")
    public void getPK8Certificate(String value, String path) throws Exception {
        commonspec.getVaultUtils().getPKCS8Certificate(path, value);
    }

    /**
     * Get Truststore with the cluster CA Bundle. The ouput file is:
     *
     *    target/test-classes/truststore.jks
     *
     * @throws Exception
     */
    @Given("^I get Truststore containing CA Bundle and save the password in environment variable '(.+?)'$")
    public void getTruststoreWithCABundle(String envVar) throws Exception {
        commonspec.getVaultUtils().getTruststoreCABundle(envVar);
    }

    /**
     * Get Keytab from specified certificate. The ouput file is:
     *
     *    target/test-classes/<value>.keytab
     *
     * @param value specific keytab's entry
     * @param path Keytab's path in Vault
     * @throws Exception
     */
    @Given("^I get keytab '(.+?)' from path '(.+?)'$")
    public void getKeytab(String value, String path) throws Exception {
        commonspec.getVaultUtils().getKeytabKrb(path, value);
    }

    /**
     * Get 'principal' from specified Keytab. The ouput is the 'principal' saved in environmental variable.
     *
     * @param value specific principal's entry
     * @param path Keytab's path in Vault
     * @param envVar environment variable to save the principal
     * @throws Exception
     */
    @Given("^I get principal '(.+?)' from path '(.+?)' and save it in environment variable '(.+?)'$")
    public void getPrincipal(String value, String path, String envVar) throws Exception {
        commonspec.getVaultUtils().getPrincipalKrb(path, value, envVar);
    }

    /**
     * Get 'pass' from specified password. The ouput is the 'pass' saved in environmental variable.
     *
     * @param path Password's path in Vault
     * @throws Exception
     */
    @Given("^I get password from path '(.+?)' and save it in environment variable '(.+?)'$")
    public void getPwd(String path, String envVar) throws Exception {
        commonspec.getVaultUtils().getPass(path, envVar);
    }

    /**
     * Get 'user' from specified password. The ouput is the 'user' saved in environmental variable.
     *
     * @param path Password's path in Vault
     * @throws Exception
     */
    @Given("^I get user from path '(.+?)' and save it in environment variable '(.+?)'$")
    public void getUsr(String path, String envVar) throws Exception {
        commonspec.getVaultUtils().getUser(path, envVar);
    }

}
