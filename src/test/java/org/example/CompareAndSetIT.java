package org.example;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;
import static com.datastax.driver.core.querybuilder.QueryBuilder.update;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class CompareAndSetIT
{
    private Cluster cluster;
    private Session session;

    @BeforeClass
    void setUp()
    {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setConnectionsPerHost(HostDistance.LOCAL, 10, 10);

        cluster = Cluster.builder()
            .addContactPoints("127.0.0.1")
            .withPoolingOptions(poolingOptions)
            .build()
            .init();
        session = cluster.connect();
    }

    @AfterClass
    void tearDown()
    {
        session.close();
        cluster.close();
    }

    @Test(dataProvider = "testData")
    public void performCompareAndSetTest(String tableName, Object comparisonValue, Object newValue)
    {
        session.execute(QueryBuilder.insertInto("testingkeyspace", tableName)
            .value("key", "test")
            .value("content", null));

        ResultSet resultSet = session.execute(update("testingkeyspace", tableName).with(set("content", newValue))
            .where(eq("key", "test"))
            .onlyIf(eq("content", comparisonValue)));

        assertThat(resultSet.wasApplied()).isFalse();
    }

    @DataProvider
    Object[][] testData()
    {
        return new Object[][]{
            { "texts", "comparison", "new value" },
            { "lists", Collections.singletonList("comparison"), Collections.singletonList("new") },
            { "sets", Collections.singleton("comparison"), Collections.singleton("new") },
            { "maps", Collections.singletonMap("comparison", "foo"), Collections.singletonMap("new", "bar") },
            { "frozenlists", Collections.singletonList("comparison"), Collections.singletonList("new") },
            { "frozensets", Collections.singleton("comparison"), Collections.singleton("new") },
            { "frozenmaps", Collections.singletonMap("comparison", "foo"), Collections.singletonMap("new", "bar") }
        };
    }
}