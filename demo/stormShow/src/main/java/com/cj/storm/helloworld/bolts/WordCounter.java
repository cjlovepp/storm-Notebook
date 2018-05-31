package com.cj.storm.helloworld.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: cj
 * @create: 2018-05-29 15:47
 * @description: 负责为单词计数。这个bolt没有发布新的数据，只是将数据保存在map中已做计数用。实际情况中可将数据持久化到DB中
 **/
public class WordCounter extends BaseBasicBolt {

    Integer id;
    String name;
    Map<String,Integer> counters;
    private OutputCollector collector;

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        String str=tuple.getString(0);
        //如果单词尚不存在于map，我们就创建一个，如果已在，我们就为它加1
        if(!counters.containsKey(str)){
            counters.put(str,1);
        }else{
            Integer c = counters.get(str) + 1;
            counters.put(str,c);
        }
        //对元组作为应答
//        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        //啥也不干
    }

    /**
     * 初始化
     * @param stormConf
     * @param context
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        this.counters = new HashMap<String, Integer>();
        this.collector = collector;
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
    }

    /**
     * 这个spout结束时（集群关闭的时候），我们会显示单词数量
     */
    @Override
    public void cleanup() {
        System.out.println("-- 单词数 【"+name+"-"+id+"】 --");
        for(Map.Entry<String,Integer> entry : counters.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue());
        }
    }
}
