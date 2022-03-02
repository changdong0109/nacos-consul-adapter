/**
 * The MIT License
 * Copyright © 2021 liu cheng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.leshu.adapter.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.leshu.adapter.controller.AgentController;
import com.leshu.adapter.controller.ServiceController;
import com.leshu.adapter.service.RegistrationService;
import com.leshu.adapter.service.impl.DirectRegistrationService;
import com.leshu.adapter.service.impl.LongPollingRegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

/**
 * @description:
 * @author: liucheng
 * @createTime:2021/6/3 21:25
 */
@EnableConfigurationProperties
@Slf4j
public class NacosConsulAdapterConfig {

    @Bean
    @ConditionalOnMissingBean
    public NacosConsulAdapterProperties nacosConsulAdapterProperties() {
        return new NacosConsulAdapterProperties();
    }

//    @Bean
//    @ConditionalOnProperty(
//            value = "nacos-consul-adapter.mode", havingValue = "direct"
//    )
//    public RegistrationService directRegistrationService(ReactiveDiscoveryClient reactiveDiscoveryClient) {
//        log.info("创建直接的请求bean");
//        return new DirectRegistrationService(reactiveDiscoveryClient);
//    }


    //    @Bean
//    @ConditionalOnProperty(name = "nacos-consul-adapter.mode", havingValue = "long-polling")
//    public RegistrationService longPollingRegistrationService(NacosConsulAdapterProperties nacosConsulAdapterProperties,
//                                                              DiscoveryClient discoveryClient, NacosServiceManager nacosServiceManager,
//                                                              NacosDiscoveryProperties nacosDiscoveryProperties) {
//
//        log.info("创建长轮询的请求bean");
//        return new LongPollingRegistrationService(nacosConsulAdapterProperties, discoveryClient, nacosServiceManager, nacosDiscoveryProperties);
//    }
    @Bean
    public RegistrationService registrationService(NacosConsulAdapterProperties nacosConsulAdapterProperties,
                                                   DiscoveryClient discoveryClient, NacosServiceManager nacosServiceManager,
                                                   NacosDiscoveryProperties nacosDiscoveryProperties, ReactiveDiscoveryClient reactiveDiscoveryClient) {
        if (NacosConsulAdapterProperties.DIRECT_MODE.equals(nacosConsulAdapterProperties.getMode())) {
            log.info("使用直接查询模式");
            return new DirectRegistrationService(reactiveDiscoveryClient);
        }
        log.info("使用长轮询模式");
        return new LongPollingRegistrationService(nacosConsulAdapterProperties, discoveryClient, nacosServiceManager, nacosDiscoveryProperties);
    }


    @Bean
    @ConditionalOnMissingBean
    public AgentController agentController() {
        return new AgentController();
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn("nacosConsulAdapterProperties")
    public ServiceController serviceController(RegistrationService registrationService) {
        return new ServiceController(registrationService);
    }

}
