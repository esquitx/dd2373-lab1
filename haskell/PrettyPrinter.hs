module PrettyPrinter where

import RegExp

prettyPrint :: RegExp -> String
prettyPrint (Litteral c) = [c]
prettyPrint Dot = "."
prettyPrint (Union e1 e2) = "(" ++ prettyPrint e1 ++ "|" ++ prettyPrint e2 ++ ")"
prettyPrint (Concatenation e1 e2) = "(" ++ prettyPrint e1 ++ prettyPrint e2 ++ ")"
prettyPrint (Closure e) = "(" ++ prettyPrint e ++ "*)"
prettyPrint (OneOrMore e) = "(" ++ prettyPrint e ++ "+)"
prettyPrint (ZeroOrOne e) = "(" ++ prettyPrint e ++ "?)"
