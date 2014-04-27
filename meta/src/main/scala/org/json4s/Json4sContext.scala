package org.json4s

import org.json4s.reflect.{Reflector, ScalaType}
import org.json4s._
import org.json4s.JsonAST.{JArray, JValue, JObject}
//
//object Json4sContext {
//  /** Type hints can be used to alter the default conversion rules when converting
//   * Scala instances into JSON and vice versa. Type hints must be used when converting
//   * class which is not supported by default (for instance when class is not a case class).
//   * <p>
//   * Example:<pre>
//   * class DateTime(val time: Long)
//   *
//   * val hints = new ShortTypeHints(classOf[DateTime] :: Nil) {
//   *   override def serialize: PartialFunction[Any, JObject] = {
//   *     case t: DateTime => JObject(JField("t", JInt(t.time)) :: Nil)
//   *   }
//   *
//   *   override def deserialize: PartialFunction[(String, JObject), Any] = {
//   *     case ("DateTime", JObject(JField("t", JInt(t)) :: Nil)) => new DateTime(t.longValue)
//   *   }
//   * }
//   * implicit val formats = DefaultFormats.withHints(hints)
//   * </pre>
//   */
//  trait TypeHints {
//
//    def hints: Seq[ScalaType[_]]
//
//    /** Return hint for given type.
//     */
//    def hintFor(clazz: ScalaType[_]): String
//
//    /** Return type for given hint.
//     */
//    def classFor(hint: String): Option[ScalaType[_]]
//
//    @deprecated("Use `containsHint` without `_?` instead", "3.2.0")
//    def containsHint_?(clazz: ScalaType[_]): Boolean = containsHint(clazz)
//    def containsHint(clazz: ScalaType[_]): Boolean = hints exists (_ <:< clazz)  // TODO: actually implement
//    def deserialize: PartialFunction[(String, JObject), Any] = Map()
//    def serialize: PartialFunction[Any, JObject] = Map()
//
//    def components: List[TypeHints] = List(this)
//
//
//
//    /**
//     * Adds the specified type hints to this type hints.
//     */
//    def + (hints: TypeHints): TypeHints = CompositeTypeHints(hints.components ::: components)
//
//    private[TypeHints] case class CompositeTypeHints(override val components: List[TypeHints]) extends TypeHints {
//      val hints: Seq[ScalaType[_]] = components.flatMap(_.hints)
//
//      /**
//       * Chooses most specific class.
//       */
//      def hintFor(clazz: ScalaType[_]): String = {
//        (components.reverse
//          filter (_.containsHint(clazz))
//          map (th => (th.hintFor(clazz), th.classFor(th.hintFor(clazz)).getOrElse(sys.error("hintFor/classFor not invertible for " + th))))
//          sortWith ((x, y) => (delta(x._2, clazz) - delta(y._2, clazz)) <= 0)).head._1
//      }
//
//      def classFor(hint: String): Option[Class[_]] = {
//        def hasClass(h: TypeHints) =
//          scala.util.control.Exception.allCatch opt (h.classFor(hint)) map (_.isDefined) getOrElse(false)
//
//        components find (hasClass) flatMap (_.classFor(hint))
//    }
//
//      override def deserialize: PartialFunction[(String, JObject), Any] = components.foldLeft[PartialFunction[(String, JObject),Any]](Map()) {
//        (result, cur) => result.orElse(cur.deserialize)
//      }
//
//      override def serialize: PartialFunction[Any, JObject] = components.foldLeft[PartialFunction[Any, JObject]](Map()) {
//        (result, cur) => result.orElse(cur.serialize)
//      }
//    }
//  }
//
//  private[json4s] object ClassDelta {
//    def delta(class1: Class[_], class2: Class[_]): Int = {
//      if (class1 == class2) 0
//      else if (class1.getInterfaces.contains(class2)) 0
//      else if (class2.getInterfaces.contains(class1)) 0
//      else if (class1.isAssignableFrom(class2)) {
//        1 + delta(class1, class2.getSuperclass)
//      }
//      else if (class2.isAssignableFrom(class1)) {
//        1 + delta(class1.getSuperclass, class2)
//      }
//      else sys.error("Don't call delta unless one class is assignable from the other")
//    }
//  }
//
//  /** Do not use any type hints.
//   */
//  case object NoTypeHints extends TypeHints {
//    val hints: Seq[ScalaType[_]] = Vector.empty[ScalaType[_]]
//    def hintFor(clazz: ScalaType[_]) = sys.error("NoTypeHints does not provide any type hints.")
//    def classFor(hint: String) = None
//  }
//
//  /** Use short class name as a type hint.
//   */
//  case class ShortTypeHints(hints: Seq[ScalaType[_]]) extends TypeHints {
//    def hintFor(clazz: ScalaType[_]) = clazz.simpleName
//    def classFor(hint: String) = hints find (hintFor(_) == hint)
//  }
//
////  /** Use full class name as a type hint.
////   */
////  case class FullTypeHints(hints:Seq[ScalaType[_]]) extends TypeHints {
////    def hintFor(clazz: ScalaType[_]) = clazz.fullName
////    def classFor(hint: String) = {
////      Reflector.scalaTypeOf(hint).map(_.erasure)//.find(h => hints.exists(l => l.isAssignableFrom(h.erasure)))
////    }
////  }
//
//}
//trait Json4sContext { self: Json4sContext =>
//  import Json4sContext._
////  def dateFormat: DateFormat
//  def typeHints: TypeHints = NoTypeHints
////  def customSerializers: List[Serializer[_]] = Nil
////  def fieldSerializers: List[(Class[_], FieldSerializer[_])] = Nil
//  def wantsBigDecimal: Boolean = false
//  def primitives: Set[ScalaType[_]] = Set(Reflector.scalaTypeOf[JValue], Reflector.scalaTypeOf[JObject], Reflector.scalaTypeOf[JArray])
//  def companions: List[(Class[_], AnyRef)] = Nil
//  def strict: Boolean = false
//
//  /**
//   * The name of the field in JSON where type hints are added (jsonClass by default)
//   */
//  def typeHintFieldName: String = "jsonClass"
//
////  def withBigDecimal: Formats = new Formats {
////    val dateFormat: DateFormat = self.dateFormat
////    override val typeHintFieldName: String = self.typeHintFieldName
////    override val parameterNameReader: reflect.ParameterNameReader = self.parameterNameReader
////    override val typeHints: TypeHints = self.typeHints
////    override val customSerializers: List[Serializer[_]] = self.customSerializers
////    override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = self.fieldSerializers
////    override val wantsBigDecimal: Boolean = true
////    override val primitives: Set[Type] = self.primitives
////    override val companions: List[(Class[_], AnyRef)] = self.companions
////    override val strict: Boolean = self.strict
////  }
////
////  def withDouble: Formats = new Formats {
////    val dateFormat: DateFormat = self.dateFormat
////    override val typeHintFieldName: String = self.typeHintFieldName
////    override val parameterNameReader: reflect.ParameterNameReader = self.parameterNameReader
////    override val typeHints: TypeHints = self.typeHints
////    override val customSerializers: List[Serializer[_]] = self.customSerializers
////    override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = self.fieldSerializers
////    override val wantsBigDecimal: Boolean = false
////    override val primitives: Set[Type] = self.primitives
////    override val companions: List[(Class[_], AnyRef)] = self.companions
////    override val strict: Boolean = self.strict
////  }
////
////  def withCompanions(comps: (Class[_], AnyRef)*): Formats = {
////    new Formats {
////      val dateFormat: DateFormat = self.dateFormat
////      override val typeHintFieldName: String = self.typeHintFieldName
////      override val parameterNameReader: reflect.ParameterNameReader = self.parameterNameReader
////      override val typeHints: TypeHints = self.typeHints
////      override val customSerializers: List[Serializer[_]] = self.customSerializers
////      override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = self.fieldSerializers
////      override val wantsBigDecimal: Boolean = self.wantsBigDecimal
////      override val primitives: Set[Type] = self.primitives
////      override val companions: List[(Class[_], AnyRef)] = comps.toList ::: self.companions
////      override val strict: Boolean = self.strict
////    }
////  }
////
////  /**
////   * Adds the specified type hints to this formats.
////   */
////  def + (extraHints: TypeHints): Formats = new Formats {
////    val dateFormat: DateFormat = self.dateFormat
////    override val typeHintFieldName: String = self.typeHintFieldName
////    override val parameterNameReader: reflect.ParameterNameReader = self.parameterNameReader
////    override val typeHints: TypeHints = self.typeHints + extraHints
////    override val customSerializers: List[Serializer[_]] = self.customSerializers
////    override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = self.fieldSerializers
////    override val wantsBigDecimal: Boolean = self.wantsBigDecimal
////    override val primitives: Set[Type] = self.primitives
////    override val companions: List[(Class[_], AnyRef)] = self.companions
////    override val strict: Boolean = self.strict
////
////  }
////
////  /**
////   * Adds the specified custom serializer to this formats.
////   */
////  def + (newSerializer: Serializer[_]): Formats = new Formats {
////    val dateFormat: DateFormat = self.dateFormat
////    override val typeHintFieldName: String = self.typeHintFieldName
////    override val parameterNameReader: reflect.ParameterNameReader = self.parameterNameReader
////    override val typeHints: TypeHints = self.typeHints
////    override val customSerializers: List[Serializer[_]] = newSerializer :: self.customSerializers
////    override val fieldSerializers: List[(Class[_], FieldSerializer[_])] = self.fieldSerializers
////    override val wantsBigDecimal: Boolean = self.wantsBigDecimal
////    override val primitives: Set[Type] = self.primitives
////    override val companions: List[(Class[_], AnyRef)] = self.companions
////    override val strict: Boolean = self.strict
////  }
////
////  /**
////   * Adds the specified custom serializers to this formats.
////   */
////  def ++ (newSerializers: Traversable[Serializer[_]]): Formats =
////    newSerializers.foldLeft(this)(_ + _)
////
////  /**
////   * Adds a field serializer for a given type to this formats.
////   */
////  def + [A](newSerializer: FieldSerializer[A]): Formats = new Formats {
////    val dateFormat: DateFormat = self.dateFormat
////    override val typeHintFieldName: String = self.typeHintFieldName
////    override val parameterNameReader: reflect.ParameterNameReader = self.parameterNameReader
////    override val typeHints: TypeHints = self.typeHints
////    override val customSerializers: List[Serializer[_]] = self.customSerializers
////    override val fieldSerializers: List[(Class[_], FieldSerializer[_])] =
////      (newSerializer.mf.erasure -> newSerializer) :: self.fieldSerializers
////    override val wantsBigDecimal: Boolean = self.wantsBigDecimal
////    override val primitives: Set[Type] = self.primitives
////    override val companions: List[(Class[_], AnyRef)] = self.companions
////    override val strict: Boolean = self.strict
////  }
////
////  private[json4s] def fieldSerializer(clazz: Class[_]): Option[FieldSerializer[_]] = {
////    import ClassDelta._
////
////    val ord = Ordering[Int].on[(Class[_], FieldSerializer[_])](x => delta(x._1, clazz))
////    fieldSerializers filter (_._1.isAssignableFrom(clazz)) match {
////      case Nil => None
////      case xs  => Some((xs min ord)._2)
////    }
////  }
////
////  def customSerializer(implicit format: Formats) =
////    customSerializers.foldLeft(Map(): PartialFunction[Any, JValue]) { (acc, x) =>
////      acc.orElse(x.serialize)
////    }
////
////  def customDeserializer(implicit format: Formats) =
////    customSerializers.foldLeft(Map(): PartialFunction[(TypeInfo, JValue), Any]) { (acc, x) =>
////      acc.orElse(x.deserialize)
////    }
//}