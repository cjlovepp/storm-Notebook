package com.cj.storm.helloworld;

import com.cj.storm.helloworld.bolts.WordCounter;
import com.cj.storm.helloworld.bolts.WordNormalizer;
import com.cj.storm.helloworld.spouts.WordReader;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.NotAliveException;
import org.apache.storm.topology.TopologyBuilder;

/**
 * @author: cj
 * @create: 2018-05-29 15:20
 * @description:
 **/
public class TopologyMain {

    public static void main(String[] args) throws InterruptedException, NotAliveException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        //Topology definition
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader",new WordReader());
        builder.setBolt("word-normalizer", new WordNormalizer())
                .shuffleGrouping("word-reader");
        //多个线程之间分组的控制
//        builder.setBolt("word-counter", new WordCounter(),2)
//                .fieldsGrouping("word-normalizer", new Fields("word"));
        builder.setBolt("word-counter",new WordCounter(),2)
                    .shuffleGrouping("word-normalizer");

        //Configuration
        Config conf = new Config();
        conf.put("wordsFile", "wordsFile.txt");
        conf.setDebug(true);
        //Topology run
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);


        if (args != null && args.length > 0) {
            // parallelism hint to set the number of workers
            conf.setNumWorkers(3);

            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        }

        // Otherwise, we are running locally
        else {

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("Getting-Started-Toplogie", conf, builder.createTopology());
            Thread.sleep(10000);
            cluster.killTopology("Getting-Started-Toplogie");
            cluster.shutdown();

        }
    }


}

