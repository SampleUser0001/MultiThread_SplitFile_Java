package ittimfn.sample;

import java.nio.file.Path;
import java.util.Random;

/**
 * Hello world!
 *
 */
public class App {

    public static String APP_HOME = Path.of(System.getProperty("user.dir")).toString();

    public static void main( String[] args ) {
        int poolSize = 3;
        MultiThreadController<Integer> controller = new MultiThreadController<Integer>(poolSize){
            @Override
            public Integer getKey(String value) {
                Integer key = Integer.parseInt(value) % poolSize;
                return key;
            }
        };

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            controller.put(String.valueOf(random.nextInt(1000)));
        }
        controller.shutdown();
    }
}
