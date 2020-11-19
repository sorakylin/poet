package com.skypyb.poet.spring.boot.core.store

import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex
import com.skypyb.poet.spring.boot.core.model.PoetAnnex
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*

class PostgresPoetAnnexRepository(jdbcTemplate: JdbcTemplate) : PoetAnnexRepository {
    val jdbcTemplate = jdbcTemplate
    var tableName = "poet_annex"

    override fun save(annex: PoetAnnex) {
        jdbcTemplate.update("INSERT INTO $tableName(name,real_name,suffix,key,length,create_time) VALUES (?,?,?,?,?,?)",
                annex.name, annex.realName, annex.suffix, annex.key, annex.length, Date())
    }

    override fun deleteByName(name: String): Int {
        return jdbcTemplate.update("DELETE FROM $tableName WHERE name=?", name)
    }

    override fun findByName(name: String): PoetAnnex? {
        val sql = "SELECT \n" +
                "    name AS name,\n" +
                "    real_name AS realName,\n" +
                "    suffix AS suffix,\n" +
                "    key AS key,\n" +
                "    length AS length\n" +
                "FROM  $tableName\n" +
                "WHERE name=?"
        val result = jdbcTemplate.query(sql, arrayOf(name), BeanPropertyRowMapper(DefaultPoetAnnex::class.java))
        return if (result.size == 0) null else result[0]
    }

    override fun findByNames(names: Collection<String>): List<PoetAnnex> {
        val sql = "SELECT \n" +
                "    name AS name,\n" +
                "    real_name AS realName,\n" +
                "    suffix AS suffix,\n" +
                "    key AS key,\n" +
                "    length AS length\n" +
                "FROM $tableName \n" +
                "WHERE name IN (?)"
        val namesString: String = names.joinToString(",")
        return jdbcTemplate.query(sql, arrayOf(namesString), BeanPropertyRowMapper(DefaultPoetAnnex::class.java))
    }
}