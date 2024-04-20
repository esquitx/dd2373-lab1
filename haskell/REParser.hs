module REParser where

import Text.ParserCombinators.ReadP

import RegExp


parseExprParen :: ReadP RegExp
parseExprParen = between (char '(') (char ')') parseExpr

parseDot :: ReadP RegExp
parseDot = char '.' >> return Dot

parseLitteral :: ReadP RegExp
parseLitteral =
  Litteral <$> satisfy (`notElem` ['.', '|', '*', '+', '?', '(', ')'])

parseExpr1 :: ReadP RegExp
parseExpr1 = parseExprParen <++ parseDot <++ parseLitteral

parseClosure :: ReadP RegExp
parseClosure = do
  e <- parseExpr1
  char '*'
  return $ Closure e

parseOneOrMore :: ReadP RegExp
parseOneOrMore = do
  e <- parseExpr1
  char '+'
  return $ OneOrMore e

parseZeroOrOne :: ReadP RegExp
parseZeroOrOne = do
  e <- parseExpr1
  char '?'
  return $ ZeroOrOne e

parseExprPost :: ReadP RegExp
parseExprPost = parseClosure <++ parseOneOrMore <++ parseZeroOrOne <++ parseExpr1

parseConcatenation :: ReadP RegExp
parseConcatenation = chainl1 parseExprPost $ return Concatenation

parseUnion :: ReadP RegExp
parseUnion = foldl1 Union <$> sepBy1 parseConcatenation (char '|')

parseExpr :: ReadP RegExp
parseExpr = parseUnion


eofParser :: ReadP t -> String -> [(t, String)]
eofParser p = readP_to_S $ do { res <- p; eof; return res }

parseRegExp :: String -> Either String RegExp
parseRegExp s =
  case eofParser parseExpr s of
    (v, ""):_ -> Right v
    _         -> Left "invalid regexp"
