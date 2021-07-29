package com.skypyb.poet.spring.boot.core.store

import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex
import com.skypyb.poet.spring.boot.core.model.PoetAnnex
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*
import java.util.stream.Collectors

class MySQLPoetAnnexRepository(private val jdbcTemplate: JdbcTemplate) : PoetAnnexRepository {
    var tableName = "poet_annex"
    val namedParameterJdbcTemplate: NamedParameterJdbcTemplate = NamedParameterJdbcTemplate(jdbcTemplate.getDataSource())

    override fun save(annex: PoetAnnex) {
        save(annex, StoreRoadSign.empty())
    }

    override fun save(annex: PoetAnnex, roadSign: StoreRoadSign) {
        jdbcTemplate.update("INSERT INTO $tableName(`name`,`real_name`,`suffix`,`key`,`length`,`create_time`) VALUES (?,?,?,?,?,?)",
                annex.name, annex.realName, annex.suffix, annex.key, annex.length, Date())
    }

    override fun deleteByName(name: String): Int {
        return jdbcTemplate.update("DELETE FROM $tableName WHERE `name`=?", name)
    }

    override fun findByName(name: String): PoetAnnex? {

        val sql = """
            SELECT 
                `name` AS name,
                `real_name` AS realName,
                `suffix` AS suffix,
                `key` AS key,
                `length` AS length
            FROM  $tableName
            WHERE `name`=?
        """
        val result = jdbcTemplate.query(sql, arrayOf(name), BeanPropertyRowMapper(DefaultPoetAnnex::class.java))
        return if (result.size == 0) null else result[0]
    }

    override fun findByNames(names: Collection<String>): List<PoetAnnex> {
        if (names.isEmpty()) return listOf()

        val sql = """
            SELECT 
                `name` AS name,
                `real_name` AS realName,
                `suffix` AS suffix,
                `key` AS key,
                `length` AS length
            FROM  $tableName
            WHERE `name` IN (:names)
        """

        return namedParameterJdbcTemplate.query(sql, mapOf("names" to names), BeanPropertyRowMapper(DefaultPoetAnnex::class.java))
    }

    override fun findByRoadSign(mainCategory: String?, instanceId: Long?, instanceModule: String?): List<PoetAnnex> {
        if (mainCategory == null && instanceId == null && instanceModule == null) {
            return listOf();
        }
        return listOf();
    }

    override fun updateInstanceId(names: MutableCollection<String>, instanceId: Long): Int {
        return 0
    }

    override fun deleteExpireAnnex(): Int {
        return 0
    }

    override fun neverExpire(names: MutableCollection<String>): Int {
        return 0
    }
}