package io.github.melin.sqlflow.parser.spark;

import io.github.melin.sqlflow.analyzer.Analysis;
import io.github.melin.sqlflow.analyzer.StatementAnalyzer;
import io.github.melin.sqlflow.parser.AbstractSqlLineageTest;
import io.github.melin.sqlflow.parser.SqlFlowParser;
import io.github.melin.sqlflow.tree.statement.Statement;
import io.github.melin.sqlflow.util.JsonUtils;
import org.junit.Test;

import java.util.Optional;

import static java.util.Collections.emptyMap;

/**
 * huaixin 2021/12/18 11:13 PM
 */
public class SparkSqlLineageTest extends AbstractSqlLineageTest {

    protected static final SqlFlowParser SQL_PARSER = new SqlFlowParser();

    @Test
    public void testInsertInto() throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("with sdfa as (                                                          \n");
        sb.append("               select                                                   \n");
        sb.append("                 concat(a.COL1, '-', a.COL2),                           \n");
        sb.append("                 a.desc,                                                \n");
        sb.append("                 substr(current_timestamp(), 1, 19) AS data_store_time, \n");
        sb.append("                 current_date - INTERVAL 10 MINUTE  as dd               \n");
        sb.append("               from db1.test a                                          \n");
        sb.append("               where ds = '201912'                                      \n");
        sb.append("             )                                                          \n");
        sb.append("insert overwrite table db2.Demo                                         \n");
        sb.append("select *                                                                \n");
        sb.append("from sdfa                                                               \n");

        final String sql = sb.toString();
        Statement statement = SQL_PARSER.createStatement(sql);

        Analysis analysis = new Analysis(statement, emptyMap());
        StatementAnalyzer statementAnalyzer = new StatementAnalyzer(analysis, new SimpleSparkMetadataService(), SQL_PARSER);

        statementAnalyzer.analyze(statement, Optional.empty());

        //System.out.println(SqlFormatter.formatSql(statement));
        System.out.println(JsonUtils.toJSONString(analysis.getTarget().get()));
    }
}
