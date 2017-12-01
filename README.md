What is this
----
This is an example application demonstrating wrong behaviour of Apache Cassandra when performing a compare-and-set operation specifying an equality criterion with a non-`null` value that encounters a `null` cell.

The problem was reported as [CASSANDRA-14087](https://issues.apache.org/jira/browse/CASSANDRA-14087).


How to use this
----
Execute `mvn verify`

* Test results are at `target/failsafe-reports/index.html`
* The tables used by the test are defined in `src/cassandra/cql/load.cql`.
* Switch to different cassandra driver or server versions by changing the properties in `pom.xml`.


Expected results
----
For each test, the compare-and-set criterion is not fulfilled, so the query reports `wasApplied() == false` and the test passes.


Actual results
----
The tests for tables `frozenlists`, `frozensets` and `frozenmaps` throw `NoHostAvailableException` reporting that an unexpected error occurred server side, a `java.lang.NullPointerException`. On Cassandra 3.11.0, the corresponding stack trace in the debug log is:

````
ERROR [Native-Transport-Requests-1] 2017-11-27 12:59:26,924 QueryMessage.java:129 - Unexpected error during query
java.lang.NullPointerException: null
        at org.apache.cassandra.cql3.ColumnCondition$CollectionBound.appliesTo(ColumnCondition.java:546) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.cql3.statements.CQL3CasRequest$ColumnsConditions.appliesTo(CQL3CasRequest.java:324) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.cql3.statements.CQL3CasRequest.appliesTo(CQL3CasRequest.java:210) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.service.StorageProxy.cas(StorageProxy.java:265) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.cql3.statements.ModificationStatement.executeWithCondition(ModificationStatement.java:441) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.cql3.statements.ModificationStatement.execute(ModificationStatement.java:416) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.cql3.QueryProcessor.processStatement(QueryProcessor.java:217) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.cql3.QueryProcessor.process(QueryProcessor.java:248) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.cql3.QueryProcessor.process(QueryProcessor.java:233) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.transport.messages.QueryMessage.execute(QueryMessage.java:116) ~[apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.transport.Message$Dispatcher.channelRead0(Message.java:517) [apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.transport.Message$Dispatcher.channelRead0(Message.java:410) [apache-cassandra-3.11.0.jar:3.11.0]
        at io.netty.channel.SimpleChannelInboundHandler.channelRead(SimpleChannelInboundHandler.java:105) [netty-all-4.0.44.Final.jar:4.0.44.Final]
        at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:357) [netty-all-4.0.44.Final.jar:4.0.44.Final]
        at io.netty.channel.AbstractChannelHandlerContext.access$600(AbstractChannelHandlerContext.java:35) [netty-all-4.0.44.Final.jar:4.0.44.Final]
        at io.netty.channel.AbstractChannelHandlerContext$7.run(AbstractChannelHandlerContext.java:348) [netty-all-4.0.44.Final.jar:4.0.44.Final]
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) [na:1.8.0_151]
        at org.apache.cassandra.concurrent.AbstractLocalAwareExecutorService$FutureTask.run(AbstractLocalAwareExecutorService.java:162) [apache-cassandra-3.11.0.jar:3.11.0]
        at org.apache.cassandra.concurrent.SEPWorker.run(SEPWorker.java:109) [apache-cassandra-3.11.0.jar:3.11.0]
        at java.lang.Thread.run(Thread.java:748) [na:1.8.0_151]
````