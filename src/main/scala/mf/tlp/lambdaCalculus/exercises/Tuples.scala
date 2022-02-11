package mf.tlp.lambdaCalculus.exercises

object Tuples extends Exercise {

  import Booleans._

  val PAIR = x ~> (y ~> (p ~> (p <> x <> y)))
  val FST = x ~> (x <> TRUE)
  val SND = x ~> (x <> FALSE)

  evaluate(
    boolUnChurch(FST <> PAIR <> TRUE <> FALSE) // true
    , boolUnChurch(SND <> PAIR <> TRUE <> FALSE) // false
  )

}
