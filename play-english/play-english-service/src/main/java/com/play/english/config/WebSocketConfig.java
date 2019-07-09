package com.play.english.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author chaiqx
 */
@Configuration
public class WebSocketConfig {

    /**
     * @return ServerEndpointExporter
     * @throws
     * @Title: serverEndpointExporter
     * @Description: 检测服务类实现
     */
    @Bean(name = "ServerEndpointExporter")
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
