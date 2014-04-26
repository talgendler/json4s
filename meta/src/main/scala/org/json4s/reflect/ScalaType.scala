package org.json4s.reflect

trait ScalaType {
  def typeArgs: Seq[ScalaType]
  def isArray: Boolean
  def rawFullName: String
  def rawSimpleName: String
  def simpleName: String
  def fullName: String
  def typeInfo: TypeInfo
  def isPrimitive: Boolean
  def isMap: Boolean
  def isCollection: Boolean
  def isOption: Boolean
  def isEither: Boolean
  override def toString: String = simpleName
}