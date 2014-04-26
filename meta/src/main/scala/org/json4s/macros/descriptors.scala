package org.json4s.macros

import org.json4s.reflect.ScalaType
import scala.language.experimental.macros
import scala.reflect.macros._

trait MetaScalaType[T] {
  def fullName: String
  def rawFullName: String
  def simpleName: String
  def rawSimpleName: String
  def isPrimitive: Boolean
  def isMap: Boolean
  def isCollection: Boolean
  def isOption: Boolean
  def isEither: Boolean
  def isArray: Boolean
}
object MetaScalaType {

  def metaScalaType[T]: MetaScalaType[T] = macro metaScalaTypeImpl[T]
  def metaScalaTypeImpl[T: c.WeakTypeTag](c: Context): c.Expr[MetaScalaType[T]] = {
    import c.universe._
    val helper = new Helper[c.type](c)
    val tpe = c.weakTypeOf[T].normalize
    val rfn = tpe.typeSymbol.fullName
    val sn = tpe.typeSymbol.name.decodedName.toString.trim
    val rsn = tpe.typeSymbol.name.encodedName.toString.trim

    reify {
      new MetaScalaType[T] {
        val fullName: String = c.Expr[String](Literal(Constant(rfn))).splice
        val rawFullName: String = c.Expr[String](Literal(Constant(rfn))).splice
        val simpleName: String = c.Expr[String](Literal(Constant(sn))).splice
        val rawSimpleName: String = c.Expr[String](Literal(Constant(rsn))).splice
        val isPrimitive: Boolean = c.Expr[Boolean](Literal(Constant(helper.isPrimitive(tpe)))).splice
        val isMap: Boolean = c.Expr[Boolean](Literal(Constant(helper.isMap(tpe)))).splice
        val isCollection: Boolean = c.Expr[Boolean](Literal(Constant(helper.isCollection(tpe)))).splice
        val isOption: Boolean = c.Expr[Boolean](Literal(Constant(helper.isOption(tpe)))).splice
        val isEither: Boolean = c.Expr[Boolean](Literal(Constant(helper.isEither(tpe)))).splice
        val isArray: Boolean = c.Expr[Boolean](Literal(Constant(helper.isArray(tpe)))).splice
      }
    }
  }

}