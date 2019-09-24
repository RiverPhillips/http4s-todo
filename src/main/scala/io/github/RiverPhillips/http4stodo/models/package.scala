package io.github.RiverPhillips.http4stodo

package object models {
  case class Todo(id: Option[Long], description: String)

  case object TodoNotFoundError
}
