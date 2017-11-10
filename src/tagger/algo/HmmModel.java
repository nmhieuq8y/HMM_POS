package tagger.algo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import tagger.domain.Constants;
import tagger.domain.PreTag_Tag;
import tagger.domain.Sentence;
import tagger.domain.Tag;
import tagger.domain.Word;
import tagger.domain.Word_Tag;

public class HmmModel {
	private Map<PreTag_Tag, Integer> preTag_TagCountMap = new HashMap<PreTag_Tag, Integer>();
	private Map<PreTag_Tag, Double> preTag_TagProbMap = new HashMap<PreTag_Tag, Double>();

	private Map<Word_Tag, Integer> word_TagCountMap = new HashMap<Word_Tag, Integer>();
	private Map<Word_Tag, Double> word_TagProbMap = new HashMap<Word_Tag, Double>();

	private Map<Tag, Integer> tagCountMap = new HashMap<Tag, Integer>();
	private Map<Word, Integer> vocab = new HashMap<Word, Integer>();

	private GeniaData trainDataObj = new GeniaData();

	public Map<Tag, Integer> getTagCountMap() {

		return tagCountMap;
	}

	public HmmModel() {
		preTag_TagCountMap = new HashMap<PreTag_Tag, Integer>();
		preTag_TagProbMap = new HashMap<PreTag_Tag, Double>();
		word_TagCountMap = new HashMap<Word_Tag, Integer>();
		word_TagProbMap = new HashMap<Word_Tag, Double>();
		tagCountMap = new HashMap<Tag, Integer>();
		vocab = new HashMap<Word, Integer>();
		trainDataObj = new GeniaData();
	}

	public HmmModel(Map<PreTag_Tag, Integer> preTag_TagCountMap, Map<PreTag_Tag, Double> preTag_TagProbMap,
			Map<Word_Tag, Integer> word_TagCountMap, Map<Word_Tag, Double> word_TagProbMap,
			Map<Tag, Integer> tagCountMap, Map<Word, Integer> vocab, GeniaData trainDataObj) {
		super();
		this.preTag_TagCountMap = preTag_TagCountMap;
		this.preTag_TagProbMap = preTag_TagProbMap;
		this.word_TagCountMap = word_TagCountMap;
		this.word_TagProbMap = word_TagProbMap;
		this.tagCountMap = tagCountMap;
		this.vocab = vocab;
		this.trainDataObj = trainDataObj;
	}

	public Map<PreTag_Tag, Double> getPreTag_TagProbMap() {
		return preTag_TagProbMap;
	}

	public void setPreTag_TagProbMap(Map<PreTag_Tag, Double> preTag_TagProbMap) {
		this.preTag_TagProbMap = preTag_TagProbMap;
	}

	public Map<Word_Tag, Double> getWord_TagProbMap() {
		return word_TagProbMap;
	}

	public void setWord_TagProbMap(Map<Word_Tag, Double> word_TagProbMap) {
		this.word_TagProbMap = word_TagProbMap;
	}

	public Double calculateTagTransitionProb(Tag previousTag, Tag currentTag) {

		Integer preTag_TagCount = preTag_TagCountMap.get(new PreTag_Tag(previousTag, currentTag));

		Integer previousCount = tagCountMap.get(previousTag);

		if (preTag_TagCount == null) {

			return Math.log((1.0 / preTag_TagCountMap.size()) / (previousCount + tagCountMap.size())) / Math.log(2);
		}

		return Math.log(((double) preTag_TagCount + 1.0) / (previousCount + tagCountMap.size())) / Math.log(2);

	}

	public Double calculateWord_TagLikelihoodProb(Word word, Tag tag) {

		Integer preTag_TagCount = word_TagCountMap.get(new Word_Tag(word, tag));

		Integer tagCount = tagCountMap.get(tag);

		if (preTag_TagCount == null) {

			return Math.log(((1.0 / word_TagCountMap.size()) / (tagCount + tagCountMap.size()))) / Math.log(2);
		}

		return Math.log(((double) preTag_TagCount + 1.0) / (tagCount + tagCountMap.size())) / Math.log(2);
	}

	public void prepareCountHolderMaps() {

		trainDataObj = new GeniaData();

		trainDataObj = trainDataObj.createGeniaTrainObject();

		tagCountMap = trainDataObj.getTagCountMap();

		createPreTag_TagCountMap(trainDataObj);

		createWord_TagCountMap(trainDataObj);

	}

