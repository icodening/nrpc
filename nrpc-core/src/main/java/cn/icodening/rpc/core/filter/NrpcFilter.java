package cn.icodening.rpc.core.filter;

import cn.icodening.rpc.core.Initializer;
import cn.icodening.rpc.core.NrpcException;
import cn.icodening.rpc.core.exchange.Request;
import cn.icodening.rpc.core.exchange.Response;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.03.09
 */
@Extensible
public interface NrpcFilter extends Initializer {

    void filter(Request request, Response response, NrpcFilterChain nrpcFilterChain) throws NrpcException;

}
