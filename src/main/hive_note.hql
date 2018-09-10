Hive 编程指南

hive on EMR
弹性MapReduce 性价比比较高 对新工具新创意测试
先使用小实例 使用Ganglia监控性能
确保EC2实例 键对 安全组 和EMR工作流在同一个区域中
EMR AWS管理控制台 规模增大 使用其他方式进行搭建
EMR elastic-mapreduc CLI
EMR API 缺点可能不够新
Amazon Thrift端口号是改变的
EMR 管理者master实例组（namenode jobtracker）
核心实例组(datanode taskTracker)(集群停止数据丢失) 结点个数不能减少
任务实例组(task tracker) 可以增加和减少 降低成本又不会丢数据

s3n性能更好
部署hive-site.xml文件
安装hive --instal-hive --hive-versions
安装hive-site 指定文件脚本
elastice --brotstrapmapreduce 配置内存密集性任务的机器

EMR hive元数据的存储
1)使用JDBC连接元数据存储
2）使用初始化脚本 startup.q 或者使用.hiverc自动调用开机启动
3）在S3上进行Mysql的dump工作

EMR中的HDFS和S3
中间数据存储在HDFS中 需要持久化的数据再存储到S3中
数据在s3中的缺点 无法使用hadoop本地化数据处理优化 在处理前就将这些热数据导入到hdfs中 然后在进行处理
就可以在后续使用hadoop本地的处理优化

用户需要将所有初始化脚本 配置脚本 资源文件 UDF/Streaming 用到的JAR文件上传到S3上

S3上的日志
elatic-mapreduce下的creentdials.json配置
--log-uri
注意要频繁的清除日志 减少产生的存储成本

现买现卖
1）将中间结果存储到S3中
2）只把任务实例作为先买现卖结点 可以加快整个工作流
先买现卖任务可能导致太多次的map失败导致hadoop
可以通过修改mapred.reduce.max.attempts,mapred.map.max.attempts
修改任务的最多尝试次数
依赖太多的现买现卖实例导致无法推测执行或者任务失败
如果想访问EMR的9100 9101端口 通过ssh 或者同台sock

启动EMRhive对S3文件系统的优化
set hive.optimize.s3.query=true

alter table xxx recover partitions;

Tez有以下几个特色：
（1） 丰富的数据流接口；
（2） 扩展性良好的“Input-Processor-Output”运行模型；
（3） 简化数据部署（充分利用了YARN框架，Tez本身仅是一个客户端编程库，无需事先部署相关服务）
（4） 性能优于MapReduce
（5） 优化的资源管理（直接运行在资源管理系统YARN之上）
（6） 动态生成物理数据流

hive on tez，直接配置hive支持tez，其实是在yarn上面启动一个app,这个app永久运行,接受不同sql，除非退出hive cli此app才会结束
tez runing app在yarn上面和spark on yarn很类似，而且tez,spark底层原理也很相似！
在切换底层为mr引擎的时候也会重启app运行，但是tez那个app也没结束，等待mr app运行结束后,两个一起结束！

原本打算自己编译的，发现编译到tez-ui的时候总是报错，除非跳过不使用tez-ui；多次编译都是失败了，暂时先放一边了；直接使用官网已经编译好的包吧；

备注：Tez-ui是一个 Web界面可以看到Job的信息的执行时间，通过它可以更好、更直观的了解到程序慢在哪个位置；

hadoop -->    mapreduce     -->    sql,hql(hive)
(hdfs)      (特别的计算模型)        (不需要快速出结果)
限制：
1）不支持记录级别更新插入删除 由于mapreduce 时间长 hive不支持事务

2) 类似OLAP工具
   OLTP On-Line Transaction Processing
   这样做的最大优点是可以即时地处理输入的数据，及时地回答。也称为实时系统(Real time System)。
   衡量联机事务处理结果的一个重要指标是系统性能，具体体现为实时请求-响应时间(Response Time)，
   即用户在终端上输入数据之后，到计算机对这个请求给出答复所需要的时间。OLTP是由前台、应用、数据库共同完成的，
   处理快慢以及处理程度取决于数据库引擎、服务器、应用引擎。OLTP 数据库旨在使事务应用程序仅写入所需的数据，以便尽快处理单个事务。
   Online Analytical Processing hive 不满足联机部分
   hadoop+hbase emr/ec2+dynamoDB

