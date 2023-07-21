# **Example of inline word counting**
import time
from utils.functions import replaceCharactersInStr

def inlineWordCounter(content):
    # Start the timer
    start_time = time.time()

    # Replace new-line characters with space in text
    content = content.replace("\n", " ")

    # Convert string from received string to lower case and split the words
    splittedWords = content.lower().split(" ")

    # Remove replace these characters: (,.)-:;!? with blank character
    filteredSplittedWords = map(lambda item: replaceCharactersInStr(item, ""), splittedWords)

    # Remove spaces from received strings
    filteredSplittedWords = filter(lambda item: item != "", filteredSplittedWords)
    words = {}

    # Count the words
    for oneStr in filteredSplittedWords:
        if oneStr not in words:  # if not words.__contains__(oneStr)
            words[oneStr] = 1
        else:
            words[oneStr] += 1

    # Print elapsed time
    print("--- %s seconds ---" % (time.time() - start_time))

    # Print counted words
    index = 0
    for wordKey in words.keys():
        print(f"{index+1}. ('{wordKey}', {words[wordKey]})")
        index += 1

    return words
