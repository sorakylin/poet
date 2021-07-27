package com.skypyb.poet.spring.boot.core.store

import com.skypyb.poet.spring.boot.core.model.DefaultPoetAnnex
import com.skypyb.poet.spring.boot.core.model.PoetAnnex
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import java.util.*
import java.util.stream.Collectors

class MySQLPoetAnnexRepository(private val jdbcTemplate: JdbcTemplate) : PoetAnnexRepository {
    val defaultRoadSign = StoreRoadSign();
    var tableName = "poet_annex"

    override fun save(annex: PoetAnnex) {
        save(annex, defaultRoadSign)
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
            WHERE `name` IN (?)
        """
        val namesString: String = names.joinToString(",")
        return jdbcTemplate.query(sql, arrayOf(namesString), BeanPropertyRowMapper(DefaultPoetAnnex::class.java))
    }

    override fun findByRoadSign(mainCategory: String?, instanceId: Long?, instanceModule: String?): List<PoetAnnex> {
        if (mainCategory == null && instanceId == null && instanceModule == null) {
            return listOf();
        }
        return listOf();
    }
}