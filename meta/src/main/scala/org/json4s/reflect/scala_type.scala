package org.json4s.reflect

import scala.language.experimental.macros
import scala.reflect.macros._


trait ScalaType[T] extends ScalaTypeBase {
  def tpe: scala.reflect.runtime.universe.Type
  def <:<(that: ScalaType[_]): Boolean
  def >:>(that: ScalaType[_]): Boolean
}

object ScalaType {

  private[this] def pmst[T: scala.reflect.runtime.universe.WeakTypeTag](fn: String, sn: String): ScalaType[T] = new ScalaType[T] {
    def typeArgs: Seq[ScalaTypeBase] = Vector.empty
    def fullName: String = fn
    def rawFullName: String = fn
    def simpleName: String = sn
    def rawSimpleName: String = sn
    def isMap: Boolean = false
    def isPrimitive: Boolean = true
    def isCollection: Boolean = false
    def isArray: Boolean = false
    def isOption: Boolean = false
    def isEither: Boolean = false
    def typeInfo: TypeInfo = null
    lazy val tpe: scala.reflect.runtime.universe.Type = scala.reflect.runtime.universe.weakTypeOf[T]
    def <:<(that: ScalaType[_]): Boolean = tpe <:< that.tpe
    def >:>(that: ScalaType[_]): Boolean = that.tpe <:< tpe
  }

  /*
   private val primitiveTypes = Set[Type](c.typeOf[Number], c.typeOf[Date], c.typeOf[Timestamp])

   */

  implicit val String: ScalaType[String] = pmst[String]("scala.String", "String")
  implicit val Symbol: ScalaType[Symbol] = pmst[Symbol]("scala.Symbol", "Symbol")
  implicit val Boolean: ScalaType[Boolean] = pmst[Boolean]("scala.Boolean", "Boolean")
  implicit val Byte: ScalaType[Byte] = pmst[Byte]("scala.Byte", "Byte")
  implicit val Short: ScalaType[Short] = pmst[Short]("scala.Short", "Short")
  implicit val Int: ScalaType[Int] = pmst[Int]("scala.Int", "Int")
  implicit val Long: ScalaType[Long] = pmst[Long]("scala.Long", "Long")
  implicit val BigInt: ScalaType[BigInt] = pmst[BigInt]("scala.math.BigInt", "BigInt")
  implicit val Float: ScalaType[Float] = pmst[Float]("scala.Float", "Float")
  implicit val Double: ScalaType[Double] = pmst[Double]("scala.Double", "Double")
  implicit val BigDecimal: ScalaType[BigDecimal] = pmst[BigDecimal]("scala.math.BigDecimal", "BigDecimal")
  implicit val JByte: ScalaType[java.lang.Byte] = pmst[java.lang.Byte]("java.lang.Byte", "JByte")
  implicit val JShort: ScalaType[java.lang.Short] = pmst[java.lang.Short]("java.lang.Short", "JShort")
  implicit val JInt: ScalaType[java.lang.Integer] = pmst[java.lang.Integer]("java.lang.Integer", "JInteger")
  implicit val JLong: ScalaType[java.lang.Long] = pmst[java.lang.Long]("java.lang.Long", "JLong")
  implicit val JBigInt: ScalaType[java.math.BigInteger] = pmst[java.math.BigInteger]("java.math.BigInteger", "JBigInteger")
  implicit val JFloat: ScalaType[java.lang.Float] = pmst[java.lang.Float]("java.lang.Float", "JFloat")
  implicit val JDouble: ScalaType[java.lang.Double] = pmst[java.lang.Double]("java.lang.Double", "JDouble")
  implicit val JBigDecimal: ScalaType[java.math.BigDecimal] = pmst[java.math.BigDecimal]("java.math.BigDecimal", "JBigDecimal")
  implicit val JBoolean: ScalaType[java.lang.Boolean] = pmst[java.lang.Boolean]("java.lang.Boolean", "JBoolean")

//
//  def scalaTypeFromString(fqn: String) = macro scalaTypeFromStringImpl
//  def scalaTypeFromStringImpl(c: Context)(fqn: c.Expr[String]) = {
//    import c.universe._
//
//    val helper = new Helper[c.type](c)
//
//    def newScalaTypeFromType(tpe: c.type#Type): c.Expr[ScalaType[_]] = {
//      val ttpe = tpe.normalize
//      val rfn = ttpe.typeSymbol.fullName
//      val sn = ttpe.typeSymbol.name.decodedName.toString.trim
//      val rsn = ttpe.typeSymbol.name.encodedName.toString.trim
//      val ip = helper.isPrimitive(ttpe)
//      val im = helper.isMap(ttpe)
//      val ic = helper.isCollection(ttpe)
//      val io = helper.isOption(ttpe)
//      val ie = helper.isEither(ttpe)
//      val ia = helper.isArray(ttpe)
//      val ta = ttpe match {
//        case TypeRef(_, _, typeArgs@_ :: _) =>
//          val nvn = c.fresh("st$")
//          val nvt = newTermName(nvn)
//          val r: List[c.Tree] = typeArgs map { ta =>
//            val t = appliedType(weakTypeOf[ScalaType[Any]].typeConstructor, ta :: Nil)
//            c.inferImplicitValue(t) match {
//              case EmptyTree =>
//                c.abort(c.enclosingPosition, s"Couldn't find a org.json4s.macros.ScalaType[${t.typeSymbol.name.decodedName.toString}], try bringing an implicit value for ${tpe.typeSymbol.name.decodedName.toString} in scope by importing one or defining one.")
//              case resolved =>
//                q"$nvt += $resolved"
//            }
//          }
//
//          val vb: c.Tree =
//            q"""
//val $nvt = Vector.newBuilder[org.json4s.reflect.ScalaType[_]]
//             """
//          Block(vb :: r, q"$nvt.result()")
//        case _ => reify {
//          Vector.empty[ScalaType[_]]
//        }.tree
//      }
//
//      reify {
//        new ScalaType[c.Expr[Type]] {
//          val fullName: String = c.Expr[String](Literal(Constant(rfn))).splice
//          val rawFullName: String = c.Expr[String](Literal(Constant(rfn))).splice
//          val simpleName: String = c.Expr[String](Literal(Constant(sn))).splice
//          val rawSimpleName: String = c.Expr[String](Literal(Constant(rsn))).splice
//          val isPrimitive: Boolean = c.Expr[Boolean](Literal(Constant(ip))).splice
//          val isMap: Boolean = c.Expr[Boolean](Literal(Constant(im))).splice
//          val isCollection: Boolean = c.Expr[Boolean](Literal(Constant(ic))).splice
//          val isOption: Boolean = c.Expr[Boolean](Literal(Constant(io))).splice
//          val isEither: Boolean = c.Expr[Boolean](Literal(Constant(ie))).splice
//          val isArray: Boolean = c.Expr[Boolean](Literal(Constant(ia))).splice
//          val typeArgs: Seq[ScalaTypeBase] = c.Expr[Seq[ScalaTypeBase]](ta).splice
//          lazy val tpe: scala.reflect.runtime.universe.Type = scala.reflect.runtime.universe.weakTypeOf[T]
//          def <:<(that: ScalaType[_]): Boolean = tpe <:< that.tpe
//          def >:>(that: ScalaType[_]): Boolean = that.tpe <:< tpe
//          def typeInfo: TypeInfo = null
//        }
//      }
//    }
//    val klass = c.mirror.staticClass(fqn.splice)
//    newScalaTypeFromType(klass.toType)
//  }

