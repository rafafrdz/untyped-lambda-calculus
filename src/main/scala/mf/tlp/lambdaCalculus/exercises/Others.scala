package mf.tlp.lambdaCalculus.exercises

import mf.tlp.lambdaCalculus.adt.Exp


object Others extends Exercise {

  import Exp._

  val TRUE: Exp[String] = x ~> (y ~> x)
  val FALSE: Exp[String] = x ~> (y ~> y)
  val example1: Exp[String] = x -> (y -> (y <> x <> a))
  val example2: Exp[String] = x -> (y -> (y <> x <> y))

  evaluate(
    TRUE
    , FALSE
    , freeVariable(TRUE)
    , variables(TRUE)
    , ocurrence(TRUE)
    , FALSE
    , freeVariable(FALSE)
    , variables(FALSE)
    , ocurrence(FALSE)
    , example1
    , freeVariable(example1)
    , variables(example1)
    , ocurrence(example1)
    , example2
    , freeVariable(example2)
    , variables(example2)
    , ocurrence(example2)
  )

}
