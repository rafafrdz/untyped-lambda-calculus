package mf.tlp.lambdaCalculus.exercises

import mf.tlp.lambdaCalculus.adt.Exp.{<>, red}
import mf.tlp.lambdaCalculus.adt._

object Numerals extends Exercise {

  val ZERO: Exp[String] = numeral(0)
  val ONE: Exp[String] = numeral(1)
  val TWO: Exp[String] = numeral(2)

  val ADD: Exp[String] = a ~> (b ~> (f ~> (x ~> (b <> f <> (a <> f <> x)))))
  val MUL: Exp[String] = a ~> (b ~> (f ~> (x ~> (b <> (a <> f) <> x))))
  val POW: Exp[String] = a ~> (b ~> (f ~> (x ~> (b <> a <> f <> x))))

  def numeralChurch(n: Int): Exp[String] = numeral(n)

  def numeralUnChurch[T](e: Exp[T]): Int = e match {
    case Lambda(_, scope) => numeralUnChurch(scope)
    case Application(_, scope) => 1 + numeralUnChurch(scope)
    case Var(_) => 0
  }

  private def numeral(n: Int): Exp[String] = {
    def aux(m: Int): Exp[String] =
      if (m == 0) Var("x") else <>(Var("f"), aux(m - 1))

    Lambda(Var("f"), Lambda(Var("x"), aux(n)))
  }

  evaluate(
    numeralUnChurch(TWO) // 2
    , numeralUnChurch(numeralChurch(100)) // 100
    , numeralUnChurch(red(ADD <> TWO <> TWO)) // 4
    , numeralUnChurch(red(MUL <> TWO <> numeralChurch(6))) // 12
    , numeralUnChurch(red(POW <> TWO <> numeralChurch(6))) // 64
  )

}
