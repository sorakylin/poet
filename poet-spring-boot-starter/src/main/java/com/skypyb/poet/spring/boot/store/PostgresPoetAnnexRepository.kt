package com.skypyb.poet.spring.boot.store

import com.skypyb.poet.core.model.DefaultPoetAnnex
import com.skypyb.poet.core.model.PoetAnnex
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*


class PostgresPoetAnnexRepository(private val jdbcTemplate: JdbcTemplate) : PoetAnnexRepository {
    var tableName = "poet_annex"

    val namedParameterJdbcTemplate: NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate.dataSource!!)


    override fun save(annex: PoetAnnex) {
        save(annex, StoreRoadSign.empty())
    }

    override fun save(annex: PoetAnnex, roadSign: StoreRoadSign) {
        val sql = """
            INSERT INTO $tableName(
            name,real_name,suffix,key,length,create_time,
            main_category,instance_id,instance_module,expire_time)
            VALUES (?,?,?,?,?,?,?,?,?,?)
        """;

        jdbcTemplate.update(sql,
                annex.name, annex.realName, annex.suffix, annex.key, annex.length, Date(),
                roadSign.mainCategory, roadSign.instanceId, roadSign.instanceModule, roadSign.expireTime)
    }

    override fun deleteByName(name: String): Int {
        return jdbcTemplate.update("DELETE FROM $tableName WHERE name=?", name)
    }

    override fun findByName(name: String): PoetAnnex? {
        val sql = """
            SELECT 
                name AS name,
                real_name AS realName,
                suffix AS suffix,
                key AS key,
                length AS length
            FROM  $tableName
            WHERE name=?
        """
        val result = jdbcTemplate.query(sql, arrayOf(name), BeanPropertyRowMapper(DefaultPoetAnnex::class.java))
        return if (result.size == 0) null else result[0]
    }

    override fun findByNames(names: Collection<String>): List<PoetAnnex> {
        if (names.isEmpty()) return listOf()

        val sql = """
            SELECT 
                name AS name,
                real_name AS realName,
                suffix AS suffix,
                key AS key,
                length AS length
            FROM  $tableName
            WHERE name IN (:names)
        """

        return namedParameterJdbcTemplate.query(sql, mapOf("names" to names), BeanPropertyRowMapper(DefaultPoetAnnex::class.java))
    }


    override fun findByRoadSign(mainCategory: String?, instanceId: Long?, instanceModule: String?): List<PoetAnnex> {
        if (mainCategory == null && instanceId == null && instanceModule == null) {
            return listOf();
        }
        val sql = """
            SELECT 
                name AS name,
                real_name AS realName,
                suffix AS suffix,
                key AS key,
                length AS length
            FROM  $tableName
            WHERE main_category=? AND instance_id=? AND instance_module=?
        """
        return jdbcTemplate.query(sql, arrayOf(mainCategory, instanceId, instanceModule), BeanPropertyRowMapper(
            DefaultPoetAnnex::class.java))
    }

    override fun updateInstanceId(names: MutableCollection<String>, instanceId: Long): Int {
        if (names.isEmpty()) return 0

        val params = mapOf(
                "instanceId" to instanceId,
                "names" to names
        )

        val sql = "UPDATE $tableName SET instance_id = :instanceId WHERE name IN (:names)";

        return namedParameterJdbcTemplate.update(sql, params);
    }

    override fun findExpireAnnex(): List<String> {
        val sql = "SELECT name FROM $tableName WHERE expire_time < now()";
        return jdbcTemplate.queryForList(sql, String::class.java);
    }

    override fun neverExpire(names: MutableCollection<String>): Int {
        if (names.isEmpty()) return 0

        val sql = "UPDATE $tableName SET expire_time = null WHERE name IN (:names)";

        return namedParameterJdbcTemplate.update(sql, mapOf("names" to names));
    }
}