  implicit def scalaTypeOf[T]: ScalaType[T] = macro scalaTypeImpl[T]

  def scalaTypeImpl[T: c.WeakTypeTag](c: Context): c.Expr[ScalaType[T]] = {
    import c.universe._
    val helper = new Helper[c.type](c)

    def newScalaTypeFromType(tpe: c.type#Type): c.Expr[ScalaType[T]] = {
      val ttpe = tpe.normalize
      val rfn = ttpe.typeSymbol.fullName
      val sn = ttpe.typeSymbol.name.decodedName.toString.trim
      val rsn = ttpe.typeSymbol.name.encodedName.toString.trim
      val ip = helper.isPrimitive(ttpe)
      val im = helper.isMap(ttpe)
      val ic = helper.isCollection(ttpe)
      val io = helper.isOption(ttpe)
      val ie = helper.isEither(ttpe)
      val ia = helper.isArray(ttpe)
      val ta = ttpe match {
        case TypeRef(_, _, typeArgs@_ :: _) =>
          val nvn = c.fresh("st$")
          val nvt = newTermName(nvn)
          val r: List[c.Tree] = typeArgs map { ta =>
            val t = appliedType(weakTypeOf[ScalaType[Any]].typeConstructor, ta :: Nil)
            c.inferImplicitValue(t) match {
              case EmptyTree =>
                c.abort(c.enclosingPosition, s"Couldn't find a org.json4s.macros.ScalaType[${t.typeSymbol.name.decodedName.toString}], try bringing an implicit value for ${tpe.typeSymbol.name.decodedName.toString} in scope by importing one or defining one.")
              case resolved =>
                q"$nvt += $resolved"
            }
          }

          val vb: c.Tree =
            q"""
val $nvt = Vector.newBuilder[org.json4s.reflect.ScalaType[_]]
             """
          Block(vb :: r, q"$nvt.result()")
        case _ => reify {
          Vector.empty[ScalaType[_]]
        }.tree
      }

      reify {
        new ScalaType[T] {
          val fullName: String = c.Expr[String](Literal(Constant(rfn))).splice
          val rawFullName: String = c.Expr[String](Literal(Constant(rfn))).splice
          val simpleName: String = c.Expr[String](Literal(Constant(sn))).splice
          val rawSimpleName: String = c.Expr[String](Literal(Constant(rsn))).splice
          val isPrimitive: Boolean = c.Expr[Boolean](Literal(Constant(ip))).splice
          val isMap: Boolean = c.Expr[Boolean](Literal(Constant(im))).splice
          val isCollection: Boolean = c.Expr[Boolean](Literal(Constant(ic))).splice
          val isOption: Boolean = c.Expr[Boolean](Literal(Constant(io))).splice
          val isEither: Boolean = c.Expr[Boolean](Literal(Constant(ie))).splice
          val isArray: Boolean = c.Expr[Boolean](Literal(Constant(ia))).splice
          val typeArgs: Seq[ScalaTypeBase] = c.Expr[Seq[ScalaTypeBase]](ta).splice
          lazy val tpe: scala.reflect.runtime.universe.Type = scala.reflect.runtime.universe.weakTypeOf[T]
          def <:<(that: ScalaType[_]): Boolean = tpe <:< that.tpe
          def >:>(that: ScalaType[_]): Boolean = that.tpe <:< tpe
          def typeInfo: TypeInfo = null
        }
      }
    }


    newScalaTypeFromType(c.weakTypeOf[T])
  }

}