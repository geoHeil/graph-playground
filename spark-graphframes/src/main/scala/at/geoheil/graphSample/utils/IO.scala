package at.geoheil.graphSample.utils

import org.apache.spark.sql.{ Row, SparkSession }
import org.graphframes.GraphFrame

object IO {

  def readCsv(spark: SparkSession, path: String) = {
    spark.read
      .option("header", true)
      .option("inferSchema", true)
      .option("delimiter", ",")
      .csv(path)
  }

  def toGraphML(g: GraphFrame): String = {
    val s = g.edges.sparkSession
    import s.implicits._
    s"""
       |<?xml version="1.0" encoding="UTF-8"?>
       |<graphml xmlns="http://graphml.graphdrawing.org/xmlns"
       |         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       |         xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns
       |         http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
       |
     |  <key id="v_name" for="node" attr.name="name" attr.type="string"/>
       |  <key id="v_fraud" for="node" attr.name="fraud" attr.type="int"/>
       |  <key id="e_edgeType" for="edge" attr.name="edgeType" attr.type="string"/>
       |  <graph id="G" edgedefault="directed">
       |${
      g.vertices.map {
        case Row(id, name, fraud) =>
          s"""
             |      <node id="${id}">
             |         <data key = "v_name">${name}</data>
             |         <data key = "v_fraud">${fraud}</data>
             |      </node>
           """.stripMargin
      }.collect.mkString.stripLineEnd
    }
       |${
      g.edges.map {
        case Row(src, dst, relationship) =>
          s"""
             |      <edge source="${src}" target="${dst}">
             |      <data key="e_edgeType">${relationship}</data>
             |      </edge>
           """.stripMargin
      }.collect.mkString.stripLineEnd
    }
       |  </graph>
       |</graphml>
  """.stripMargin
  }
}
