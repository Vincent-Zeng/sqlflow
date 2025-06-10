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
        sb.append("with a1 as (                                                                                                   \n");
        sb.append("             select                                                                                            \n");
        sb.append("               concat(a.COL1, '-', a.COL2),                                                                    \n");
        sb.append("               a.desc,                                                                                         \n");
        sb.append("               substr(current_timestamp(), 1, 19) AS data_store_time,                                          \n");
        sb.append("               current_date - INTERVAL 10 MINUTE  as dd                                                        \n");
        sb.append("             from db1.test a, db1.test b                                                                       \n");
        sb.append("             left join db1.test c                                                                              \n");
        sb.append("               on a.desc = c.desc                                                                              \n");
        sb.append("             where a.ds = '201912'                                                                             \n");
        sb.append("               and a.desc = b.desc                                                                             \n");
        sb.append("           ),                                                                                                  \n");
        sb.append("     a2 as (                                                                                                   \n");
        sb.append("             select                                                                                            \n");
        sb.append("               (select desc as i_desc, t1.desc as o_desc from a1 i1 where i1.desc = t1.desc limit 1) as itest, \n");
        sb.append("               t1.*,                                                                                           \n");
        sb.append("               t1.dd as tdd                                                                                    \n");
        sb.append("             from a1 t1                                                                                        \n");
        sb.append("           )                                                                                                   \n");
        sb.append("insert overwrite table db2.Demo                                                                                \n");
        sb.append("select                                                                                                         \n");
        sb.append("  t2.*                                                                                                         \n");
        sb.append("from a1 t1                                                                                                     \n");
        sb.append("left join a2 t2                                                                                                \n");
        sb.append("on t1.desc = t2.desc                                                                                           \n");

        final String sql = sb.toString();
        Statement statement = SQL_PARSER.createStatement(sql);

        Analysis analysis = new Analysis(statement, emptyMap());
        StatementAnalyzer statementAnalyzer = new StatementAnalyzer(analysis, new SimpleSparkMetadataService(), SQL_PARSER);

        statementAnalyzer.analyze(statement, Optional.empty());

        //System.out.println(SqlFormatter.formatSql(statement));
        System.out.println(JsonUtils.toJSONString(analysis.getTarget().get()));
    }
}
