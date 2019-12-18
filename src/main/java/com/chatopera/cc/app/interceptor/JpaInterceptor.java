package com.chatopera.cc.app.interceptor;

import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.util.CollectionUtils;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

public class JpaInterceptor implements StatementInspector {

    private static CCJSqlParserManager pm = new CCJSqlParserManager();

    public static Set<String> useTables = Sets.newConcurrentHashSet();


    @SneakyThrows
    @Override
    public String inspect(String sql) {
        //System.out.println("进入拦截器" + sql);
        List<String> tableNames = JpaInterceptor.getTableNames(sql);
        if(!CollectionUtils.isEmpty(tableNames)){
            JpaInterceptor.useTables.addAll(tableNames);
        }

        return sql;
    }

    private static List<String> getTableNames(String sql) throws Exception {
        List<String> tablenames = null;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        Statement statement = pm.parse(new StringReader(sql));

        if (statement instanceof Select) {
            tablenames = tablesNamesFinder.getTableList((Select) statement);
        } else if (statement instanceof Update) {
            tablenames = tablesNamesFinder.getTableList((Update) statement);

        } else if (statement instanceof Delete) {
            tablenames = tablesNamesFinder.getTableList((Delete) statement);

        } else if (statement instanceof Replace) {
            tablenames = tablesNamesFinder.getTableList((Replace) statement);

        } /*else if (statement instanceof Insert) {
            tablenames = tablesNamesFinder.getTableList((Insert) statement);

        }*/
        return tablenames;
    }
}
