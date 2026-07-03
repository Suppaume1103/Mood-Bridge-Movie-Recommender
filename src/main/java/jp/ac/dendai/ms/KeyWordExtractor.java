package jp.ac.dendai.ms;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer; // 英語のトークナイズ・ストップワード除去・語幹化を行うクラス
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader; // インデックスの読み取りを行うクラス
import org.apache.lucene.index.IndexWriter; // インデックスの作成・更新を行うクラス
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.ByteBuffersDirectory; // メモリ上にインデックスを作成するための保存領域の実装
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

/**
 * レビュー群からLucene TF-IDFで重要語を抽出するクラス。
 * TMDBのレビューは英語(en-US)のため、EnglishAnalyzerを使用する。
 */
public class KeyWordExtractor {

    private static final int TOP_N = 10;
    private static final String FIELD = "content";

    public KeyWordExtractor() {
    }

    /**
     * レビュー群からTF-IDFスコア上位の重要語を抽出する
     * 
     * @param reviews レビューのリスト
     * @return 重要語のリスト(TF-IDFスコア降順、最大TOP_N件)
     */
    public List<String> extractKeywords(List<Review> reviews) throws IOException {
        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
        fieldType.setStored(false);
        fieldType.setTokenized(true);
        fieldType.setStoreTermVectors(true); // 単語の出現頻度を取得するためにフィールドにTermVectorを保存するよう設定
        fieldType.freeze();

        Map<String, Double> scores = new HashMap<>();

        // インデックス構築
        try (Analyzer analyzer = new EnglishAnalyzer();
                Directory directory = new ByteBuffersDirectory()) {

            try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
                for (Review review : reviews) {
                    Document doc = new Document();
                    doc.add(new Field(FIELD, review.getContent(), fieldType));
                    writer.addDocument(doc);
                }
            }

            // TF-IDFスコア計算
            try (IndexReader reader = DirectoryReader.open(directory)) {
                int numDocs = reader.numDocs();
                for (int docId = 0; docId < numDocs; docId++) {
                    Terms termVector = reader.termVectors().get(docId, FIELD);
                    if (termVector == null) {
                        continue;
                    }
                    TermsEnum termsEnum = termVector.iterator();
                    BytesRef term;
                    while ((term = termsEnum.next()) != null) {
                        String word = term.utf8ToString();
                        long termFreq = termsEnum.totalTermFreq();
                        int docFreq = reader.docFreq(new Term(FIELD, term));
                        double idf = Math.log((double) numDocs / (docFreq + 1)) + 1;
                        double tfIdf = termFreq * idf;
                        scores.merge(word, tfIdf, Double::sum);
                    }
                }
            }
        }

        // TF-IDFスコア上位TOP_Nの重要語を抽出
        return scores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(TOP_N)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
