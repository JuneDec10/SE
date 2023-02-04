package com.example.springboot.service;

import com.example.springboot.pojo.Vki;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VikServiceImpl {

    public Map<String,Object> search(String sea, Integer pageNum,Integer pageSize) throws Exception {
        Map<String,Object> result = new HashMap<>();
        List<Vki> vkiList = new ArrayList<>();
//        1.创建分词器，对搜索的关键词进行分词使用
//        ！！！创建分词器要和创建索引使用的分词器一模一样
        Analyzer analyzer = new IKAnalyzer();

//        不管是title还是text，只要有都查
//        查询的多个域名
        String [] fields = {"title","text"};
//        设置影响排序的权重，这里设置域的权重
        Map<String,Float> boots = new HashMap<>();
        boots.put("title",10000000f);
//        从多个域查询对象
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields,analyzer,boots);
//        设置查询的关键字
        Query query = multiFieldQueryParser.parse(sea);

//        设置格式器
        Formatter formatter = new SimpleHTMLFormatter("<font style=\"color:red\">","</font>");
//        通过scorer包装query实例
        Scorer scorer = new QueryScorer(query);
//        创建高亮器
        Highlighter highlighter = new Highlighter(formatter,scorer);
//        设置高亮的文本长度
        highlighter.setTextFragmenter(new SimpleFragmenter(Integer.MAX_VALUE-1));

//        4.创建Directory目录对象，指定索引库的位置
        Directory directory = MMapDirectory.open(Paths.get("E:\\lucenedir\\luceneindex"));
//        5.创建输入流对象
        IndexReader indexReader = DirectoryReader.open(directory);
//        6.创建搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        7.搜索，并返回结果
        TopDocs topDocs = indexSearcher.search(query,pageNum*pageSize);
//        获取查询到的结果集的总数
        long total = topDocs.totalHits;
        result.put("total",total);
//        8.获取结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//        9.遍历结果集

//        定义查询的起始行号以及结束的行号
        int startSize = (pageNum-1)*pageSize;
        long endSize = ((pageSize * pageNum)>total)?total:(pageSize*pageNum);

        if(scoreDocs!=null){
            for (int i = startSize;i<endSize;i++) {
                ScoreDoc s = scoreDocs[i];
                int docId = s.doc;
//                通过文档id，读取文档
                Document document = indexSearcher.doc(docId);
                Vki vki = new Vki();
//                通过域名，从文档中获取数值
                vki.setUrl(document.get("url"));
                vki.setTitle(document.get("title"));
                vki.setText(document.get("text"));
//                通过高亮器对title和text进行高亮处理
                String highlightTitle =  highlighter.getBestFragment(analyzer,"title", vki.getTitle());
                String highlightText = highlighter.getBestFragment(analyzer,"text", document.get("text"));
                vki.setTitle(highlightTitle);
                vki.setText(highlightText);
                vkiList.add(vki);
            }
        }
        result.put("vki",vkiList);
//        10.关闭流
        indexReader.close();
        return result;
    }
}
