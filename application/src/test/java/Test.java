import com.threathunter.babel.meta.ServiceMeta;
import com.threathunter.babel.meta.ServiceMetaUtil;
import com.threathunter.babel.rpc.impl.ServiceClientImpl;

public class Test {
    public static void main(String[] args) {
        String fileName = "httplog_redis.service";
        ServiceMeta meta;
        ServiceClientImpl client;
        meta = ServiceMetaUtil.getMetaFromResourceFile("babel/" + fileName);
        client = new ServiceClientImpl(meta);
        client.bindService(meta);
        client.start();

        while (true) {

        }
    }
}
