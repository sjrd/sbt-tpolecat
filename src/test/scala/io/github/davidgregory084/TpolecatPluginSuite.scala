package io.github.davidgregory084

import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.scalacheck.Gen
import org.scalacheck.Prop._

import TpolecatPlugin._
import TpolecatPlugin.autoImport._

class TpolecatPluginSuite extends AnyFunSuite with ScalaCheckDrivenPropertyChecks {
  val versionGen = Gen.chooseNum(0L, 20L)

  test("valid when neither addedIn nor removedIn") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val scalacOption = ScalacOption("-some-opt", None, None)
      assert(
        validFor(currentVersion)(scalacOption),
        "Should be valid when neither addedIn nor removedIn"
      )
    }
  }

  test("valid when added in past major release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val addedVersion = Version(currentMaj - 1, 0)
      val scalacOption = ScalacOption("-some-opt", addedIn = Some(addedVersion))
      assert(
        validFor(currentVersion)(scalacOption),
        "Should be valid when addedIn matches past major release"
      )
    }
  }

  test("valid when added in past minor release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val addedVersion = Version(currentMaj, currentMin - 1)
      val scalacOption = ScalacOption("-some-opt", addedIn = Some(addedVersion))
      assert(
        validFor(currentVersion)(scalacOption),
        "Should be valid when addedIn matches past minor release"
      )
    }
  }

  test("valid when added in this minor release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val scalacOption = ScalacOption("-some-opt", addedIn = Some(currentVersion))
      assert(
        validFor(currentVersion)(scalacOption),
        "Should be valid when addedIn matches this minor release"
      )
    }
  }

  test("not valid when added in a future major release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val addedVersion = Version(currentMaj + 1, currentMin)
      val scalacOption = ScalacOption("-some-opt", addedIn = Some(addedVersion))
      assert(
        !validFor(currentVersion)(scalacOption),
        "Should not be valid when addedIn matches a future major release"
      )
    }
  }

  test("not valid when added in a future minor release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val addedVersion = Version(currentMaj, currentMin + 1)
      val scalacOption = ScalacOption("-some-opt", addedIn = Some(addedVersion))
      assert(
        !validFor(currentVersion)(scalacOption),
        "Should not be valid when addedIn matches a future minor release"
      )
    }
  }

  test("valid when removed in next major release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val removedVersion = Version(currentMaj + 1, 0)
      val scalacOption = ScalacOption("-some-opt", None, removedIn = Some(removedVersion))
      assert(
        validFor(currentVersion)(scalacOption),
        "Should be valid when removedIn matches next major release"
      )
    }
  }

  test("valid when removed in next minor release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val removedVersion = Version(currentMaj, currentMin + 1)
      val scalacOption = ScalacOption("-some-opt", None, removedIn = Some(removedVersion))
      assert(
        validFor(currentVersion)(scalacOption),
        "Should be valid when removedIn matches next minor release"
      )
    }
  }

  test("not valid when removed in this minor release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val scalacOption = ScalacOption("-some-opt", None, removedIn = Some(currentVersion))
      assert(
        !validFor(currentVersion)(scalacOption),
        "Should not be valid when removedIn matches this minor release"
      )
    }
  }

  test("not valid when removed in an old major release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val removedVersion = Version(currentMaj - 1, currentMin)
      val scalacOption = ScalacOption("-some-opt", None, removedIn = Some(removedVersion))
      assert(
        !validFor(currentVersion)(scalacOption),
        "Should not be valid when removedIn matches an old major release"
      )
    }
  }

  test("not valid when removed in an old minor release") {
    forAll(versionGen, versionGen) { (currentMaj: Long, currentMin: Long) =>
      val currentVersion = Version(currentMaj, currentMin)
      val removedVersion = Version(currentMaj, currentMin - 1)
      val scalacOption = ScalacOption("-some-opt", None, removedIn = Some(removedVersion))
      assert(
        !validFor(currentVersion)(scalacOption),
        "Should not be valid when removedIn matches an old minor release"
      )
    }
  }
}
