package tagger.algo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import tagger.domain.Constants;
import tagger.domain.PreTag_Tag;
import tagger.domain.Sentence;
import tagger.domain.Tag;
import tagger.domain.Word;
import tagger.domain.Word_Tag;

public class ViterbiHmm {
	private int taggedCorrectly = 0;
	private int totalTagNumber = 0;
	private Map<PreTag_Tag, Double> preTagTagProbMap = new HashMap<PreTag_Tag, Double>();
	private Map<Word_Tag, Double> wordTagProbMap = new HashMap<Word_Tag, Double>();

	public void run() {

		prepareViterbi();
	}

	private void prepareViterbi() {

		GeniaData geniaTestObj = new GeniaData();

		geniaTestObj = geniaTestObj.createGeniaTestObject();
		HmmModel hmmModel = new HmmModel();

		hmmModel.readProbMaps();
		List<Tag> tagList = convertKeySetToList(hmmModel);

		runViterbi(geniaTestObj, hmmModel, tagList);
	}

	public void runConsole() {
		HmmModel hmmModel = new HmmModel();
		hmmModel.readProbMaps();
		List<Tag> tagList = convertKeySetToList(hmmModel);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			String input;
			while (true) {
				System.out.println("Xin hãy nhập câu tiếng việt: ");
				input = br.readLine();

				if ("q".equals(input)) {
					System.out.println("Exit!");
					System.exit(0);
				}
				runViterbi(input, hmmModel, tagList);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private List<Tag> convertKeySetToList(HmmModel hmmProb) {

		Set<Tag> set = hmmProb.getTagCountMap().keySet();

		List<Tag> tagList = new ArrayList<Tag>();

		tagList.addAll(set);

		tagList.remove(new Tag(Constants.SENTENCE_START));
		tagList.remove(new Tag(Constants.SENTENCE_END));

		tagList.add(0, new Tag(Constants.SENTENCE_START));
		tagList.add(tagList.size(), new Tag(Constants.SENTENCE_END));

		return tagList;
	}

	private void runViterbi(GeniaData geniaTestObj, HmmModel hmmProb, List<Tag> tagList) {
		try {
			int sentenceLength;
			int tagListLength;
			List<Tag> viterbiPath;
			Double[][] viterbiTable;
			Double[] probs;
			Double preTag_TagProb;
			Double word_TagProb;
			int argmax;
			PrintWriter writer = new PrintWriter(Constants.VITERBI_PATH_OUTPUT_FILE, "UTF-8");

			System.out.println(Constants.VITERBI_PATH_OUTPUT_FILE + " is being generated..");

			for (Sentence sentence : geniaTestObj.getSentenceList()) {
				System.out.println(sentence.toString());
				sentenceLength = sentence.getWords().size();
				tagListLength = tagList.size();

				viterbiTable = new Double[tagListLength][sentenceLength];

				viterbiPath = new ArrayList<Tag>();

				for (int t = 1; t < tagListLength; t++) {

					preTag_TagProb = hmmProb.getPreTag_TagProbMap().get(new PreTag_Tag(tagList.get(0), tagList.get(t)));

					word_TagProb = hmmProb.getWord_TagProbMap()
							.get(new Word_Tag(sentence.getWords().get(1), tagList.get(t)));
					if (null == preTag_TagProb) {
						preTag_TagProb = 0.0;
					}
					if (null == word_TagProb) {
						word_TagProb = 0.0;
					}
					viterbiTable[t][1] = preTag_TagProb + word_TagProb;
				}

				for (int w = 2; w < sentenceLength; w++) {
					for (int t = 1; t < tagListLength; t++) {
						probs = new Double[tagListLength];
						for (int i = 1; i < tagListLength; i++) {

							if (viterbiTable[i][w - 1] != null) {
								preTag_TagProb = hmmProb.getPreTag_TagProbMap()
										.get(new PreTag_Tag(tagList.get(i), tagList.get(t)));
								word_TagProb = hmmProb.getWord_TagProbMap()
										.get(new Word_Tag(sentence.getWords().get(w), tagList.get(t)));
								if (null == preTag_TagProb) {
									preTag_TagProb = 0.0;
								}
								if (null == word_TagProb) {
									word_TagProb = 0.0;
								}
								probs[i] = viterbiTable[i][w - 1] + preTag_TagProb + word_TagProb;

							} else {
								probs[i] = Math.log(Constants.NOT_IN_VOCAB_PROB) / Math.log(2);
							}
						}
						argmax = argmax(probs);
						viterbiTable[t][w] = probs[argmax];
					}
				}
				generateViterbiPath(tagList, sentenceLength, tagListLength, viterbiTable, viterbiPath, sentence,
						writer);

			}

			writer.close();

			System.out.println(Constants.VITERBI_PATH_OUTPUT_FILE + " is generated..");

			printAccuracy();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private Sentence convertSentece(String intput) {
		String[] setences;
		Sentence sentence;
		sentence = new Sentence();
		sentence.getWords().add(new Word(Constants.SENTENCE_START));
		sentence.getTags().add(new Tag(Constants.SENTENCE_START));
		setences = intput.split(Constants.SPACES);

		for (String w : setences) {
			sentence.getWords().add(new Word(w.toLowerCase()));
			sentence.getTags().add(new Tag(Constants.TEMPLABEL));
		}
		sentence.getWords().add(new Word(Constants.SENTENCE_END));
		sentence.getTags().add(new Tag(Constants.SENTENCE_END));
		return sentence;
	}

	private void runViterbi(String intput, HmmModel hmmProb, List<Tag> tagList) {
		try {
			int sentenceLength;
			int tagListLength;
			List<Tag> viterbiPath;
			Double[][] viterbiTable;
			Double[] probs;
			Double preTag_TagProb;
			Double word_TagProb;
			int argmax;
			Sentence sentence;

			sentence = convertSentece(intput);
			sentenceLength = sentence.getWords().size();
			tagListLength = tagList.size();

			viterbiTable = new Double[tagListLength][sentenceLength];
			viterbiPath = new ArrayList<Tag>();

			for (int t = 1; t < tagListLength; t++) {

				preTag_TagProb = hmmProb.getPreTag_TagProbMap().get(new PreTag_Tag(tagList.get(0), tagList.get(t)));
				word_TagProb = hmmProb.getWord_TagProbMap()
						.get(new Word_Tag(sentence.getWords().get(1), tagList.get(t)));
				if (null == preTag_TagProb) {
					preTag_TagProb = 0.0;
				}
				if (null == word_TagProb) {
					word_TagProb = 0.0;
				}
				viterbiTable[t][1] = preTag_TagProb + word_TagProb;
			}

			for (int w = 2; w < sentenceLength; w++) {
				for (int t = 1; t < tagListLength; t++) {
					probs = new Double[tagListLength];
					for (int i = 1; i < tagListLength; i++) {
						if (viterbiTable[i][w - 1] != null) {
							preTag_TagProb = hmmProb.getPreTag_TagProbMap()
									.get(new PreTag_Tag(tagList.get(i), tagList.get(t)));
							word_TagProb = hmmProb.getWord_TagProbMap()
									.get(new Word_Tag(sentence.getWords().get(w), tagList.get(t)));
							if (null == preTag_TagProb) {
								preTag_TagProb = 0.0;
							}
							if (null == word_TagProb) {
								word_TagProb = 0.0;
							}
							probs[i] = viterbiTable[i][w - 1] + preTag_TagProb + word_TagProb;
						} else {
							probs[i] = Math.log(Constants.NOT_IN_VOCAB_PROB) / Math.log(2);
						}
					}
					argmax = argmax(probs);
					viterbiTable[t][w] = probs[argmax];
				}
			}
			generateViterbiPath(tagList, sentenceLength, tagListLength, viterbiTable, viterbiPath, sentence);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printAccuracy() {
		System.out.println("Viterbi taggedCorrectly: " + taggedCorrectly);
		System.out.println("Viterbi totalTagNumber: " + totalTagNumber);
		Double accuracy = ((double) taggedCorrectly / totalTagNumber) * 100;
		System.out.println("Viterbi Accuracy: %" + String.format(Locale.US, "%.2f", accuracy));
	}

	private void generateViterbiPath(List<Tag> tagList, int sentenceLength, int tagListLength, Double[][] viterbiTable,
			List<Tag> viterbiPath, Sentence sentence, PrintWriter writer) {
		for (int w = 1; w < sentenceLength; w++) {
			Double[] probs = new Double[tagListLength];
			for (int t = 1; t < tagListLength; t++) {
				probs[t] = viterbiTable[t][w];
			}
			int index = argmax(probs);
			viterbiPath.add(tagList.get(index));
		}
		viterbiPath.add(0, new Tag(Constants.SENTENCE_START));
		writer.println("test sentence words: " + sentence.getWords());
		writer.println("test sentence tags : " + sentence.getTags());
		writer.println("vibertiPath        : " + viterbiPath);
		writer.println();
		calculateAccuracy(viterbiPath, sentence);
	}

	private void generateViterbiPath(List<Tag> tagList, int sentenceLength, int tagListLength, Double[][] viterbiTable,
			List<Tag> viterbiPath, Sentence sentence) {
		int index;
		Double[] probs;
		StringBuilder viterbiPathOutPut = new StringBuilder();
		viterbiPathOutPut.append(Constants.SENTENCE_START + Constants.WORD_TAG_SEPERATOR + Constants.SENTENCE_START);
		for (int w = 1; w < sentenceLength; w++) {
			probs = new Double[tagListLength];
			for (int t = 1; t < tagListLength; t++) {
				probs[t] = viterbiTable[t][w];
			}
			index = argmax(probs);
			viterbiPathOutPut.append(
					sentence.getWords().get(w) + Constants.WORD_TAG_SEPERATOR + tagList.get(index) + Constants.SPACES);
		}
		viterbiPath.add(0, new Tag(Constants.SENTENCE_START));
		System.out.println("test sentence words: " + sentence.getWords());
		System.out.println("vibertiPath        : " + viterbiPathOutPut);

	}

	private void calculateAccuracy(List<Tag> viterbiPath, Sentence sentence) {
		for (int i = 0; i < viterbiPath.size(); i++) {
			if (viterbiPath.get(i).equals(sentence.getTags().get(i))) {
				taggedCorrectly++;
			}
			totalTagNumber++;
		}
	}

	private static Integer argmax(Double[] arr) {
		Double max = arr[1];
		Integer argmax = 1;
		for (int i = 2; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
				argmax = i;
			}
		}
		return argmax;
	}
}
