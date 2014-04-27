package org.json4s

case class GenericWithoutBound[T](data: List[T])
trait ToMixIn {
  def name: String
}


case class GenericWithBound[T <: ToMixIn](data: List[T])

case class Plain(data: List[ToMixIn])