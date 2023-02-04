package com.example.springboot;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.springboot.pojo.Vki;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@SpringBootTest
class SpringbootApplicationTests {

    //创建索引库
    @Test
    public void createIndexTest() throws Exception{
//        1.采集数据
        File docPath = new File("E:\\lucenedir\\lucenetext");//获得该文件夹下所有的数据集文件
        List<Document> documentList = new ArrayList<>();//创建document集合
//        2.创建文档对象
        for (File v: Objects.requireNonNull(docPath.listFiles())) {//遍历该文件夹中的每一个文档进行后续操作
            Document document = new Document();
//         创建域对象并且放入文档对象中  域名，域值，是否存储

//            是否分词：否，分词后无意义
//            是否索引：是
//            是否存储：是
            document.add(new StringField("url",v.getPath(),Field.Store.YES));

//            是否分词：是；譬如说：搜索“计算机”，可以找到“计算机科学”，不分词就无法找到
//            是否索引：是
//            是否存储：是
            document.add(new TextField("title",v.getName(), Field.Store.YES));

//            是否分词：是
//            是否索引：是
//            是否存储：是
            document.add(new TextField("text",FileUtils.readFileToString(v), Field.Store.YES));

//            将文档对象放入文档集合中
            documentList.add(document);
        }
        long start = System.currentTimeMillis();
//        3.创建分词器为IKAnalyzer
        Analyzer analyzer = new IKAnalyzer();
//        4.创建directory目录对象，目录对象表示索引库的位置
        Directory dir = MMapDirectory.open(Paths.get("E:\\lucenedir\\luceneindex"));
//        5.创建IndexWriterConfig对象，制定了切分词使用的分词器
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
//        优化IO，一万条数据写一次
        config.setMaxBufferedDocs(10000);
//        6.创建IndexWriter输出对象，指定输出的位置使用的config初始化对象
        IndexWriter indexWriter = new IndexWriter(dir,config);
//        7.写入文档到索引库
        for (Document d:documentList) {
            indexWriter.addDocument(d);
        }
//        8.释放资源
        indexWriter.close();
        long end = System.currentTimeMillis();
        System.out.println("本次建立索引所花时间为： "+(end-start)+" ms");
    }

    //    测试搜索过程
    @Test
    public void testSearch() throws ParseException, IOException {
//        1.创建分词器，对搜索的关键词进行分词使用
//        ！！！创建分词器要和创建索引使用的分词器一模一样
        Analyzer analyzer = new IKAnalyzer();
//        2.创建查询对象,第一个参数：默认查询域，如果查询的关键字带搜索的域名，不带则从默认的查;第二个参数：使用的分词器
        QueryParser queryParser = new QueryParser("title",analyzer);
//        3.设置搜索关键词
        Query query = queryParser.parse("鲁迅");
//        4.创建Directory目录对象，指定索引库的位置
        Directory directory = MMapDirectory.open(Paths.get("E:\\lucenedir"));
//        5.创建输入流对象
        IndexReader indexReader = DirectoryReader.open(directory);
//        6.创建搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        7.搜索，并返回结果
        TopDocs topDocs = indexSearcher.search(query,10);
//        获取查询到的结果集的总数打印
        System.out.println("====total:===="+topDocs.totalHits);
//        8.获取结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//        9.遍历结果集
        if(scoreDocs!=null){
            for (ScoreDoc s:scoreDocs) {
                int docId = s.doc;
//                通过文档id，读取文档
                Document document = indexSearcher.doc(docId);
                System.out.println("===========================");
//                通过域名，从文档中获取数值
                System.out.println("===id=="+document.get("id"));
                System.out.println("===id=="+document.get("url"));
                System.out.println("===id=="+document.get("title"));
                System.out.println("===id=="+document.get("text"));
            }
        }
//        10.关闭流
        indexReader.close();
    }

    @Test
    void contextLoads() {
       // System.out.println(vki.findOne());
    }

    @Test
    void deletedIndex() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory directory = FSDirectory.open(Paths.get("E:\\lucenedir"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,config);
        indexWriter.deleteAll();
        indexWriter.close();
    }

    @Test
    void FileTest() throws Exception{
        Directory directory = FSDirectory.open(Paths.get("E:\\lucenedir\\index01"));
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //        优化IO，一千条数据写一次
        config.setMaxBufferedDocs(1000);
        IndexWriter indexWriter = new IndexWriter(directory,config);
        File file = new File("E:\\Desktop\\wiki_zh_2019\\wiki_zh\\AB");
        for (File f:file.listFiles()
             ) {
            String fileName = f.getName();
            String filePath = f.getPath();
            String fileContent = FileUtils.readFileToString(f);

        }
    }

    //判断文件名是否正确，不能包括 \ / : * ? " < > | ”
    boolean isValidFileName(String fileName) {
        if (fileName == null || fileName.length() > 255) return false;
        else return fileName.matches("[^\\s\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\/:\\*\\?\"<>\\|])*[^\\s\\/:\\*\\?\\\"<>\\|\\.]$"); }