Map + Reducer
map 集合的一种形式到另一种形式
reudcer 键-值对 输入和输出的键值是不同
hadoop 备份3份 64M或者倍数
减少写入读取次数
input -> mappers -> shuffle(重新分发)/combine(小范围sort)/sort -> reducers -> output

hive cli hwi(网页界面) jdbc odbc thrift
hive -> job执行计划 -> 执行mapper/reducer模块 hive网关机 -> NameNode
pig 数据流 步进式的数据流更加直观
hbase 行级别的数据更新 快速查询 行级事务
Hbase 只查询部分列会很快
Hbase 内存缓存技术 + hdfs
本地文件的内存患处技术 数据更新操作日志
没有使用mapreduce的工具
spark（shark） storm kafka
hadoop
单点 伪分布式 分布式
hive内部是什么
$HIVE_HOME/lib
Thrift 提供了了远程访问其他进程
hive客户端 ---> metastoreservice
使用Derby数据库是 用户不可以并发两个hiveCLI实例
derby --> metastore_db
默认路径是user/hive/warehouse hive.metastore.local hive类路径要有jdbc
hive --auxpath 允许用户指定以猫号分割的附属的jar包
--config 指定一个新的配置文件的目录
set 命令 显示修改变量值
set, set -v所有变量 hadoop hdfs mapreduce 的属性
set 还可以给变量赋新的值
hive --define 定义变量
hive --define foo=bar --hivevar --define的标记是相同的
set hivevar:foo=bar;
create table toss1(i int, ${hivevar:foo} string);
system: 命名空间 Java可读可写 对env：只能读
hivevar rw 用户自定义变量
hiveconf rw Hive相关的配置属性
system rw  Java定义的配置属性
env    r    shell环境 bash环境的变量
hive -e 一次执行命令
hive -s 静默模式 去掉 ok 和 Time taken等行
hive -f 执行指定文件中一个多个查询语句 *.q *.hql
hive shell中可使用source来执行一个脚本文件
.hiverc 文件中有内容在cli启动前自动执行文件命令
历史记录在~/.hivehistory
hiveshell中 !简单的shell命令;
hiveshell中 dfs -ls / ;更高效的dfs的命令
set hive.cli.print.header=true cli 打印出字段名称
hive 脚本使用--进行注释
hive 基本数据类型
TINYINT 1byte
SMALINT 2byte
INT 4byte
BIGINT 8byte
BOOLEAN true false
FLOAT 单精度
DOUBLE 双精度
STRING 单引号双引号
TIMESTAMPS UTC时间
BINARY 字节数组
对应java中的数据类型
在Hive宽松的世界里 限制长度并不重要
BINARY和BLOB并不相同
cast(s AS INT)
Hive中支持struct map array
3种集合数据类型
struct('John', 'Doe')
访问使用.字段名
map('first','JOIN','last','Doe')
键值对的组合
数组名['first']
Array('John','Doe')
数组名[1]
进行封装提高吞吐量的数据 最少的头部寻址来提供查询速度
create table employees (
    name STRING,
    salary FLOAT,
    subordinates Array<STRING>,
    deductions Map<STRING,FLOAT>,
    address struct<street:STRING>
)
注意struct一旦声明好结构 位置就不会改变
hive 中默认的记录和字段分割符
\n 使用文本文件的每行
^A 8进制编码
^B map键值对的分割
^C 键和值的分割
用户可以使用\t字段分割符

传统数据库是写时模式 schema on write 数据在写入数据库是对模式进行检查 写入时对模式进行检查
hive 是读时模式 schema on read 不会再数据加载时验证 在查询时进行验证

HiveQL hive 查询语言 hql
hive 不支持行级插入操作 更新操作和删除操作
hive 不支持事务
hive 数据库表是目录命名空间 
默认default
create database if not exists financials
show databases like 'mobula.*' 查询mobula开头的数据库 
default没有自己的目录  数据库目录是*.db
hive.metastore.warehouse.dir 表示存储的目录
describe database 数据库名  查看数据库的描述信息 
s3n://的性能更好 
DROP database if exists 数据库名 cascade
cascade 可以使hive自行先删除数据库中的表
restrict 则要先删除数据库的目录

