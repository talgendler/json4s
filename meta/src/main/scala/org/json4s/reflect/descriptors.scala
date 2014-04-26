package org.json4s.reflect

import java.lang.reflect.{Constructor => JConstructor, Field, TypeVariable}
import scala._
import org.json4s.JsonAST.{JValue, JObject, JArray}

sealed trait Descriptor
object DefaultScalaType {

  private val types = new Memo[Manifest[_], DefaultScalaType]

  def apply[T](mf: Manifest[T]): DefaultScalaType = {
    /* optimization */
    if (mf.runtimeClass == classOf[Int] || mf.runtimeClass == classOf[java.lang.Integer]) DefaultScalaType.IntType
    else if (mf.runtimeClass == classOf[Long] || mf.runtimeClass == classOf[java.lang.Long]) DefaultScalaType.LongType
    else if (mf.runtimeClass == classOf[Byte] || mf.runtimeClass == classOf[java.lang.Byte]) DefaultScalaType.ByteType
    else if (mf.runtimeClass == classOf[Short] || mf.runtimeClass == classOf[java.lang.Short]) DefaultScalaType.ShortType
    else if (mf.runtimeClass == classOf[Float] || mf.runtimeClass == classOf[java.lang.Float]) DefaultScalaType.FloatType
    else if (mf.runtimeClass == classOf[Double] || mf.runtimeClass == classOf[java.lang.Double]) DefaultScalaType.DoubleType
    else if (mf.runtimeClass == classOf[BigInt] || mf.runtimeClass == classOf[java.math.BigInteger]) DefaultScalaType.BigIntType
    else if (mf.runtimeClass == classOf[BigDecimal] || mf.runtimeClass == classOf[java.math.BigDecimal]) DefaultScalaType.BigDecimalType
    else if (mf.runtimeClass == classOf[Boolean] || mf.runtimeClass == classOf[java.lang.Boolean]) DefaultScalaType.BooleanType
    else if (mf.runtimeClass == classOf[String] || mf.runtimeClass == classOf[java.lang.String]) DefaultScalaType.StringType
    else if (mf.runtimeClass == classOf[java.util.Date]) DefaultScalaType.DateType
    else if (mf.runtimeClass == classOf[java.sql.Timestamp]) DefaultScalaType.TimestampType
    else if (mf.runtimeClass == classOf[Symbol]) DefaultScalaType.SymbolType
    else if (mf.runtimeClass == classOf[Number]) DefaultScalaType.NumberType
    else if (mf.runtimeClass == classOf[JObject]) DefaultScalaType.JObjectType
    else if (mf.runtimeClass == classOf[JArray]) DefaultScalaType.JArrayType
    else if (mf.runtimeClass == classOf[JValue]) DefaultScalaType.JValueType
    /* end optimization */
    else {
      if (mf.typeArguments.isEmpty) types(mf, new DefaultScalaType(_))
      else new DefaultScalaType(mf)
    }
  }

  def apply(runtimeClass: Class[_], typeArgs: Seq[DefaultScalaType] = Seq.empty): DefaultScalaType = {
    val mf = ManifestFactory.manifestOf(runtimeClass, typeArgs.map(_.manifest))
    DefaultScalaType(mf)
  }

  def apply(target: TypeInfo): DefaultScalaType = {
    target match {
      case t: TypeInfo with SourceType => t.scalaType
      case t =>
        val tArgs = t.parameterizedType.map(_.getActualTypeArguments.toList.map(Reflector.scalaTypeOf(_))).getOrElse(Nil)
        DefaultScalaType(target.clazz, tArgs)
    }
  }

  // Deal with the most common cases as an optimization
  /* optimization */
  private val IntType: DefaultScalaType = new PrimitiveScalaType(Manifest.Int)
  private val NumberType: DefaultScalaType = new PrimitiveScalaType(manifest[Number])
  private val LongType: DefaultScalaType = new PrimitiveScalaType(Manifest.Long)
  private val ByteType: DefaultScalaType = new PrimitiveScalaType(Manifest.Byte)
  private val ShortType: DefaultScalaType = new PrimitiveScalaType(Manifest.Short)
  private val BooleanType: DefaultScalaType = new PrimitiveScalaType(Manifest.Boolean)
  private val FloatType: DefaultScalaType = new PrimitiveScalaType(Manifest.Float)
  private val DoubleType: DefaultScalaType = new PrimitiveScalaType(Manifest.Double)
  private val StringType: DefaultScalaType = new PrimitiveScalaType(manifest[java.lang.String])
  private val SymbolType: DefaultScalaType = new PrimitiveScalaType(manifest[Symbol])
  private val BigDecimalType: DefaultScalaType = new PrimitiveScalaType(manifest[BigDecimal])
  private val BigIntType: DefaultScalaType = new PrimitiveScalaType(manifest[BigInt])
  private val JValueType: DefaultScalaType = new PrimitiveScalaType(manifest[JValue])
  private val JObjectType: DefaultScalaType = new PrimitiveScalaType(manifest[JObject])
  private val JArrayType: DefaultScalaType = new PrimitiveScalaType(manifest[JArray])
  private val DateType: DefaultScalaType = new PrimitiveScalaType(manifest[java.util.Date])
  private val TimestampType: DefaultScalaType = new PrimitiveScalaType(manifest[java.sql.Timestamp])