    @Test
    void FileCut() throws Exception{
        //维基百科数据集的存放位置
        File docPath = new File("E:\\Desktop\\wiki_zh_2019\\wiki_zh\\AB");
        for(File f:docPath.listFiles()){
            String fileName = f.getName();
            //拼接文件夹和文件名，得到文件的url
            FileReader fileReader = new FileReader("E:\\Desktop\\wiki_zh_2019\\wiki_zh\\AB\\"+fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String row;
            Vki v = new Vki();
            //数据集文件中的一条数据即为一条维基百科，将这一条维基百科切分出来，标题作为文件名，text作为文件内容
        while((row= bufferedReader.readLine())!=null){
            JSONObject jsonObject = JSON.parseObject(row);
            String s = (String)jsonObject.get("title");
            //选用维基百科的标题作为文件名，但其中有一些标题并不是完全符合文件名的要求，需要将不符合的文件跳过
            if(isValidFileName(s)==false) continue;
            v.setTitle(s);
            v.setText((String)jsonObject.get("text"));
            //创建新的文件
            FileWriter fw = new FileWriter("E:/lucenedir/lucenetext/"+v.getTitle()+".txt");
            fw.append(v.getText());
            fw.close();
        }
        }
    }

    @Test
    void testttt() throws Exception{
        //        1.采集数据
        Vki v = new Vki();
        v.setUrl("E:\\Desktop\\wiki_zh_2019\\wiki_zh\\AB\\wiki_00");
        FileReader fileReader = new FileReader("E:\\Desktop\\wiki_zh_2019\\wiki_zh\\AB\\wiki_00");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String row;
        String s="";
        while((row= bufferedReader.readLine())!=null){
            s=s+row;
        }
        v.setText(s);
        v.setTitle("2222");
        List<Document> documentList = new ArrayList<>();
//        2.创建文档对象

            Document document = new Document();
//         创建域对象并且放入文档对象中  域名，域值，是否存储

//            是否分词：否，分词后无意义
//            是否索引：
//            是否存储：
            document.add(new StringField("url",v.getUrl(),Field.Store.YES));

//            是否分词：是；譬如说：搜索“计算机”，可以找到“计算机科学”，不分词就无法找到
//            是否索引：是
//            是否存储：是
            document.add(new TextField("title",v.getTitle(), Field.Store.YES));

//            是否分词：是
//            是否索引：
//            是否存储：
            document.add(new TextField("text",v.getText(), Field.Store.YES));

//            将文档对象放入文档集合中
            documentList.add(document);

//        3.创建分词器,对中文是单字分词
        Analyzer analyzer = new IKAnalyzer();
//        4.创建directory目录对象，目录对象表示索引库的位置
        Directory dir = FSDirectory.open(Paths.get("E:\\lucenedir"));
//        5.创建IndexWriterConfig对象，制定了切分词使用的分词器
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
//        优化IO，一千条数据写一次
//        6.创建IndexWriter输出对象，指定输出的位置使用的config初始化对象
        IndexWriter indexWriter = new IndexWriter(dir,config);
//        7.写入文档到索引库
        for (Document d:documentList) {
            indexWriter.addDocument(d);
        }
//        8.释放资源
        indexWriter.close();
    }

    public void search(String sea, Integer pageNum,Integer pageSize) throws Exception {
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
        Formatter formatter = new SimpleHTMLFormatter("<span style=\"color:red\">","</span>");
//        通过scorer包装query实例
        Scorer scorer = new QueryScorer(query);
//        创建高亮器
       Highlighter highlighter = new Highlighter(formatter,scorer);
       highlighter.setTextFragmenter(new SimpleFragmenter(Integer.MAX_VALUE-1));
        long start = System.currentTimeMillis();
//        4.创建Directory目录对象，指定索引库的位置
        Directory directory = MMapDirectory.open(Paths.get("E:\\lucenedir\\luceneindex"));
//        5.创建输入流对象
        IndexReader indexReader = DirectoryReader.open(directory);
//        6.创建搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        7.搜索，并返回结果
        TopDocs topDocs = indexSearcher.search(query,2281,Sort.RELEVANCE);
        long end = System.currentTimeMillis();
        System.out.println("本次搜索所花时间为： "+(end-start)+" ms");
//        获取查询到的结果集的总数打印
        long total = topDocs.totalHits;
        System.out.println("total:"+total);
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
               String highlightText = highlighter.getBestFragment(analyzer,"text", vki.getText());
               vki.setTitle(highlightTitle);
                vki.setText(highlightText);
                System.out.println(highlightText);
                vkiList.add(vki);
//                System.out.println(highlightTitle);
            }
        }
//        10.关闭流
        indexReader.close();
    }

    @Test
    void SearchTest() throws Exception {
        search("北京",2280,1);
    }
}
