# Lambda Calculus Implementation

### **Theory of programming language**

**Author.** Rafael Fernández Ortiz

---

**Description**. To implement untyped lambda calculus in Scala

**Exercise/paper.** [Untyped Lambda Calculus](https://github.com/rafafrdz/untyped-lambda-calculus/blob/master/paper/lambda_ut.pdf)

# Where are the code?

Exercise is located `into paper/lambda_ut.pdf`

Code is located into `src/main/scala/mr/tlp/lambdaCalculus`

- ADT folder contains the algebra datatype for Lambda Calculus expressions
- Exercises folder contains the exercises

## Datatypes (Exercise 13)

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/adt/Exp.scala`

```scala
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
```

where show method is

```scala
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
```



## Lambda calculus rules

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/adt/Exp.scala`

### Capture Avoiding Substitution (Exercise 14 and 15)

```scala
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
```

Where the auxiliar methods are the following (`freeVariable` method **Exercise 14**):

```scala
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
```



### Alpha Conversion or Alpha Reduction

```scala
  /** Alpha-reduction method */
  def alphaRed(term: Exp[String]): Exp[String] = alphaconversion(term)

  private def alpha(term: Exp[String], from: Var[String], to: Var[String]): Exp[String] = cas(term, from, to)

  private def alphaconversion(term: Exp[String]): Exp[String] = term match {
    case Lambda(v, scope) =>
      val nv: Var[String] = fresh(variables(scope), v)
      Lambda(nv, alphaconversion(alpha(scope, v, nv)))
    case e: Exp[String] => e
  }
```



### Beta Conversion or Beta Reduction (Exercise 16 and 17)

```scala
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
```



# Exercise

We can observe that there is a trait named Exercise where is allocated some commons variables and the `evaluate` method. This one just print by REPL the value of a sequence of any objects.

## Church Booleans (Exercise 11)

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/exercises/Booleans.scala`

```scala
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
```



## Tuples

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/exercises/Tuples.scala`

```scala
  import Booleans._

  val PAIR = x ~> (y ~> (p ~> (p <> x <> y)))
  val FST = x ~> (x <> TRUE)
  val SND = x ~> (x <> FALSE)

  evaluate(
    boolUnChurch(FST <> PAIR <> TRUE <> FALSE) // true
    , boolUnChurch(SND <> PAIR <> TRUE <> FALSE) // false
  )
```



## Church Numerals

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/exercises/Numerals.scala`

```scala
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
```

