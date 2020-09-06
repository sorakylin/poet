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
        jdbcTemplate.update("INSERT INTO " + tableName + "(name,real_name,suffix,key,length,create_time) VALUES (?,?,?,?,?,?)",
                annex.getName(), annex.getRealName(), annex.getSuffix(), annex.getKey(), annex.getLength(), new Date());
    }

    @Override
    public int deleteByName(String name) {
        return jdbcTemplate.update("DELETE FROM " + tableName + " WHERE name=?", name);
    }

    @Override
    public PoetAnnex findByName(String name) {

        final String sql = "SELECT \n" +
                "    name AS name,\n" +
                "    real_name AS realName,\n" +
                "    suffix AS suffix,\n" +
                "    key AS key,\n" +
                "    length AS length\n" +
                "FROM " + tableName + " \n" +
                "WHERE name=?";

        final List<DefaultPoetAnnex> result = jdbcTemplate.queryForList(sql, new Object[]{name}, DefaultPoetAnnex.class);
        return result.size() == 0 ? null : result.get(0);
    }

    @Override
    public List<? extends PoetAnnex> findByNames(Collection<String> names) {
        final String sql = "SELECT \n" +
                "    name AS name,\n" +
                "    real_name AS realName,\n" +
                "    suffix AS suffix,\n" +
                "    key AS key,\n" +
                "    length AS length\n" +
                "FROM " + tableName + " \n" +
                "WHERE name IN (?)";

        final String namesString = names.stream().collect(Collectors.joining(","));
        return jdbcTemplate.queryForList(sql, new Object[]{namesString}, DefaultPoetAnnex.class);
    }

}
