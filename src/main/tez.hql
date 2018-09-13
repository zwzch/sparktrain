灵活的输入-处理器-输出
最佳资源管理 运行是计划重新配置 动态数据流决策
多个mr --> 1个tez中
https://tez.apache.org/releases/0.8.4/tez-api-javadocs/configs/TezConfiguration.html

set tez.grouping.min-size = 64*1024*1024
set tez.grouping.max-size = 128*1024*1024
set hive.tez.auto.reducer.parallelism=true 
set hive.exec.reducers.bytes.per.reducer=1073741824;
set tez.shuffle-vertex-manager.min-src-fraction=0.25；
set tez.shuffle-vertex-manager.max-src-fraction=0.75；
