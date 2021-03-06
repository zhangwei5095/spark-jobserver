package spark.jobserver.util

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class SparkJobUtilsSpec extends FunSpec with ShouldMatchers {
  import collection.JavaConverters._

  val config = ConfigFactory.parseMap(Map(
                 "spark.home" -> "/etc/spark"
               ).asJava)
  val sparkMaster = "local[4]"
  val contextName = "demo"

  def getSparkConf(configMap: Map[String, Any]): SparkConf =
    SparkJobUtils.configToSparkConf(config, ConfigFactory.parseMap(configMap.asJava),
                                    sparkMaster, contextName)

  describe("SparkJobUtils.configToSparkConf") {
    it("should translate num-cpu-cores and memory-per-node properly") {
      val sparkConf = getSparkConf(Map("num-cpu-cores" -> 4, "memory-per-node" -> "512m"))
      sparkConf.get("spark.master") should equal ("local[4]")
      sparkConf.get("spark.cores.max") should equal ("4")
      sparkConf.get("spark.executor.memory") should equal ("512m")
      sparkConf.get("spark.home") should equal ("/etc/spark")
    }

    it("should add other arbitrary settings") {
      val sparkConf = getSparkConf(Map("spark.cleaner.ttl" -> 86400))
      sparkConf.getInt("spark.cleaner.ttl", 0) should equal (86400)
    }
  }
}