  private class PrimitiveScalaType(mf: Manifest[_]) extends DefaultScalaType(mf) {
    override val isPrimitive = true
  }
  private class CopiedScalaType(
                  mf: Manifest[_],
                  private[this] var _typeVars: Map[TypeVariable[_], DefaultScalaType],
                  override val isPrimitive: Boolean) extends DefaultScalaType(mf) {

    override def typeVars: Map[TypeVariable[_], DefaultScalaType] = {
      if (_typeVars == null)
        _typeVars = Map.empty ++
          erasure.getTypeParameters.map(_.asInstanceOf[TypeVariable[_]]).zip(typeArgs)
      _typeVars
    }
  }
  /* end optimization */
}
class DefaultScalaType(private val manifest: Manifest[_]) extends ScalaType with Equals {

  import DefaultScalaType.{ types, CopiedScalaType }
  val erasure: Class[_] = manifest.runtimeClass

  val typeArgs: Seq[DefaultScalaType] = manifest.typeArguments.map(ta => Reflector.scalaTypeOf(ta)) ++ (
    if (erasure.isArray) List(Reflector.scalaTypeOf(erasure.getComponentType)) else Nil
  )

  private[this] var _typeVars: Map[TypeVariable[_], DefaultScalaType] = null
  def typeVars: Map[TypeVariable[_], DefaultScalaType] = {
    if (_typeVars == null)
      _typeVars = Map.empty ++
        erasure.getTypeParameters.map(_.asInstanceOf[TypeVariable[_]]).zip(typeArgs)
    _typeVars
  }


  val isArray: Boolean = erasure.isArray

  private[this] var _rawFullName: String = null
  def rawFullName: String = {
    if (_rawFullName == null)
      _rawFullName = erasure.getName
    _rawFullName
  }

  private[this] var _rawSimpleName: String = null
  def rawSimpleName: String = {
    if (_rawSimpleName == null) {
      _rawSimpleName = safeSimpleName(erasure)
    }
    _rawSimpleName
  }

  lazy val simpleName: String =
    rawSimpleName + (if (typeArgs.nonEmpty) typeArgs.map(_.simpleName).mkString("[", ", ", "]") else (if (typeVars.nonEmpty) typeVars.map(_._2.simpleName).mkString("[", ", ", "]") else ""))

  lazy val fullName: String =
    rawFullName + (if (typeArgs.nonEmpty) typeArgs.map(_.fullName).mkString("[", ", ", "]") else "")

  lazy val typeInfo: TypeInfo =
    new TypeInfo(
      erasure,
      if (typeArgs.nonEmpty) Some(Reflector.mkParameterizedType(erasure, typeArgs.map(_.erasure).toSeq)) else None
    ) with SourceType {
      val scalaType: DefaultScalaType = DefaultScalaType.this
    }

  val isPrimitive = false

  def isMap = classOf[collection.immutable.Map[_, _]].isAssignableFrom(erasure) || classOf[collection.Map[_, _]].isAssignableFrom(erasure)
  def isCollection = erasure.isArray || classOf[Iterable[_]].isAssignableFrom(erasure) || classOf[java.util.Collection[_]].isAssignableFrom(erasure)
  def isOption = classOf[Option[_]].isAssignableFrom(erasure)
  def isEither = classOf[Either[_, _]].isAssignableFrom(erasure)
  def <:<(that: DefaultScalaType): Boolean = manifest <:< that.manifest
  def >:>(that: DefaultScalaType): Boolean = manifest >:> that.manifest

  override def hashCode(): Int = manifest.##

  override def equals(obj: Any): Boolean = obj match {
    case a: DefaultScalaType => manifest == a.manifest
    case _ => false
  }

  def canEqual(that: Any): Boolean = that match {
    case s: DefaultScalaType => manifest.canEqual(s.manifest)
    case _ => false
  }

