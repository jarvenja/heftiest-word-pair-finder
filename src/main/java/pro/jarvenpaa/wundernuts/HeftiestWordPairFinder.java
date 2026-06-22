package pro.jarvenpaa.wundernuts;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public final class HeftiestWordPairFinder {
	private final static class Word {
		static LinkedList<Word> highestPairs = new LinkedList<Word>();
		static int highestScore = 0;

		static private void verifyHighest(Word w1, Word w2) {
			int score = Integer.bitCount(w1.letters | w2.letters);
			if (score >= highestScore) {
				if (score > highestScore) {
					highestScore = score;
					highestPairs.clear();
				}
				highestPairs.add(w1);
				highestPairs.add(w2);
			}
		}
		
		private int letters = 0;
		final int pos;
		
		Word(int startPos) { pos = startPos; }
		void setLetter(int index) { letters |= 1 << index; }
		int calcLettersCount() { return Integer.bitCount(letters); }
		@Override
		public String toString() { return String.format("%d->%s", pos, Integer.toBinaryString(letters)); }
	}
	
	private static final int HYPHEN = -1;
	private final HashMap<Character, Integer> charMap = new HashMap<Character, Integer>();
	private final int maxDifferentLetters;
	private String text = null;
	
	public HeftiestWordPairFinder(int maxDifferentLettersInWord, CharSequence alphabet) {
		maxDifferentLetters = maxDifferentLettersInWord;
		for (int i = alphabet.length(); --i >= 0;) {
			char c = alphabet.charAt(i);
			charMap.put(c, i);
			charMap.put(Character.toUpperCase(c), i);
		}
		assert(charMap.size() >> 1 == alphabet.length());
		charMap.put('-', HYPHEN);
	}
			
	public void evaluate(String src) {
		text = src;
		Word.highestScore = 0;
		
		// Initialize 'hefty' categories
		@SuppressWarnings("unchecked")
		LinkedList<Word>[] categories = new LinkedList[maxDifferentLetters];
		for (int i = categories.length; --i >= 0;) categories[i] = new LinkedList<Word>();
		{	Word word = null;
			for (int i = 0; i < text.length(); ++i) {
				Integer letterIndex = charMap.get(text.charAt(i));
				if(letterIndex == null)
				{	if(word != null)
					{	categories[word.calcLettersCount()].add(word);
						//System.out.println(word);
						word = null;
					}
				} else if(letterIndex != HYPHEN) {
					if (word == null) word = new Word(i);
					word.setLetter(letterIndex);
				}
			}
		}
		
		// Create track by probability
		int n = categories.length; 
		while (categories[--n].isEmpty());
		PriorityQueue<Integer> track = new PriorityQueue<>(n * (n - 1), Collections.reverseOrder());
		for(; n > 1; --n)
			for(int m = n; m > 1; --m)
				track.add(((m + n) << 10) | (m << 5) | n);
		
		// Find best pairs
		for (Iterator<Integer> it = track.iterator(); it.hasNext();)
		{	int group2 = it.next(), group1 = group2 & 31;
			group2 >>= 5;
			group2 &= 31;
			if (group1 + group2 < Word.highestScore) return;
			if (group1 == group2) {
				for (ListIterator<Word> outer = categories[group1].listIterator(); outer.hasNext();) {
					Word w1 = outer.next();
					for (ListIterator<Word> inner = categories[group1].listIterator(outer.nextIndex()); inner.hasNext();)
						Word.verifyHighest(w1, inner.next());
				}
			} else 
				for (Word w1 : categories[group1])
					for (Word w2 : categories[group2])
						Word.verifyHighest(w1, w2);
		}
	}
	
	void appendWord(int pos, StringBuilder sb) {
		for (char c = text.charAt(pos); charMap.get(c) != null; c = text.charAt(++pos)) sb.append(c);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("The heftiest word pair instances (");
		sb.append(Word.highestPairs.size() >> 1);
		sb.append(") with the score of ");
		sb.append(Word.highestScore);
		sb.append(" are:\n");
		for (ListIterator<Word> it = Word.highestPairs.listIterator();;) {
			appendWord(it.next().pos, sb);
			sb.append(" & ");
			appendWord(it.next().pos, sb);
			if (!it.hasNext()) break;
			sb.append("\n");
		}
		return sb.toString();
	}
}