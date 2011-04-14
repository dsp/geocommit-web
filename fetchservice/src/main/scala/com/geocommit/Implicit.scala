package com.geocommit

import com.surftools.BeanstalkClient.Job
import net.liftweb.json.JsonAST._
import net.liftweb.json.Printer._

object Implicit {
    implicit def job2RichJob(j: Job): RichJob =
        new RichJob(j)

    implicit def json2ByteArray(j: JValue): Array[Byte] =
        compact(render(j)).getBytes("UTF-8")

    implicit def byteArray2String(b: Array[Byte]): String =
        new String(b, "UTF-8")

    implicit def string2ByteArray(s: String): Array[Byte] =
        s.getBytes("UTF-8")
}

/* vim: set sw=4 */
