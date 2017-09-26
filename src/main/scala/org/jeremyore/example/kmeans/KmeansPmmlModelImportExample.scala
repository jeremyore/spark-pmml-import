package org.jeremyore.example.kmeans

import java.io.File

import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SparkSession}
import org.jpmml.evaluator.spark.{EvaluatorUtil, TransformerBuilder}

object KmeansPmmlModelImportExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[*]")
      .appName("KmeansPmmlModelImportExample")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    val sc = spark.sparkContext

    val dataRDD = sc.textFile("src/main/resources/data/kmeans/kmeans_data.txt")

    val schemaString = "field_0 field_1 field_2"

    // Generate the schema based on the string of schema
    val fields = schemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    val rowRDD = dataRDD
      .map(_.split(" "))
      .map(attributes => Row(attributes(0).trim, attributes(1).trim, attributes(2).trim))

    // Apply the schema to the RDD
    val dataDF = spark.createDataFrame(rowRDD, schema)

    val evaluator = EvaluatorUtil.createEvaluator(new File("src/main/resources/data/kmeans/kmeans.pmml"))

    val pmmlTransformerBuilder = new TransformerBuilder(evaluator)
      .withTargetCols()
      .withOutputCols()
      .exploded(true)

    val pmmlTransformer = pmmlTransformerBuilder.build

    pmmlTransformer.transform(dataDF).show()
  }
}
