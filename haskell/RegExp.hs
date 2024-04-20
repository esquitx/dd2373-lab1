module RegExp where

data RegExp = Litteral Char
            | Dot
            | Union RegExp RegExp
            | Concatenation RegExp RegExp
            | Closure RegExp
            | OneOrMore RegExp
            | ZeroOrOne RegExp
            deriving (Show, Eq)
