package com.study.filter;

import com.study.util.RequestHolder;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(group = {CommonConstants.CONSUMER})
public class TransportIpFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String requestIp = RequestHolder.getRequest();
        // 通过RpcContext进行隐式传参
        RpcContext.getContext().setAttachment("RequestAddress", requestIp);
        return invoker.invoke(invocation);
    }
}
