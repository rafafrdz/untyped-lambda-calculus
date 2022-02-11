package mf.tlp.lambdaCalculus.exercises

import mf.tlp.lambdaCalculus.adt.Exp.red
import mf.tlp.lambdaCalculus.adt.{Exp, Lambda, Var}

object Booleans extends Exercise {

  /**
   * Primitive form
   * val TRUE: Exp[String] = \(x, \(y, x))
   * val FALSE: Exp[String] = \(x, \(y, y))
   * */

  val TRUE: Exp[String] = x ~> (y ~> x)

  val FALSE: Exp[String] = x ~> (y ~> y)

  val COND: Exp[String] = b ~> (x ~> (y ~> (b <> x <> y)))

  val CONJ: Exp[String] = x ~> (y ~> (x <> y <> FALSE))

  val DISJ: Exp[String] = x ~> (y ~> (x <> TRUE <> y))

  val NEG: Exp[String] = b ~> (x ~> (y ~> (b <> y <> x)))

  def boolChurch(b: Boolean): Exp[String] = if (b) TRUE else FALSE

  def boolUnChurch[T](e: Exp[T]): Boolean = e match {
    case Lambda(Var(x), Lambda(Var(_), Var(z))) if x == z => true
    case Lambda(Var(_), Lambda(Var(y), Var(z))) if y == z => false
  }

  evaluate(
    TRUE // λx.λy.x
    , FALSE // λx.λy.Y
    , boolChurch(true) // λx.λy.x
    , boolChurch(false) // λx.λy.Y
    , boolUnChurch(TRUE) // true
    , boolUnChurch(FALSE) // false

    /** CONJUNCTION */
    , boolUnChurch(red(CONJ <> TRUE <> TRUE)) // true
    , boolUnChurch(red(CONJ <> TRUE <> FALSE)) // false
    , boolUnChurch(red(CONJ <> FALSE <> TRUE)) // false
    , boolUnChurch(red(CONJ <> FALSE <> FALSE)) // false

    /** DISJUNCTION */
    , boolUnChurch(red(DISJ <> TRUE <> TRUE)) // true
    , boolUnChurch(red(DISJ <> TRUE <> FALSE)) // true
    , boolUnChurch(red(DISJ <> FALSE <> TRUE)) // true
    , boolUnChurch(red(DISJ <> FALSE <> FALSE)) // false

    /** NEGATION */
    , boolUnChurch(red(NEG <> TRUE)) // false
    , boolUnChurch(red(NEG <> FALSE)) // true

  )

}
