package org.json4s

import org.json4s.reflect.ScalaType

trait Error {
  def message: String
}
case class ParseError(message: String, line: Int = 1, pos: Int = 0) extends Error
case class MappingError[T](source: JsonAST.JValue, expected: ScalaType[T]) extends Error {
  def message = s"Couldn't map $source to $expected"
}