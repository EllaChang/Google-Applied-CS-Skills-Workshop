/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();
    private List<String> wordList = new ArrayList<>();
    private HashSet<String> wordSet = new HashSet<>();
    private HashMap<String, ArrayList<String>> lettersToWord = new HashMap<>();
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<>();
    private int wordLength = DEFAULT_WORD_LENGTH;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            if (sizeToWords.containsKey(word.length())) {
                sizeToWords.get(word.length()).add(word);
            } else {
                ArrayList<String> ls = new ArrayList<>();
                ls.add(word);
                sizeToWords.put(word.length(), ls);
            }
        }
    }

    public String sortLetters (String s) {
        char[] arr = s.toCharArray();
        Arrays.sort(arr);
        return new String(arr);
    }

    public boolean isGoodWord(String word, String base) {
        if (!wordSet.contains(word) || word.contains(base)) return false;
        return true;
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<>();
        if (lettersToWord.containsKey(sortLetters(targetWord))) {
            result = lettersToWord.get(sortLetters(targetWord));
        } else {
            for (String s : wordList)
                if (sortLetters(s).equals(sortLetters(targetWord))) result.add(s);
            lettersToWord.put(sortLetters(targetWord), result);
        }
        return result;
    }

    public boolean isPlusOneAnagram(String shorter, String longer) {
        for (int i = 0; i < longer.length(); i++) {
            String newStr = longer.substring(0, i) + longer.substring(i + 1);
            if (newStr.equals(shorter)) return true;
        }
        return false;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<>();
        for (String s : wordList) {
            if (s.length() == word.length()+1) {
                String shorter = sortLetters(word);
                String longer = sortLetters(s);
                if (isPlusOneAnagram(shorter, longer) && !s.contains(word)) result.add(s);
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        String starter = "";
        while (true) {
            Random r = new Random(System.currentTimeMillis());
            starter = sizeToWords.get(wordLength).get(r.nextInt(sizeToWords.size()));
            if (getAnagramsWithOneMoreLetter(starter).size() >= MIN_NUM_ANAGRAMS) {
                break;
            }
        }
        if (wordLength < MAX_WORD_LENGTH) wordLength++;
        return starter;
    }
}