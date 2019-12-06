/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uros.citlab.module.htr;

import com.achteck.misc.types.ConfMat;
import com.achteck.misc.types.ParamAnnotation;
import com.achteck.misc.util.IO;
import com.achteck.misc.util.StringIO2;
import de.planet.languagemodel.lmtypes.LMAbstract;
import de.planet.languagemodel.lmtypes.LanguageModel;
import de.planet.languagemodel.train.CharTokenizer;
import edu.berkeley.nlp.lm.*;
import edu.berkeley.nlp.lm.cache.ArrayEncodedCachingLmWrapper;
import edu.berkeley.nlp.lm.cache.ContextEncodedCachingLmWrapper;
import edu.berkeley.nlp.lm.io.*;
import java.io.IOException;
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author tobias
 */
public class LMBerkleyChar extends LMAbstract implements LanguageModel {

    private NgramLanguageModel<String> languageModel;
    private List<String> words;
    @ParamAnnotation
    private String path2File = "";
    @ParamAnnotation
    private String spaceSubs = "" + ConfMat.NaC;
    private char spaceSubsChar;
    private List<String> tmpList;
    private SubstitutedList substitutedList;

    public LMBerkleyChar() {
        addReflection(this, LMBerkleyChar.class);
    }

    /**
     * @param path2File
     * @param spaceSubs
     */
    public LMBerkleyChar(String path2File, String spaceSubs) {
        this.path2File = path2File;
        this.spaceSubs = spaceSubs;
        addReflection(this, LMBerkleyChar.class);
    }

    @Override
    public void init() {
        super.init();
        if (path2File.endsWith(".bin")) {
            ArrayEncodedProbBackoffLm<String> languageModel1 = loadLmBinFromFileOrRessource(path2File);
            // hint! not thread safe
            languageModel = ArrayEncodedCachingLmWrapper.wrapWithCacheNotThreadSafe(languageModel1);
        } else if (path2File.endsWith(".arpa")) {
            ContextEncodedProbBackoffLm<String> languageModel1 = (ContextEncodedProbBackoffLm) LmReaders.readContextEncodedLmFromArpa(path2File);
            // hint! not thread safe
            languageModel = ContextEncodedCachingLmWrapper.wrapWithCacheNotThreadSafe(languageModel1);
        } else if (path2File.endsWith(".txt")) {
            List<String> text = null;
            try {
                text = StringIO2.loadLineList(path2File, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Objects.requireNonNull(text);
            CharTokenizer charTokenizer = new CharTokenizer(String.valueOf(spaceSubs));
            StringWordIndexer wordIndexer = new StringWordIndexer();
            wordIndexer.setStartSymbol("<s>");
            wordIndexer.setEndSymbol("</s>");
            wordIndexer.setUnkSymbol("<unk>");
            List<String> tokenizedList = new LinkedList<>();
            for (String line : text) {
                String tokenize = charTokenizer.tokenize(line);
                if (tokenize != null && !tokenize.isEmpty()) {
                    tokenizedList.add(tokenize);
                }
            }
            TextReader reader = new TextReader((Iterable<String>) tokenizedList, wordIndexer);
            KneserNeyLmReaderCallback kneserNeyReader = new KneserNeyLmReaderCallback(wordIndexer, 5, new ConfigOptions());
            reader.parse(kneserNeyReader);
            languageModel = LmReaders.readContextEncodedLmFromArpa(kneserNeyReader, wordIndexer, new ConfigOptions());
        } else {
            throw new IllegalArgumentException("illegal languagemodel extension: must be .bin or .arpa");
        }
        tmpList = new LinkedList<>();
        if (spaceSubs.isEmpty() || spaceSubs.length() > 1) {
            throw new IllegalArgumentException("parameter spaceSubs not correctly set");
        }
        spaceSubsChar = spaceSubs.charAt(0);
        WordIndexer<String> wordIndexer = languageModel.getWordIndexer();
        words = new LinkedList<>();
        for (int i = 0; i < wordIndexer.numWords(); i++) {
            words.add(wordIndexer.getWord(i).replace(spaceSubsChar, ' '));
        }
        substitutedList = new SubstitutedList(spaceSubs);
    }

    private ArrayEncodedProbBackoffLm loadLmBinFromFileOrRessource(String path) {
        try {
            return (ArrayEncodedProbBackoffLm) IO.load(path);
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public double getLogProb(List<String> phrase) {
        /*
        tmpList.clear();
        for (String string : phrase) {
            tmpList.add(string.replace(' ', spaceSubsChar));
        }
        return languageModel.getLogProb(tmpList);
         */
        substitutedList.internList = phrase;
        return languageModel.getLogProb(substitutedList);
    }

    @Override
    public List<String> getWords() {
        return words;
    }

    @Override
    public int getHistSize() {
        return languageModel.getLmOrder() - 1;
    }

    public double scoreSentence(List<String> sentence) {
        tmpList.clear();
        for (String string : sentence) {
            tmpList.add(string.replace(' ', spaceSubsChar));
        }
        return languageModel.scoreSentence(tmpList);
    }

    @Override
    public String getEoSSymb() {
        return languageModel.getWordIndexer().getEndSymbol();
    }

    @Override
    public String getBoSSymb() {
        return languageModel.getWordIndexer().getStartSymbol();
    }

    @Override
    public String getUnkSymb() {
        return languageModel.getWordIndexer().getUnkSymbol();
    }

    static class SubstitutedList extends AbstractList<String> {

        private final String sub;
        List<String> internList;

        SubstitutedList(String sub) {
            this.sub = sub;
        }

        @Override
        public String get(int index) {
            String s = internList.get(index);
            if (s.equals(" ")) {
                return sub;
            } else {
                return s;
            }
        }

        @Override
        public int size() {
            return internList.size();
        }
    }
}