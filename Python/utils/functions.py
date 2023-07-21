# Replace these characters: (,.)-:;!? with blank character
def replaceCharactersInStr(s, charToReplace):
    return s\
        .replace("(", charToReplace)\
        .replace(",", charToReplace)\
        .replace(".", charToReplace)\
        .replace(")", charToReplace)\
        .replace("-", charToReplace)\
        .replace(":", charToReplace)\
        .replace(";", charToReplace)\
        .replace("!", charToReplace)\
        .replace("?", charToReplace)

# Change dictionary from key (name) / value (count) to str "key1:count1;key2:count2"
def createStrFromWordsDict(words):
    strToRet = ""

    for idx, wordKey in enumerate(words.keys()):
        strToRet += f"{wordKey}:{words[wordKey]}"
        if (idx + 1) != len(words):
            strToRet += ";"

    return strToRet