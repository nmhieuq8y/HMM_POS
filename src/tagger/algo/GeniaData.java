package tagger.algo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import tagger.domain.Constants;
import tagger.domain.Sentence;
import tagger.domain.Tag;
import tagger.domain.Word;

public class GeniaData {

	private List<Sentence> sentenceList = new ArrayList<Sentence>();

	private Map<Tag, Integer> tagCountMap = new HashMap<Tag, Integer>();

	private Map<Word, Map<Tag, Integer>> wordTagCountMap = new HashMap<Word, Map<Tag, Integer>>();

	public GeniaData() {
		sentenceList = new ArrayList<Sentence>();
		tagCountMap = new HashMap<Tag, Integer>();
		wordTagCountMap = new HashMap<Word, Map<Tag, Integer>>();
	}

	public GeniaData(List<Sentence> sentenceList, Map<Tag, Integer> tagCountMap,
			Map<Word, Map<Tag, Integer>> wordTagCountMap) {
		super();
		this.sentenceList = sentenceList;
		this.tagCountMap = tagCountMap;
		this.wordTagCountMap = wordTagCountMap;
	}

	public List<Sentence> getSentenceList() {
		return sentenceList;
	}

	public Map<Tag, Integer> getTagCountMap() {
		return tagCountMap;
	}

	public void driveSumFile() {
		Random r = new Random();
		PrintWriter writerTest = null;
		PrintWriter writerTrain = null;
		int i = 0;
		int Low = 1;
		int High = 100;
		int Result = r.nextInt(High - Low) + Low;
		BufferedReader br = null;
		String fileLine;
		try {
			writerTest = new PrintWriter(Constants.GENIA_TEST_FILE, "UTF-8");
			writerTrain = new PrintWriter(Constants.GENIA_TRAIN_FILE, "UTF-8");

			br = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.GENIA_SUM_FILE), "UTF8"));
			while ((fileLine = br.readLine()) != null) {
				if (fileLine.trim().length() != 0) {
					Result = r.nextInt(High - Low) + Low;
					i++;
					if (Result <= 30) {
						System.out.println(Result);
						writerTest.println(fileLine);
						System.out.println("Test");
					} else {
						System.out.println(Result);
						writerTrain.println(fileLine);
						System.out.println("Train");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writerTest.close();
			writerTrain.close();
		}
	}

	public void sumAllFilesForFolder(File folder) {
		PrintWriter writer = null;
		String s = "Done";
		try {
			writer = new PrintWriter("D:\\Data\\Searching\\study\\Trainset-POS-1\\Sum.pos", "UTF-8");
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					sumAllFilesForFolder(fileEntry);
				} else {
					System.out.println(fileEntry);
					sumfile(fileEntry.toString(), writer);
					System.out.println(s);
				}
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.close();
	}

	public void sumfile(String fileName, PrintWriter writer) {
		BufferedReader br = null;
		String fileLine;

		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF8"));
			while ((fileLine = br.readLine()) != null) {
				writer.println(fileLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void readGeniaFile(String fileName) {

		BufferedReader br = null;

		try {

			String fileLine;
			String[] setences;
			String[] word_Tags;
			Sentence sentence;
			StringBuilder word;
			br = new BufferedReader(new FileReader(fileName));

			while ((fileLine = br.readLine()) != null) {

				sentence = new Sentence();
				sentence.getWords().add(new Word(Constants.SENTENCE_START));
				sentence.getTags().add(new Tag(Constants.SENTENCE_START));
				setences = fileLine.split(Constants.SPACES);
				
				for (String word_tag : setences) {
					if(word_tag.equals("//CH")) {
						sentence.getWords().add(new Word("/"));
						sentence.getTags().add(new Tag("CH"));
					} else {
						System.out.println(word_tag);
						word_Tags = word_tag.split(Constants.WORD_TAG_SEPERATOR);
						if(word_Tags.length > 2) {
							word = new StringBuilder();
							word.append(word_Tags[0].toLowerCase());
							for(int i = 1; i < word_Tags.length-1; i++) {
								word.append(Constants.WORD_TAG_SEPERATOR);
								word.append(word_Tags[i].toLowerCase());
							}
							sentence.getWords().add(new Word(word.toString()));
							sentence.getTags().add(new Tag(word_Tags[word_Tags.length-1]));
						} else {
							sentence.getWords().add(new Word(word_Tags[0].toLowerCase()));
							sentence.getTags().add(new Tag(word_Tags[1]));
						}
						
					}
				}
				
				sentence.getWords().add(new Word(Constants.SENTENCE_END));
				sentence.getTags().add(new Tag(Constants.SENTENCE_END));
				sentenceList.add(sentence);
			}

			System.out.println(fileName + " is read and sentences are created..");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void createTagMap(String fileName) {

		for (Sentence sentence : sentenceList) {
			if(null != sentence) {
				for (Tag tag : sentence.getTags()) {

					if (tagCountMap.containsKey(tag)) {

						tagCountMap.put(tag, tagCountMap.get(tag) + 1);

					} else {

						tagCountMap.put(tag, 1);
					}

				}
			} else {
				break;
			}
			
		}

		System.out.println("Tag Map of " + fileName + " is created..");

		System.out.println("Size of tag map is: " + tagCountMap.size());

		System.out.println("tag set of " + fileName + ": " + tagCountMap.keySet());
	}

	// this method is used for baseline tagger
	public void createWordTagCountMap() {

		for (Sentence sentence : sentenceList) {
			if(null != sentence) {
				for (int i = 0; i < sentence.getWords().size(); i++) {
					Word word = sentence.getWords().get(i);

					Tag tag = sentence.getTags().get(i);

					if (wordTagCountMap.containsKey(word)) {

						Map<Tag, Integer> map = wordTagCountMap.get(word);

						if (map.containsKey(tag)) {

							map.put(tag, map.get(tag) + 1);

						} else {

							map.put(tag, 1);
						}

						wordTagCountMap.put(word, map);

					} else {

						Map<Tag, Integer> map = new HashMap<Tag, Integer>();

						map.put(tag, 1);

						wordTagCountMap.put(word, map);
					}
				}
			}
			
		}

	}

	public GeniaData createGeniaTrainObject() {

		GeniaData trainData = new GeniaData();

		trainData.readGeniaFile(Constants.GENIA_TRAIN_FILE);

		trainData.createTagMap(Constants.GENIA_TRAIN_FILE);

		trainData.createWordTagCountMap();

		return trainData;
	}

	public GeniaData createGeniaTestObject() {

		GeniaData testData = new GeniaData();

		testData.readGeniaFile(Constants.GENIA_TEST_FILE);

		return testData;
	}

}