  def copy(erasure: Class[_] = erasure, typeArgs: Seq[DefaultScalaType] = typeArgs, typeVars: Map[TypeVariable[_], DefaultScalaType] = _typeVars): DefaultScalaType = {
    /* optimization */
    if (erasure == classOf[Int] || erasure == classOf[java.lang.Integer]) DefaultScalaType.IntType
    else if (erasure == classOf[Long] || erasure == classOf[java.lang.Long]) DefaultScalaType.LongType
    else if (erasure == classOf[Byte] || erasure == classOf[java.lang.Byte]) DefaultScalaType.ByteType
    else if (erasure == classOf[Short] || erasure == classOf[java.lang.Short]) DefaultScalaType.ShortType
    else if (erasure == classOf[Float] || erasure == classOf[java.lang.Float]) DefaultScalaType.FloatType
    else if (erasure == classOf[Double] || erasure == classOf[java.lang.Double]) DefaultScalaType.DoubleType
    else if (erasure == classOf[BigInt] || erasure == classOf[java.math.BigInteger]) DefaultScalaType.BigIntType
    else if (erasure == classOf[BigDecimal] || erasure == classOf[java.math.BigDecimal]) DefaultScalaType.BigDecimalType
    else if (erasure == classOf[Boolean] || erasure == classOf[java.lang.Boolean]) DefaultScalaType.BooleanType
    else if (erasure == classOf[String] || erasure == classOf[java.lang.String]) DefaultScalaType.StringType
    else if (erasure == classOf[java.util.Date]) DefaultScalaType.DateType
    else if (erasure == classOf[java.sql.Timestamp]) DefaultScalaType.TimestampType
    else if (erasure == classOf[Symbol]) DefaultScalaType.SymbolType
    else if (erasure == classOf[Number]) DefaultScalaType.NumberType
    else if (erasure == classOf[JObject]) DefaultScalaType.JObjectType
    else if (erasure == classOf[JArray]) DefaultScalaType.JArrayType
    else if (erasure == classOf[JValue]) DefaultScalaType.JValueType
    /* end optimization */
    else {
      val mf = ManifestFactory.manifestOf(erasure, typeArgs.map(_.manifest))
      val st = new CopiedScalaType(mf, typeVars, isPrimitive)
      if (typeArgs.isEmpty) types.replace(mf, st)
      else st
    }
  }

  override def toString: String = simpleName
}
case class PropertyDescriptor(name: String, mangledName: String, returnType: DefaultScalaType, field: Field) extends Descriptor {
  def set(receiver: Any, value: Any) = field.set(receiver, value)
  def get(receiver: AnyRef) = field.get(receiver)
}
case class ConstructorParamDescriptor(name: String, mangledName: String, argIndex: Int, argType: DefaultScalaType, defaultValue: Option[() => Any]) extends Descriptor {
  lazy val isOptional = defaultValue.isDefined || argType.isOption
}
case class ConstructorDescriptor(params: Seq[ConstructorParamDescriptor], constructor: java.lang.reflect.Constructor[_], isPrimary: Boolean) extends Descriptor
case class SingletonDescriptor(simpleName: String, fullName: String, erasure: DefaultScalaType, instance: AnyRef, properties: Seq[PropertyDescriptor]) extends Descriptor

sealed trait ObjectDescriptor extends Descriptor
case class ClassDescriptor(simpleName: String, fullName: String, erasure: DefaultScalaType, companion: Option[SingletonDescriptor], constructors: Seq[ConstructorDescriptor], properties: Seq[PropertyDescriptor]) extends ObjectDescriptor {

  def bestMatching(argNames: List[String]): Option[ConstructorDescriptor] = {
    val names = Set(argNames: _*)
    def countOptionals(args: List[ConstructorParamDescriptor]) =
      args.foldLeft(0)((n, x) => if (x.isOptional) n+1 else n)
    def score(args: List[ConstructorParamDescriptor]) =
      args.foldLeft(0)((s, arg) => if (names.contains(arg.name)) s+1 else -100)

    if (constructors.isEmpty) None
    else {
      val best = constructors.tail.foldLeft((constructors.head, score(constructors.head.params.toList))) { (best, c) =>
        val newScore = score(c.params.toList)
        val newIsBetter = {
          (newScore == best._2 && countOptionals(c.params.toList) < countOptionals(best._1.params.toList)) ||
            newScore > best._2
        }
        if (newIsBetter) (c, newScore) else best
      }

      Some(best._1)
    }
  }

  private[this] var _mostComprehensive: Seq[ConstructorParamDescriptor] = null
  def mostComprehensive: Seq[ConstructorParamDescriptor] = {
    if (_mostComprehensive == null)
      _mostComprehensive =
        if (constructors.nonEmpty) constructors.sortBy(-_.params.size).headOption.map(_.params).getOrElse(Nil)
        else Nil

    _mostComprehensive
  }
}

case class PrimitiveDescriptor(erasure: DefaultScalaType, default: Option[() => Any] = None) extends ObjectDescriptor

