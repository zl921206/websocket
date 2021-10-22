package com.websocket.service;

/**
 * 重试服务接口
 */
public interface RetryService {

    /**
     * Http请求重试
     * @param reqUrl
     * @param reqData
     */
    void retryHttpReq(String reqUrl, String reqData) throws Exception;
}
