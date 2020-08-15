package com.skypyb.poet.spring.boot.core.store;

import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex;
import com.skypyb.poet.spring.boot.core.model.PoetAnnex;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JdbcPoetAnnexRepository implements PoetAnnexRepository {

    private JdbcTemplate jdbcTemplate;

    private String tableName = "tb_poet_annex";


    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void save(PoetAnnex annex) {
        jdbcTemplate.update("INSERT INTO ?(`name`,`real_name`,`suffix`,`key`,`length`,`create_time`) VALUES (?,?,?,?,?,?)",
                tableName, annex.getName(), annex.getRealName(), annex.getSuffix(), annex.getLength(), new Date());
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("DELETE FROM ? WHERE `name`=?", tableName, name);
    }

    @Override
    public PoetAnnex findByName(String name) {

        final String sql = "SELECT \n" +
                "    `name` AS `name`,\n" +
                "    `real_name` AS `realName`,\n" +
                "    `suffix` AS suffix,\n" +
                "    `key` AS `key`,\n" +
                "    `length` AS `length`\n" +
                "FROM ? \n" +
                "WHERE `name`=?";

        return jdbcTemplate.queryForObject(sql, new Object[]{tableName, name}, DefaultPoetAnnex.class);
    }

    @Override
    public List<? extends PoetAnnex> findByNames(Collection<String> names) {
        final String sql = "SELECT \n" +
                "    `name` AS `name`,\n" +
                "    `real_name` AS `realName`,\n" +
                "    `suffix` AS suffix,\n" +
                "    `key` AS `key`,\n" +
                "    `length` AS `length`\n" +
                "FROM ? \n" +
                "WHERE `name` IN (?)";

        final String namesString = names.stream().collect(Collectors.joining(","));
        return jdbcTemplate.queryForList(sql, new Object[]{tableName, namesString}, DefaultPoetAnnex.class);
    }

}