alter database test set dbproperties('xxx'='xxx')
修改数据库的描述信息

create table if not exists 表存在 忽略后面的语句
hive 两个表属性 last_modified_by 最后修改表的用户名
last_modified_time 最后一次修改的时间
show tables in mydb
describe extended xxx
describe formatted 格式更加规范
describe extended 库名.表名.列名

内部表（管理表）hive控制着数据的生命周期
外部表 表是外部的 删除表不会删除掉数据
只会删除元数据
语句中省略掉external 源表是外部表 呢么新表也将是外部表

分区表 提高查询的性能 
内表 ：分层存储
set hive.mapred.mode = strict
set hive.mapred.mode = nonstrict
strict 表示where中没有分区过滤的化会禁止提交任务
show partitions 表名
外部表：可以使用分区 共享数据 优化查询性能
alter table xxx partition()
location '';
动态添加分区
1.distcp 将分区下的数据拷贝到s3中
2.修改外部表的分区路径
3.hadoop fs -rmr path
删除hdfs中的分区数据
describe extended 表名 partition ();
查看分区数据所在的路径
distcp的优势
1.执行的分布式特性
之前在上文中已经提到过,DistCp本身会构造成一个MR的Job.他是一个纯由Map Task构成的Job,注意是没有Reduce过程的.
所以他能够把集群资源利用起来,集群闲下来的资源越多,他跑的越快.下面是Job的构造过程:
2.高效的MR组件
高效的MR组件的意思DistCp在相应的Job时,提供了针对此类型任务的Map Class,InputFormat和OutputFormat,分别是CopyMapper, DynamicInputFormat, CopyOutputFormat.这三者MR设置类型与普通的MR类型有什么区别呢,答案在下面
通过指定第三方的输入输出格式和Serde 使hive支持其他广泛的文件格式

删除表
drop table if exists employees
外部表 不会删除数据
管理表 表的元数据和表内数据都会被删除
hadoop 
/user/$USER/.trash 目录
fs.trash.interval 可以将间隔设置的合理
修改表
alter table 
操作会修改元数据 不会修改数据本身
alter table log_messages rename to xxx;
alter table log_messages add if not exists
如果没有分区就增加分区

alter table xxx CHANGE COLUMN old_column_name new_column_name column_data_type COMMENT 'column_comment';
对hive中的某个字段重命名

alter table log_messages add columns()
在已经有的字段后面增加字段

alter table log_messages replace columns
替换列位新列
 replace 的语句只能使用一下两种SerDe的表
 DynamicSerDe 
 MetadataTypedColumnsetSerDe

 alter table log_messages partition () set fileformat seqencefile
 修改存储格式和Serde属性 
 分区表要指定一个新的Serde
 alter table table_using_JSON set serdeproperties()
 向已经存在的SerDe增加新的serdeproperties属性
 cluster by into bucket 是必选的
 alter table archive partition 把分区内的文件打成一个HAR文件 建成 NameNode的压力
 使用UNARCHIVE 就可以反响操作

如何将数据转载到内部表中 
load data local inpath ''
overwrite into table xxx
partition ();
目录不存在 先创建分区目录 数据拷贝到目录下
local 表示的使用的是本地文件系统
如果没有local是 hadoop中的目录
全路径会有更好的鲁棒性
指定了overwrite 会将之前的数据先删除掉
没有使用overwrite是会将保留文件为文件名_序列号
使用inpath要注意文件路径下不能有任何文件夹
hive不会验证内容 但是会验证格式

INSERT INTO TABLE schema_name.table_name
SELECT
    column_names
FROM
    source_schema_name.source_table_name;
通过查询语句向表中插入数据

hive提供了动态分区的功能 根据查询参数推断分区
静态分区键必须出现在动态分区之前
设置动态分区
set hive.exec.dynamic.partition = true;
set hive.exec.dynamic.partition.mode = nonstrict
set hive.exec.max.dynamic.partitions.pernode=1000
设置期望的属性
create table xxx AS
SELECT  column_names
FROM    table_name;
通过单个查询语句创建表并加载数据

导出数据 
格式正确的
    hadoop fs -cp source_path target_path

或者
    INSERT DIRECTORY 'path'
    SELECT
        column_names
    FROM
        source_schema_name.source_table_name;
