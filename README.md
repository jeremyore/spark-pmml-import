#Spark PMML Import Example

Spark官方只提供Pmml Export操作，由于工作中，经常会将R 的模型算法 应用到Spark MLLib中，基于跨平台的不方便，
且Spark官网并没有相应的将R导出的Pmml文件Import到Spark MLLib中作为Model Train,这里将常用的Pmml跨平台操作
Example Mark一下

#Version and Compatibility

Spark Spark 2.0.2

#Features:

 - Spark Pmml 操作基于 Spark Pipeline,源码中给出了一个Spark官网的Pipeline Example
 - data 数据中的 pmml 文件都是由R中Train Model 生成
 - 大部分源码基于Spark官方源码 和 jpmml 项目
 - 目前只公开了kmeans、xgboost的 pmml example，以后会不定期更新其他算法




