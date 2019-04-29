package org.firas.rdb_model.dialect

/**
 * 
 * @author Wu Yuping
 */
interface DbCharset {

    fun getUTF8(): String

    fun getGB18030(): String

    fun getGBK(): String

    fun getGB2312(): String
}