# SParser
Implementation of a simple s-expression parser. It is used for educational purpose for students practical lessons.

# Usage
```java
SParser reader = new SParser();
SPrinter printer;
List<SNode> nodes = reader.parse("( \"Hello world\")");
SNode n = nodes.get(0);
printer = new SPrinter();
n.accept(printer);
assertTrue(printer.result().toString().equals("( \"Hello world\" )"));
```
