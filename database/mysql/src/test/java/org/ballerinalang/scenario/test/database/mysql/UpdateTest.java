package org.ballerinalang.scenario.test.database.mysql;

import org.ballerinalang.config.ConfigRegistry;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.scenario.test.common.ScenarioTestBase;
import org.ballerinalang.scenario.test.common.database.DatabaseUtil;
import org.ballerinalang.scenario.test.database.util.AssertionUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Test(groups = Constants.MYSQL_TESTNG_GROUP)
public class UpdateTest extends ScenarioTestBase {
    private CompileResult updateCompileResult;
    private String jdbcUrl;
    private String userName;
    private String password;
    private Path resourcePath;

    @BeforeClass
    public void setup() throws Exception {
        Properties deploymentProperties = getDeploymentProperties();
        jdbcUrl = deploymentProperties.getProperty(Constants.MYSQL_JDBC_URL_KEY) + "/testdb";
        userName = deploymentProperties.getProperty(Constants.MYSQL_JDBC_USERNAME_KEY);
        password = deploymentProperties.getProperty(Constants.MYSQL_JDBC_PASSWORD_KEY);

        ConfigRegistry registry = ConfigRegistry.getInstance();
        HashMap<String, String> configMap = new HashMap<>(3);
        configMap.put(Constants.MYSQL_JDBC_URL_KEY, jdbcUrl);
        configMap.put(Constants.MYSQL_JDBC_USERNAME_KEY, userName);
        configMap.put(Constants.MYSQL_JDBC_PASSWORD_KEY, password);
        registry.initRegistry(configMap, null, null);

        resourcePath = Paths.get("src", "test", "resources").toAbsolutePath();
        DatabaseUtil.executeSqlFile(jdbcUrl, userName, password,
                Paths.get(resourcePath.toString(), "sql-src", "ddl-select-update-test.sql"));
        updateCompileResult = BCompileUtil
                .compileAndSetup(Paths.get(resourcePath.toString(), "bal-src", "update-test.bal").toString());
    }

    @Test(description = "Test update numeric types with values")
    public void testUpdateNumericTypesWithValues() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testUpdateNumericTypesWithValues");
        AssertionUtil.assertUpdateQueryReturnValue(returns[0], 1);
    }

    @Test(description = "Test update numeric types with params")
    public void testUpdateNumericTypesWithParams() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testUpdateNumericTypesWithParams");
        AssertionUtil.assertUpdateQueryReturnValue(returns[0], 1);
    }

    @Test(description = "Test update string types with params")
    public void testUpdateStringTypesWithValues() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testUpdateStringTypesWithValues");
        AssertionUtil.assertUpdateQueryReturnValue(returns[0], 1);
    }

    @Test(description = "Test update string types with params")
    public void testUpdateStringTypesWithParams() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testUpdateStringTypesWithParams");
        AssertionUtil.assertUpdateQueryReturnValue(returns[0], 1);
    }

    @Test(description = "Test update complex types")
    public void testUpdateComplexTypesWithValues() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testUpdateComplexTypesWithValues");
        AssertionUtil.assertUpdateQueryReturnValue(returns[0], 1);
    }

    @Test(description = "Test update complex types with params")
    public void testUpdateComplexTypesWithParams() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testUpdateComplexTypesWithParams");
        AssertionUtil.assertUpdateQueryReturnValue(returns[0], 1);
    }

    @Test(description = "Test update datetime types with params")
    public void testUpdateDateTimeWithValuesParam() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testUpdateDateTimeWithValuesParam");
        AssertionUtil.assertUpdateQueryReturnValue(returns[0], 1);
    }

    @Test(description = "Test Update with generated keys")
    public void testGeneratedKeyOnInsert() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testGeneratedKeyOnInsert");
        Map<String, String> expectedGeneratedKeys = new HashMap<>(1);
        expectedGeneratedKeys.put("GENERATED_KEY", "1");
        AssertionUtil.assertUpdateQueryWithGeneratedKeysReturnValue(returns[0], 1, expectedGeneratedKeys);
    }

    @Test(description = "Test Update with generated keys - empty results scenario")
    public void testGeneratedKeyOnInsertEmptyResults() {
        BValue[] returns = BRunUtil.invokeStateful(updateCompileResult, "testGeneratedKeyOnInsertEmptyResults");
        AssertionUtil.assertUpdateQueryWithGeneratedKeysReturnValue(returns[0], 1, new HashMap<>(0));
    }

    @AfterClass(alwaysRun = true)
    public void cleanup() throws Exception {
        BRunUtil.invokeStateful(updateCompileResult, "stopDatabaseClient");
        DatabaseUtil.executeSqlFile(jdbcUrl, userName, password,
                Paths.get(resourcePath.toString(), "sql-src", "cleanup-select-update-test.sql"));
    }

}
