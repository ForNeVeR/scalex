package ophir.search

import ophir.model.Def
import ophir.db.DefRepo

trait Engine {

  protected def makeResult(d: Def): Result = Result(d)

  def find(query: Query): Iterator[Result]
}

class TextEngine extends Engine {

  def find(query: Query): Iterator[Result] = {
    val tokens = Def nameToTokens query.string
    DefRepo findByTokens tokens map makeResult
  }
}

// List[String] => Int => List[Int]
class TypeEngine extends Engine {

  def find(query: Query): Iterator[Result] = {
    val tokens = Def nameToTokens query.string
    DefRepo findByTokens tokens map makeResult
  }
}

object Engine {

  val textRegex = """^([\w\s-:]+)$""".r
  val typeRegex = """^=>$""".r

  def find(query: Query): Iterator[Result] = query.string match {
    case typeRegex(text) => (new TypeEngine) find query
    case textRegex(text) => (new TextEngine) find query
  }
}