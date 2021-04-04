package cn.icodening.rpc.config;

import cn.icodening.rpc.core.boot.Boot;
import cn.icodening.rpc.core.extension.Extensible;

/**
 * @author icodening
 * @date 2021.03.14
 */
@Extensible
public interface NrpcRunner extends Boot {

    int ENV_CONFIG_PRIORITY = 0;
    int APPLICATION_CONFIG_PRIORITY = 100;
    int SERVICE_EXPORT_PRIORITY = 200;
    int REFERENCE_PRIORITY = 400;
    int SERVICE_REGISTRATION_PRIORITY = 10000;
}
