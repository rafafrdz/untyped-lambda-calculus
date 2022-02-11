package mf.tlp.lambdaCalculus.adt

import mf.tlp.lambdaCalculus.adt.Exp.show

import scala.annotation.tailrec
import scala.language.implicitConversions

sealed trait Exp[T] {
  self =>
  override def toString: String = show[T](self)

  def <>(e: Exp[T]): Application[T] = Application[T](self, e)
}

case class Var[T](value: T) extends Exp[T] {
  /** Two ways to do a lambda term from a variable */
  def ~>(e: Exp[T]): Lambda[T] = Lambda(this, e)

  def ->(e: Exp[T]): Lambda[T] = ~>(e)
}

case class Lambda[T](v: Var[T], scope: Exp[T]) extends Exp[T]

case class Application[T](e1: Exp[T], e2: Exp[T]) extends Exp[T]

object Exp {

  def v[T](value: T): Var[T] = Var[T](value)

  def \[T](v: Var[T], scope: Exp[T]): Lambda[T] = Lambda[T](v, scope)

  def <>[T](e1: Exp[T], e2: Exp[T]): Application[T] = Application[T](e1, e2)


  /** Method to show in a pretty form the lambda-expression `term` */
  def show[T](term: Exp[T]): String = pretty[T](term)

  private def pretty[T](term: Exp[T], m: Int = 0): String = term match {
    case Var(value) => value.toString
    case Lambda(v, scope) => {
      lazy val s: String = s"λ$v.${pretty(scope)}"
      if (m != 0) s"($s)" else s
    }
    case Application(e1, e2) => {
      lazy val s: String = s"${pretty(e1, 1)} ${pretty(e2, 2)}"
      if (m == 2) s"($s)" else s
    }
  }

  /** Method to get the freeVariables in terms of type T */
  def freeVariable[T](term: Exp[T]): List[T] = {
    def aux(exp: Exp[T], bounded: List[T], free: List[T]): List[T] = exp match {
      case Var(value) => if (bounded.contains(value)) free else value :: free
      case Lambda(v, scope) => aux(scope, v.value :: bounded, free)
      case Application(e1, e2) => aux(e1, bounded, free) ++ aux(e2, bounded, free)
    }

    aux(term, Nil, Nil)
  }

  /** Method to obtain all variables that `term` contains in terms of Lambda-Expression type `Exp[T]` */
  def variables[T](term: Exp[T]): Set[Var[T]] = used(term)

  private def used[T](term: Exp[T]): Set[Var[T]] = ocurrence[T](term).map(v[T])

  /** Method to obtain all variables that `term` contains in terms of type `T` */
  def ocurrence[T](term: Exp[T]): Set[T] = {
    def aux(exp: Exp[T], occ: List[T]): List[T] = exp match {
      case Var(value) => value :: occ
      case Lambda(v, scope) => aux(scope, v.value :: occ)
      case Application(e1, e2) => aux(e1, occ) ++ aux(e2, occ)
    }

    aux(term, Nil).toSet
  }

  /** Methods to get new fresh variables */
  def fresh(set: Set[String], v: String = "x"): Var[String] = {
    val nv: Int => String = (i: Int) => s"$v$i"

    @tailrec
    def generate(acc: Int): String = if (set.contains(nv(acc))) generate(acc + 1) else nv(acc)

    Var(generate(0))
  }

  def fresh(set: Set[Var[String]], v: Var[String]): Var[String] = {
    val setV: Set[String] = set.map(_.value)
    fresh(setV, v.value)
  }

  def fresh(v: Var[String], term: Exp[String]): Var[String] = {
    val occ: Set[String] = ocurrence(term)
    fresh(occ, v.value)
  }


  /**
   * Capture Avoiding Substitution method
   *
   * @param term term to performs Capture Avoiding Substitution
   * @param x    variable that susbstitute
   * @param N    term to susbstitute
   * @return
   */
  def cas(term: Exp[String], x: Var[String], N: Exp[String]): Exp[String] = {
    term match {
      case v@Var(_) if v.equals(x) => N
      case v@Var(_) => v
      case Application(e1, e2) => <>(cas(e1, x, N), cas(e2, x, N))
      case l@Lambda(v, _) if v.equals(x) => l
      case l@Lambda(y, scope) if !variables(scope).contains(x) => l
      case Lambda(y, scope) if !variables(N).contains(y) => Lambda(y, cas(scope, x, N))
      case l@Lambda(y, scope) =>
        val nv: Var[String] = fresh(variables(scope).union(variables(N)), y)
        Lambda(nv, cas(cas(scope, y, nv), x, N))
    }
  }

  /** Alpha-reduction method */
  def alphaRed(term: Exp[String]): Exp[String] = alphaconversion(term)

  private def alpha(term: Exp[String], from: Var[String], to: Var[String]): Exp[String] = cas(term, from, to)

  private def alphaconversion(term: Exp[String]): Exp[String] = term match {
    case Lambda(v, scope) =>
      val nv: Var[String] = fresh(variables(scope), v)
      Lambda(nv, alphaconversion(alpha(scope, v, nv)))
    case e: Exp[String] => e
  }

  /** Beta-reduction method o simply red (alias) */
  def red(term: Exp[String]): Exp[String] = betaconversion(term)

  def betaRed(term: Exp[String]): Exp[String] = betaconversion(term)

  def beta(term: Exp[String], from: Var[String], to: Exp[String]): Exp[String] = cas(term, from, to)

  def betaconversion(term: Exp[String]): Exp[String] = term match {
    case Lambda(v, scope) => Lambda(v, betaconversion(scope))
    case Application(e1: Application[String], e2) => betaconversion(Application(betaconversion(e1), e2))
    case Application(e1: Lambda[String], e2) => betaconversion(beta(e1.scope, e1.v, e2))
    case e: Exp[String] => e
  }

}