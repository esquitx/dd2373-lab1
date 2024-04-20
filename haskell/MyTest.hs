-- Dependencies:
--   * QuickCheck
--   * regex-compat

import Test.QuickCheck
import Text.Regex


-- Your own regex search function here:
-- Takes a regex and a text as input
-- Returns true if the regex matches any substring of the text; otherwise returns false
mySearch :: String -> String -> Bool
mySearch regex text = False  -- should be something that correctly implements it


test1 :: Property
test1 = let regex = "abab" in
  forAll (listOf $ elements "abc") $ \s -> case matchRegex (mkRegex regex) s of
  Nothing -> not $ mySearch regex s
  Just _  -> mySearch regex s

test2 :: Property
test2 = let regex = "ab|cde" in
  forAll (listOf $ elements "abcdef") $ \s -> case matchRegex (mkRegex regex) s of
  Nothing -> not $ mySearch regex s
  Just _  -> mySearch regex s

test3 :: Property
test3 = let regex = "a*bc*(ab)+" in
  forAll (listOf $ elements "abcd") $ \s -> case matchRegex (mkRegex regex) s of
  Nothing -> not $ mySearch regex s
  Just _  -> mySearch regex s

test4 :: Property
test4 = let regex = "a+.+b?a+.+b+" in
  forAll (listOf $ elements "abcde") $ \s -> case matchRegex (mkRegex regex) s of
  Nothing -> not $ mySearch regex s
  Just _  -> mySearch regex s

test5 :: Property
test5 = let regex = "abcd*|cba(abc)+|(abc)+(bdb)(abc)?|a.b.c.d|(((abc)+)+)+" in
  forAll (listOf $ elements "abcde") $ \s -> case matchRegex (mkRegex regex) s of
  Nothing -> not $ mySearch regex s
  Just _  -> mySearch regex s


main = do
  quickCheck test1
  quickCheck test2
  quickCheck test3
  quickCheck test4
  quickCheck test5
