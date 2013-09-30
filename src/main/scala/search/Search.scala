package org.scalex
package search

import scala.util.{ Try, Success, Failure }

import akka.actor.{ ActorRef, Props }
import akka.pattern.ask

final class Search(env: Env) {

  private implicit val timeout = makeTimeout.veryLarge

  private val actor: ActorRef = env.system.actorOf(Props(
    new SearchActor(env.config)
  ), name = "search")

  def apply(expression: String): Fu[Try[result.Results]] = {
    println("Search for \"%s\"" format expression)
    actor ? expression mapTo manifest[Try[result.Results]]
  }
}
