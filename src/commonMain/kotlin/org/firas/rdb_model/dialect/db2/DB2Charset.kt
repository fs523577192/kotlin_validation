package org.firas.rdb_model.dialect.db2

import org.firas.rdb_model.dialect.DbCharset

/**
 * Code page number in DB2 for the character set
 * @author Wu Yuping
 */
class DB2Charset private constructor(): DbCharset {

    companion object {
        @kotlin.jvm.JvmStatic
        val instance = DB2Charset()
    }

    override fun getUTF8(): String {
        return "1208"
    }

    override fun getGB18030(): String {
        return "gb18030"
    }

    override fun getGBK(): String {
        return "1386"
    }

    override fun getGB2312(): String {
        return "1383"
    }

    override fun getBig5(): String {
        return "950"
    }
}
