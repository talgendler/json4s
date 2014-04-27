package org.json4s

import org.specs2.mutable.Specification
import org.json4s.reflect.{Reflector, ScalaType}

object MetaScalaTypeSpec {

  class Foo {

    class Bar {

    }

  }

}

class MetaScalaTypeSpec extends Specification {
  "A MetaScalaType" should {
    "get the raw full name of a class" in {
      val t = Reflector.scalaTypeOf[MetaScalaTypeSpec.Foo#Bar]
      t.rawFullName must_== classOf[MetaScalaTypeSpec.Foo#Bar].getName.replace("$", ".")
    }
    "get the full name of a class" in {
      val t = Reflector.scalaTypeOf[MetaScalaTypeSpec.Foo#Bar]
      t.fullName must_== classOf[MetaScalaTypeSpec.Foo#Bar].getName.replace("$", ".")
    }
    "get the simple name of a class" in {
      val t = Reflector.scalaTypeOf[MetaScalaTypeSpec.Foo#Bar]
      t.simpleName must_== classOf[MetaScalaTypeSpec.Foo#Bar].getSimpleName
    }
    "get the raw simple name of a class" in {
      val t = Reflector.scalaTypeOf[MetaScalaTypeSpec.Foo#Bar]
      t.rawSimpleName must_== classOf[MetaScalaTypeSpec.Foo#Bar].getSimpleName
    }
    "detect a scala.Byte as primitive" in {
      val t = Reflector.scalaTypeOf[Byte]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.Short as primitive" in {
      val t = Reflector.scalaTypeOf[Short]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.Int as primitive" in {
      val t = Reflector.scalaTypeOf[Int]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.Long as primitive" in {
      val t = Reflector.scalaTypeOf[Long]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.BigInt as primitive" in {
      val t = Reflector.scalaTypeOf[BigInt]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.Float as primitive" in {
      val t = Reflector.scalaTypeOf[Float]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.Double as primitive" in {
      val t = Reflector.scalaTypeOf[Double]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.BigDecimal as primitive" in {
      val t = Reflector.scalaTypeOf[BigDecimal]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.lang.Byte as primitive" in {
      val t = Reflector.scalaTypeOf[java.lang.Byte]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.lang.Short as primitive" in {
      val t = Reflector.scalaTypeOf[java.lang.Short]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.lang.Integer as primitive" in {
      val t = Reflector.scalaTypeOf[java.lang.Integer]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.lang.Long as primitive" in {
      val t = Reflector.scalaTypeOf[java.lang.Long]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.math.BigInteger as primitive" in {
      val t = Reflector.scalaTypeOf[java.math.BigInteger]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.lang.Float as primitive" in {
      val t = Reflector.scalaTypeOf[java.lang.Float]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.lang.Double as primitive" in {
      val t = Reflector.scalaTypeOf[java.lang.Double]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a java.math.BigDecimal as primitive" in {
      val t = Reflector.scalaTypeOf[java.math.BigDecimal]
      t.isPrimitive must beTrue
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect an option" in {
      val t = Reflector.scalaTypeOf[Option[Int]]
      t.isPrimitive must beFalse
      t.isOption must beTrue
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect an either" in {
      val t = Reflector.scalaTypeOf[Either[String, Int]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beTrue
      t.isMap must beFalse
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a Map as map" in {
      val t = Reflector.scalaTypeOf[Map[String, Int]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beTrue
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.collection.immutable.Map as map" in {
      val t = Reflector.scalaTypeOf[scala.collection.immutable.Map[String, Int]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beTrue
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.collection.Map as map" in {
      val t = Reflector.scalaTypeOf[scala.collection.Map[String, Int]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beTrue
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.collection.mutable.Map as map" in {
      val t = Reflector.scalaTypeOf[scala.collection.mutable.Map[String, Int]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beTrue
      t.isCollection must beFalse
      t.isArray must beFalse
    }
    "detect a scala.collection.mutable.Buffer as collection" in {
      val t = Reflector.scalaTypeOf[scala.collection.mutable.Buffer[String]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beTrue
      t.isArray must beFalse
    }
    "detect a scala.collection.immutable.List as collection" in {
      val t = Reflector.scalaTypeOf[scala.collection.immutable.List[String]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beTrue
      t.isArray must beFalse
    }
    "detect a scala.collection.mutable.Set as collection" in {
      val t = Reflector.scalaTypeOf[scala.collection.mutable.Set[String]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beTrue
      t.isArray must beFalse
    }
    "detect a scala.collection.immutable.Set as collection" in {
      val t = Reflector.scalaTypeOf[scala.collection.immutable.Set[String]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beTrue
      t.isArray must beFalse
    }
    "detect a scala.Array as collection and as array" in {
      val t = ScalaType.scalaTypeOf[Array[String]]
      t.isPrimitive must beFalse
      t.isOption must beFalse
      t.isEither must beFalse
      t.isMap must beFalse
      t.isCollection must beTrue
      t.isArray must beTrue
    }

  }
}