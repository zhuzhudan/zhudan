package com.study;

import com.study.service.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerApplication {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:dubbo-consumer.xml");

        HelloService service = context.getBean("helloService", HelloService.class);

        // 使用线程池进行方法调用
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        long start = System.currentTimeMillis();
        while (true){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executorService.execute(() -> service.methodA() );
            executorService.execute(() -> service.methodB() );
            executorService.execute(() -> service.methodC() );

            // long end = System.currentTimeMillis();
            // // 1分钟停止
            // if((end - start) > 60 * 1000 ){
            //     break;
            // }
        }
        // executorService.shutdown();
        // System.out.println("已完成");
    }
}
