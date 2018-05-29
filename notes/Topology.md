# <center> :rainbow: 拓扑</center>

> 说明拓扑的相关概念及创建

<!-- MarkdownTOC -->

- [数据流组](#%E6%95%B0%E6%8D%AE%E6%B5%81%E7%BB%84)

<!-- /MarkdownTOC -->

## 数据流组

> 设计一个拓扑时，你要做的最重要的事情之一就是定义如何在各组件之间交换数据（数据流是如何被 bolts 消费的）。一个据数流组指定了每个 bolt 会消费哪些数据流，以及如何消费它们。

- `随机数据流组:`随机流组是最常用的数据流组。它只有一个参数（数据源组件），并且数据源会向随机选择的 bolt 发送元组，保证每个消费者收到近似数量的元组。随机数据流组用于数学计算这样的原子操作。

- `域数据流组:`域数据流组允许你基于元组的一个或多个域控制如何把元组发送给 bolts。 它保证拥有相同域组合的值集发送给同一个 bolt。
```java
···
    builder.setBolt("word-counter", new WordCounter(),2)
           .fieldsGrouping("word-normalizer", new Fields("word"));
···  
```
  ** NOTE **:在域数据流组中的所有域集合必须存在于数据源的域声明中。

- `全部数据流组:`全部数据流组，为每个接收数据的实例复制一份元组副本。这种分组方式用于向 bolts 发送信号。比如，你要刷新缓存，你可以向所有的 bolts 发送一个刷新缓存信号。
```java
   public void execute(Tuple input) {
        String str = null;
        try{
            if(input.getSourceStreamId().equals("signals")){
                str = input.getStringByField("action");
                if("refreshCache".equals(str))
                    counters.clear();
            }
        }catch (IllegalArgumentException e){
            //什么也不做
        }
        ···
    }
```
我们添加了一个 if 分支，用来检查源数据流。 Storm 允许我们声明具名数据流（如果你不把元组发送到一个具名数据流，默认发送到名为 ”default“ 的数据流）。这是一个识别元组的极好的方式，就像这个例子中，我们想识别 signals 一样。 在拓扑定义中，你要向 word-counter bolt 添加第二个数据流，用来接收从 signals-spout 数据流发送到所有 bolt 实例的每一个元组。
```java
 builder.setBolt("word-counter", new WordCounter(),2)
           .fieldsGroupint("word-normalizer",new Fields("word"))
           .allGrouping("signals-spout","signals");   
```

- `自定义数据流组:`

- `直接数据流组:`

- `全局数据流组:`