	public void readProbMaps() {
		BufferedReader brPreTag_Tag = null;
		BufferedReader brWord_Tag = null;
		BufferedReader brTag = null;
		String fileLine;
		String[] parts;
		String[] word_Tags;
		String[] preTag_Tags;
		Tag tag = null;
		Word_Tag word_Tag = null;
		PreTag_Tag preTag_Tag = null;
		try {

			brWord_Tag = new BufferedReader(
					new InputStreamReader(new FileInputStream(Constants.GENIA_WORD_TAG_PROB_OUTPUT_FILE), "UTF8"));
			brPreTag_Tag = new BufferedReader(
					new InputStreamReader(new FileInputStream(Constants.GENIA_PRETAG_TAG_OUTPUT_FILE), "UTF8"));
			brTag = new BufferedReader(
					new InputStreamReader(new FileInputStream(Constants.GENIA_TAG_OUTPUT_FILE), "UTF8"));
			System.out.println("Read writerPreTagTag");
			while ((fileLine = brPreTag_Tag.readLine()) != null) {
				parts = fileLine.split(Constants.SPACES);
				preTag_Tags = parts[0].split(Constants.UNDERLINE);
				preTag_Tag = new PreTag_Tag(new Tag(preTag_Tags[0]), new Tag(preTag_Tags[1]));
				preTag_TagProbMap.put(preTag_Tag, Double.parseDouble(parts[1]));
			}
			System.out.println("Done read writerPreTagTag");
			
			System.out.println("Read writerWordTag");
			int i=0;
			while ((fileLine = brWord_Tag.readLine()) != null) {
				parts = fileLine.split(Constants.SPACES);
				word_Tags = parts[0].split(Constants.AND);
				word_Tag = new Word_Tag(new Word(word_Tags[0]), new Tag(word_Tags[1]));
				word_TagProbMap.put(word_Tag, Double.parseDouble(parts[1]));
				System.out.println(++i);
			}
			System.out.println("Done read writerWordTag");
			
			System.out.println("Read writeTag");
			while ((fileLine = brTag.readLine()) != null) {
				tag = new Tag((fileLine));
				tagCountMap.put(tag, 1);
			}
			System.out.println("Done read writeTag");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (brWord_Tag != null) {
					brWord_Tag.close();
				}
				if (brPreTag_Tag != null) {
					brPreTag_Tag.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void createPreTag_TagCountMap(GeniaData geniaDataObj) {

		for (Sentence sentence : geniaDataObj.getSentenceList()) {

			for (int i = 0; i < sentence.getTags().size() - 1; i++) {

				Tag previousTag = sentence.getTags().get(i);
				Tag currentTag = sentence.getTags().get(i + 1);

				PreTag_Tag preTag_Tag = new PreTag_Tag(previousTag, currentTag);

				if (preTag_TagCountMap.containsKey(preTag_Tag)) {
					preTag_TagCountMap.put(preTag_Tag, preTag_TagCountMap.get(preTag_Tag) + 1);
				} else {
					preTag_TagCountMap.put(preTag_Tag, 1);
				}
			}
		}

		System.out.println("C(ti-1, ti) holder map is created..");
	}

	private void createWord_TagCountMap(GeniaData geniaDataObj) {

		for (Sentence sentence : geniaDataObj.getSentenceList()) {

			for (int i = 0; i < sentence.getWords().size(); i++) {

				Word word = sentence.getWords().get(i);
				Tag tag = sentence.getTags().get(i);

				Word_Tag word_Tag = new Word_Tag(word, tag);

				if (word_TagCountMap.containsKey(word_Tag)) {
					word_TagCountMap.put(word_Tag, word_TagCountMap.get(word_Tag) + 1);

				} else {
					word_TagCountMap.put(word_Tag, 1);
				}
				if (!vocab.containsKey(word)) {
					vocab.put(word, 1);
				}

			}
		}

		System.out.println("C(ti, wi) holder map is created..");
	}

	private void calulateProbality() {

		Set<Tag> tagSet = trainDataObj.getTagCountMap().keySet();
		PreTag_Tag preTag_TagTemp = null;
		Word_Tag word_TagTemp = null;
		for (Tag ti : tagSet) {
			for (Tag tj : tagSet) {
				preTag_TagTemp = new PreTag_Tag(ti, tj);
				preTag_TagProbMap.put(preTag_TagTemp, calculateTagTransitionProb(ti, tj));
			}
		}

		Set<Word> wordSet = vocab.keySet();
		for (Word w : wordSet) {
			for (Tag ti : tagSet) {
				word_TagTemp = new Word_Tag(w, ti);
				word_TagProbMap.put(word_TagTemp, calculateWord_TagLikelihoodProb(w, ti));
			}
		}
	}

	private void writeProb() {
		PrintWriter writerPreTag_Tag = null;
		PrintWriter writerWord_Tag = null;
		PrintWriter writerTag = null;
		try {
			writerPreTag_Tag = new PrintWriter(Constants.GENIA_PRETAG_TAG_OUTPUT_FILE, "UTF-8");
			writerWord_Tag = new PrintWriter(Constants.GENIA_WORD_TAG_PROB_OUTPUT_FILE, "UTF-8");
			writerTag = new PrintWriter(Constants.GENIA_TAG_OUTPUT_FILE, "UTF-8"); 
			Set<PreTag_Tag> setPreTag_Tag = preTag_TagProbMap.keySet();
			Set<Word_Tag> setWord_Tag = word_TagProbMap.keySet();
			Set<Tag> setTag = tagCountMap.keySet();
			
			for (PreTag_Tag preTag_Tag : setPreTag_Tag) {
				writerPreTag_Tag.println(preTag_Tag.toString() + " " + preTag_TagProbMap.get(preTag_Tag));
			}
			for (Word_Tag word_Tag : setWord_Tag) {
				writerWord_Tag.println(word_Tag.toString() + " " + word_TagProbMap.get(word_Tag));
			}
			for(Tag tag : setTag) {
				writerTag.println(tag);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			writerPreTag_Tag.close();
			writerWord_Tag.close();
			writerTag.close();
		}
	}

	public void run() {
		prepareCountHolderMaps();
		calulateProbality();
		writeProb();
	}
}
