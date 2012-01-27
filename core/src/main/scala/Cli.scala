package scalex

import scalex.dump.Locator
import scalex.model.Def
import scalex.search._

import java.io.File
import scalaz.Scalaz.{ success, failure }

object Cli {

  def env = new Env

  def main(args: Array[String]): Unit = sys exit {
    println(args.toList match {
      case Nil             ⇒ "No command given."
      case command :: args ⇒ process(command, args)
    })
    0
  }

  def process(command: String, args: List[String]) = command match {
    case "dump"   ⇒ dump(args)
    case "index"  ⇒ index()
    case "search" ⇒ search(args mkString " ")
    case command  ⇒ "Unknown command " + command
  }

  def search(query: String): String =
    (env.engine find RawQuery(query, 1, 5)).fold(identity, {
      case Results(paginator, defs) ⇒
        "%d results for %s\n\n%s" format (paginator.nbResults, query, render(defs))
    })

  def dump(fs: List[String]): String = {
    val pack = fs.head
    val files = fs.tail map (f ⇒ new File(f))
    val sources = (new Locator) locate files map (_.getPath)
    env.dumper.process(pack, sources)
    "Dump complete"
  }

  def index(): String = {
    env.indexRepo write (env.defRepo.findAll map (_.toIndex))
    "Index complete"
  }

  private def render(d: Def): String =
    "[" + d.pack + "] " + d.name + "\n  " + d.toString + "\n" + (
      d.comment map (_.short) getOrElse "no comment"
    )

  private def render(ds: Seq[Def]): String =
    ds map render map ("* "+) mkString "\n\n"
}