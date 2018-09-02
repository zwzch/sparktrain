package project.spark.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RetriesExhaustedWithDetailsException;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.sql.Connection;

/**
 * Created by zw on 18-9-2.
 */
public class HBaseUtil {

    private Configuration configuration = null;
    private Connection connection = null;

    private static HBaseUtil instance = null;
    private HBaseAdmin hBaseAdmin= null;
    private HBaseUtil() {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.rootdir", "hdfs://master:9000/hbase");
        configuration.set("hbase.zookeeper.quorum", "slave1,slave2,slave3");
        try {
            hBaseAdmin =new HBaseAdmin(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized HBaseUtil getInstance() {
        if (null == instance) {
            instance = new HBaseUtil();
        }
        return instance;
    }

    public HTable getTable(String tableName) {
        HTable table = null;
        try {
            table = new HTable(configuration,tableName);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;
    }
    //表名 rowKey 列族 列名 值
    public void put(String tableName, String rowKey, String familyName, String qualifier, String value) {

        HTable table = getTable(tableName);
        Put put = new Put(Bytes.toBytes(rowKey));
        put.add(Bytes.toBytes(familyName), Bytes.toBytes(qualifier),Bytes.toBytes(value));
        try {
            table.put(put);
        } catch (InterruptedIOException e) {
            e.printStackTrace();
        } catch (RetriesExhaustedWithDetailsException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        try {
//            System.out.println(HBaseUtil.getInstance().getHTable("course_clickcount").getTableDescriptor().getRegionReplication());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        HBaseUtil.getInstance().put("course_clickcount", "20171111_1", "info", "click_count", "1");
//        System.out.println( HBaseUtil.getInstance().getHTable("course_clickcount").toString());
        HBaseUtil.getInstance().put("course_clickcount", "20171111_1", "info", "click_count", "1");
    }
}