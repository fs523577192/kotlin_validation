package org.firas.rdb_model.dialect.mysql

import org.firas.rdb_model.dialect.DbCharset

/**
 * 
 * @author Wu Yuping
 */
class MySQLCharset private constructor(): DbCharset {

    companion object {
        @kotlin.jvm.JvmStatic
        val instance = MySQLCharset()
    }

    override fun getUTF8(): String {
        return "utf8mb4"
    }

    override fun getGB18030(): String {
        return "gb18030"
    }

    override fun getGBK(): String {
        return "gbk"
    }

    override fun getGB2312(): String {
        return "gb2312"
    }

    override fun getBig5(): String {
        return "big5"
    }
}
