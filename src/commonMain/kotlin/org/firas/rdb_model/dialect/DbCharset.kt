package org.firas.rdb_model.dialect

import kotlin.js.JsName

/**
 * 
 * @author Wu Yuping
 */
interface DbCharset {

    @JsName("getUTF8")
    fun getUTF8(): String

    @JsName("getGB18030")
    fun getGB18030(): String

    @JsName("getGBK")
    fun getGBK(): String

    @JsName("getGB2312")
    fun getGB2312(): String
    
    @JsName("getBig5")
    fun getBig5(): String
}
