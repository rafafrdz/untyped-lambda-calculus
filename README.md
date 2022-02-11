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

[https://gist.github.com/rafafrdz/4b8f59a5b6398c050b936b24f5c7811b](https://gist.github.com/rafafrdz/4b8f59a5b6398c050b936b24f5c7811b)

where show method is

[https://gist.github.com/rafafrdz/b34bfe4ad3caca8904451234f71aab89](https://gist.github.com/rafafrdz/b34bfe4ad3caca8904451234f71aab89)

## Lambda calculus rules

**Code:** `src/main/scala/mf/tlp/lambdaCalculus/adt/Exp.scala`

### Capture Avoiding Substitution (Exercise 14 and 15)

[https://gist.github.com/rafafrdz/2ec14b7e89beb1b214ab681b203db3c7](https://gist.github.com/rafafrdz/2ec14b7e89beb1b214ab681b203db3c7)

Where the auxiliar methods are the following (`freeVariable` method **Exercise 14**):

[https://gist.github.com/rafafrdz/db578453b66657d70c884112cf402ff4](https://gist.github.com/rafafrdz/db578453b66657d70c884112cf402ff4)

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