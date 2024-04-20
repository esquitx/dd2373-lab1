module Main where

import REParser
import PrettyPrinter

-- TODO: Implement a grep-like tool.

main =
  -- Example of using the regexp parser:
  case parseRegExp "(aba)*c+a|cd" of
    Right v -> print $ prettyPrint v
