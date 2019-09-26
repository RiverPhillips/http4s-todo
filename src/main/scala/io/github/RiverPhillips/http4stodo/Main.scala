package io.github.RiverPhillips.http4stodo

import cats.effect.{Blocker, ExitCode, IO, IOApp, Resource}
import cats.implicits._

import io.github.RiverPhillips.http4stodo.repository.TodoRepository
import io.github.RiverPhillips.http4stodo.service.TodoService
import io.github.RiverPhillips.http4stodo.database.Database
import org.http4s.server.blaze.BlazeServerBuilder


object Main extends IOApp {
  val transactor = Database.dbTransactor

  override def run(args: List[String]): IO[ExitCode] =
    transactor.use { xa =>

      val todoRepository = new TodoRepository(xa)

      BlazeServerBuilder[IO]
        .bindHttp(8080, "localhost")
        .withHttpApp(new TodoService(todoRepository).service)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }

}