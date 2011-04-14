package com.geocommit

import com.geocommit.Implicit._

import net.liftweb.json.JsonParser
import net.liftweb.json.JsonParser._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Printer._

import com.surftools.BeanstalkClient.Job

class RichJob(job: Job) {
    def getData(): JValue = JsonParser parse job.getData()
    def getJobId() = job.getJobId()
}

