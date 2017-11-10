package tagger.domain;

public class Constants {
	public static final String AND = "&";
	public static final String UNDERLINE = "_";
	public static final String SPACES = " ";
	public static final String GENIA_SUM_FILE = "D:\\Data\\Searching\\study\\HMM_POS\\Sum.pos";
    public static final String GENIA_TRAIN_FILE = "D:\\Data\\Searching\\study\\HMM_POS\\TrainSet.pos";
    public static final String GENIA_TEST_FILE = "D:\\Data\\Searching\\study\\HMM_POS\\TestSet.pos";
   
    public static final String WORD_TAG_SEPERATOR = "/";

    public static final String SENTENCE_START = "SentenceStart";
    public static final String SENTENCE_END = "SentenceEnd";
    public static final String TEMPLABEL = "Temp";
    
    
    public static final String MULTI_TAG_SEPERATOR = "|";

    public static final String VITERBI_PATH_OUTPUT_FILE = "D:\\Data\\Searching\\study\\HMM_POS\\viterbi_path_output_file.pos";

    public static final String GENIA_WORD_TAG_PROB_OUTPUT_FILE = "D:\\Data\\Searching\\study\\HMM_POS\\writerWordTag.pos";
    public static final String GENIA_PRETAG_TAG_OUTPUT_FILE = "D:\\Data\\Searching\\study\\HMM_POS\\writerPreTagTag.pos";
    public static final String GENIA_TAG_OUTPUT_FILE = "D:\\Data\\Searching\\study\\HMM_POS\\writerTag.pos";
    
    public static final Double NOT_IN_VOCAB_PROB = 1e-20;

}
