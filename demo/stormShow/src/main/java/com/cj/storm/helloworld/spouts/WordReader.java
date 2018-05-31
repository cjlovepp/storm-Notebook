package com.cj.storm.helloworld.spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

/**
 * @author: cj
 * @create: 2018-05-29 15:21
 * @description: 按行读取文件
 **/
public class WordReader implements IRichSpout {

    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    private TopologyContext context;

    /**
     * 我们将创建一个文件并维持一个collector对象
     * @param conf
     * @param topologyContext
     * @param spoutOutputCollector
     */
    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        try {
            String fileName = conf.get("wordsFile").toString();
            String classPath = WordReader.class.getClassLoader().getResource("").getPath();
            this.context = topologyContext;
            System.out.println("====fileName:"+classPath+fileName);
            this.fileReader = new FileReader(classPath+fileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file ["+conf.get("wordFile")+"]");
        }
        this.collector = spoutOutputCollector;
    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    /**
     * 这个方法做的惟一一件事情就是分发文件中的文本行
     */
    @Override
    public void nextTuple() {
        //这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
        if(completed){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //什么也不做
            }
            return;
        }
        String str;
        //创建reader
        BufferedReader reader = new BufferedReader(fileReader);
        try{
            //读所有文本行
            while((str = reader.readLine()) != null){
                System.out.println("==新的一行=="+str);
                //按行发布一个新值
                this.collector.emit(new Values(str),str);
            }
        }catch(Exception e){
            throw new RuntimeException("Error reading tuple",e);
        }finally{
            completed = true;
        }
    }

    @Override
    public void ack(Object msgId) {
        System.out.println("OK:"+msgId);
    }

    @Override
    public void fail(Object msgId) {
        System.out.println("FAIL:"+msgId);
    }

    /**
     * 声明输入域"word"
     * @param outputFieldsDeclarer
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("line"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
