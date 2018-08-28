import org.apache.log4j.*;
/**
 * Created by zw on 18-8-29.模拟日志产生
 *
 */
public class LoggerGenerator {
    private static Logger logger = Logger.getLogger(LoggerGenerator.class.getName());
    public static void main(String[] args) {
        int index = 0;

        while (true) {
            try {
                Thread.sleep(1000);
                logger.info("current value is " + index++);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