导出数据
数据多少取决于reduce的个数

或者创建一个临时表 将表的存储方式配置成为期望的数据 再从表中查询数据
tips 临时表要自己删除

HIVE 查询
SELECT   
FROM    table_name;
select sql中的影射算子
select 表示了从哪个表 视图嵌套查询中选择记录
from 表示从哪个表 视图 嵌套查询中选择记录
引用不存在的元素会返回NULL
STRING 将不再加引号
map元素可以空arrayp[...] 或者点
使用正则查询列
hive 支持所有的典型运算符
A+B
A-B
A * B 
A/B 
A%B 按位与
A|B 按位或
A^B 按位异或
～A 按位取反
数学函数
round() 近似值
round(DOUBLE d, int n) 保留n位小数的DOUBLE的近似值
floor(DOUBLE d) 返回 <=d 的最大BIGINT
ceil(DOUBLE d)
ceiling(DOUBLE d) 返回>=d 最小BIGINT
rand
rand(int seed) 取随机数
exp(DOUBLE d)  幂次方
ln()
log10()
log()
pow(d,p) d 的p次幂
sqrt()开方
bin()返回二进制
hex（）
unhex()
conv(a,b,c) 将a从b进制转换成c进制
abs() 绝对值
pmod() 取模
sin()
asin()
cos()
acos()
positive(INT/DOUBLE) 返回Int型值i
negative() 返回负数
聚合函数 多行返回一行的值 groupby

count(*) 计算总行数 有NULL
count(expr) 提供表达式的非NULL行数
count(Distinct) 重排后的行数
sum(col)
sum(Distinct col)
avg()
min()
max()
variance(col) 方差
var_samp() 样本方差
percentfile_approx()
collect_set（）col元素从拍后的数组
set hive.map.aggr = true
提高聚合性能 触发在map阶段的聚合过程
表生成函数 
一列扩充成多列
explode(Array ) 返回 0到多行的结果
explode(Map)
inline(Array) 提取出结构数组出入表中
json_tuple()
parse_url_tuple()从url中解析出n个部分的信息

ascii()返回ascii的整数值
base64()
binary()转换成二进制
cast() 失败返回NULL
concat() 拼接字符春
concat_ws()使用分割符拼接
decode()
encode()
find_in_set()都好分割的字符串中s出现的位置
format_number()
get_json_object()
xxx in (aaa,bbb)  在元组中返回true
in_file(String s, String filename) 文件中有匹配的值就返回true
instr() 子串第一次出现的位置
length()
locate(sbustr,str,pos) pos 位置之后子串第一次出现的位置
lower（）变小写
lcase（） 同上
ltrim() 去除左空格
lpad() 对字符串从左开始填充
parse_url()
printf()
regexp_extract() 抽取正则
regexp_replace() 正则替换
repeat(s,n) 输出n次s
reverse()反转
rpad()
rtrim() 
sentences(s,long ,locale) 字符串转化成句子数组
size
space(n) 返回n个空格
split（）返回分割的数组
str_to_map()
substr()
trim
unbase64()
upper()大写
from_unixtime()时间戳->UTC时间
unix_timestamp() 当下时间戳
unix_timestamp(String date)
to_date(timestamp)
year
month
day 
hour 
minute()
second()
weekofyear()在一年的第几周中
datediff()时间相差的天数
date_sub()
date_add(String)
增加多少天

limit 
列别名
case when then 

select * 是不需要mapreduce操作的
set hive.exec.model.local.auto=true 
hive会尝试用本地模式执行其他的查询
where 不可以使用列别名 可以用嵌套的select语句

A=B TRUE false
a <=> b a和b都是NULL 也返回true
a<>b a!=b a不等于b
a<b a<=b a>b a>=b
a [not] between b and c 
a is NULL 
A like B  B简单的正则
A RLIKE B B是java中的正则
对于浮点数操作 钱要cast成float
避免窄类型向宽类型的转换
rlike 是更强大的like
having（）更简单的过滤了groupby的数据 避免了子查询
hive 只支持等值连接 pig中可以实现非等值的连接
inner join 两边都有的数据
hive 会线缓存左边的表的数据 最后扫描表进行计算 用户要保证 表是从右向左递增的
hvie提供了一个标记来显示呢个表是最大表
mapside join