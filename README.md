# Lambda Calculus Implementation

### **Theory of programming language**

**Author.** Rafael Fernández Ortiz

---

**Description**. To implement untyped lambda calculus in Scala

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

[https://gist.github.com/rafafrdz/29768f5da9a54bca123c3cc587f33999](https://gist.github.com/rafafrdz/29768f5da9a54bca123c3cc587f33999)

### Beta Conversion or Beta Reduction (Exercise 16 and 17)

[https://gist.github.com/rafafrdz/665f918d307e2204a96285d551ba7bcc](https://gist.github.com/rafafrdz/665f918d307e2204a96285d551ba7bcc)

# Exercise

We can observe that there is a trait named Exercise where is allocated some commons variables and the `evaluate` method. This one just print by REPL the value of a sequence of any objects.

## Church Booleans (Exercise 11)

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/exercises/Booleans.scala`

[https://gist.github.com/rafafrdz/fd64f649200c72b9d30ca54b8d374967](https://gist.github.com/rafafrdz/fd64f649200c72b9d30ca54b8d374967)

## Tuples

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/exercises/Tuples.scala`

[https://gist.github.com/rafafrdz/b624135e043aad45e15333129db78a94](https://gist.github.com/rafafrdz/b624135e043aad45e15333129db78a94)

## Church Numerals

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/exercises/Numerals.scala`

[https://gist.github.com/rafafrdz/64273ede9f8b8a89792ef65749687714](https://gist.github.com/rafafrdz/64273ede9f8b8a89792ef65749687714)