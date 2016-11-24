package org.apodhrad.jeclipse.manager;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DevstudioTest {

	public static final String JBDS_7_1_1_GA = "jbdevstudio-product-universal-7.1.1.GA-v20140314-2145-B688.jar";
	public static final String JBDS_8_1_0_GA = "jboss-devstudio-8.1.0.GA-installer-standalone.jar";
	public static final String JBDS_9_1_0_GA = "jboss-devstudio-9.1.0.GA-installer-standalone.jar";
	public static final String JBDS_10_1_0_GA = "devstudio-10.1.0.GA-installer-standalone.jar";
	public static final String JBDS_10_1_0_GA_EAP = "devstudio-10.1.0.GA-installer-eap.jar";
	public static final String JBDS_10_2_0_AM2 = "devstudio-10.2.0.AM2-v20161014-1657-B6205-installer-standalone.jar";
	public static final String JBDSIS_10_0_0_CR1 = "devstudio-integration-stack-10.0.0.CR1-standalone-installer.jar";
	public static final String JBDSIS_10_0_0_CR1_RT = "devstudio-integration-stack-rt-10.0.0.CR1-standalone-installer.jar";

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

}
