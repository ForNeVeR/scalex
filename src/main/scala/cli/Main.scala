package org.scalex
package cli

import scala.concurrent.duration._
import scala.concurrent.{ Future, Await }
import scala.util.{ Try, Success, Failure }

import com.typesafe.config.ConfigFactory

object Main {

  def main(args: Array[String]): Unit = sys exit {
    Await.result(process(args) map (_ ⇒ 0) recover {
      case e: IllegalArgumentException ⇒ {
        println("! %s: %s".format("Illegal argument", e.getMessage))
        1
      }
      case e: Exception ⇒ {
        println("! " + e)
        1
      }
    }, 1 hour)
  }

  private def process(args: Array[String]): Fu[Unit] = (args.toList match {
    // TODO real option to set optional scaladoc url
    case "index" :: name :: version :: rest ⇒ Future {
      index Indexer api.Index(name, version, Some("http://www.scala-lang.org/api/2.10.3"), rest)
    }
    case "server" :: port :: Nil ⇒ port.parseInt.fold(
      err ⇒ fufail(badArg(s"Invalid server port: $port")),
      port ⇒ Env.using(Env.defaultConfig) { env ⇒
        new server.Server(new search.Search(env), port)
        fuccess(println("Server stopped"))
      }
    )
    case _ ⇒ Parser.parse(args).fold(fufail[Any](badArg(args mkString " "))) {
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer indexConfig)
      // case Config(Some(indexConfig), _) ⇒ Success(index Indexer api.Index.test)
      case Config(None, Some(searchConfig)) ⇒
        Env.using(Env.defaultConfig) { env ⇒
          (new search.Search(env) apply searchConfig.expression) andThen {
            case Success(results) ⇒ results.fold(
              err ⇒ println(s"Invalid query: $err"),
              println
            )
            case Failure(err) ⇒ println(s"An error occured: $err")
          }
        }
      case c ⇒ fufail(badArg(c.toString))
    }
  }).void
}
