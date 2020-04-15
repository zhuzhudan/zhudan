package com.study.filter;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.*;
import java.util.concurrent.*;

@Activate(group = {CommonConstants.PROVIDER})
public class TpMonitorFilter implements Filter {
    //保存方法名与方法Tp指标的map
    private static Map<String, Map<Long, Long>> methodsTpMap = new ConcurrentHashMap<>();

    static {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(new Runnable() {
                    @Override
                    public void run() {
                        if (methodsTpMap.size() > 0){
                            System.out.println("===============1分钟内个方法TP90、TP99耗时情况===============");
                        }
                        for (Map.Entry<String, Map<Long, Long>> methodTpMap : methodsTpMap.entrySet()) {
                            String methodName = methodTpMap.getKey();
                            Map<Long, Long> tpMap = methodTpMap.getValue();
                            if(tpMap.values().size() > 0) {
                                List<Long> times = new ArrayList<>(tpMap.values());
                                System.out.println(methodName + ", TP90: " + tp(times, 90) + "毫秒, TP99: " + tp(times, 99)+ "毫秒");
                            } else {
                                System.out.println("无内容");
                            }
                        }
                    }
                }, 1, 5, TimeUnit.SECONDS);
    }



    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String methodName = invocation.getMethodName();

        long start = System.currentTimeMillis();
        try {
            return invoker.invoke(invocation);
        } finally {
            long end = System.currentTimeMillis();

            Map<Long, Long> tpMap = new ConcurrentHashMap();
            if (methodsTpMap.containsKey(methodName)){
                tpMap = methodsTpMap.get(methodName);
                tpMap.entrySet().removeIf(m -> (end - m.getKey()) > 60 * 1000 );
            }
            tpMap.put(end, (end - start));
            methodsTpMap.put(methodName, tpMap);
        }

    }

    private static Long tp(List<Long> times, int percent){
        float percentF = (float) percent / 100;
        int index = (int) Math.ceil(percentF * times.size() - 1);
        Collections.sort(times);
        return times.get(index);
    }
}
