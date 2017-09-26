package org.jeremyore.example.xgboost

import java.io.File

import ml.dmlc.xgboost4j.scala.Booster
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SparkSession}
import org.jpmml.evaluator.spark.{EvaluatorUtil, TransformerBuilder}

object XGBoostPmmlModelImportExample {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("XGBoostPmmlModelImportExample")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    sparkConf.registerKryoClasses(Array(classOf[Booster]))

    val spark = SparkSession
      .builder
      .master("local[*]")
      .config(sparkConf)
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    val sc = spark.sparkContext

    val dataRDD = sc.textFile("src/main/resources/data/xgboost/iris.csv")

    val schemaString = "field_0 field_1 field_2 field_3 field_4"

    // Generate the schema based on the string of schema
    val fields = schemaString.split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    val rowRDD = dataRDD
      .map(_.split(","))
      .map(attributes => Row(attributes(0).trim, attributes(1).trim, attributes(2).trim, attributes(3).trim, attributes(4).trim))

    // Apply the schema to the RDD
    val dataDF = spark.createDataFrame(rowRDD, schema)

    val evaluator = EvaluatorUtil.createEvaluator(new File("src/main/resources/data/xgboost/iris_xgb.pmml"))

    val pmmlTransformerBuilder = new TransformerBuilder(evaluator)
      .withTargetCols()
      .withOutputCols()
      .exploded(true)

    val pmmlTransformer = pmmlTransformerBuilder.build

    pmmlTransformer.transform(dataDF).show()
  }
}
