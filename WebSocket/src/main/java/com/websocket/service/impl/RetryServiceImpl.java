package com.websocket.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.websocket.constant.Constants;
import com.websocket.service.RetryService;
import com.websocket.utils.HttpUtils;
import com.websocket.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 重试服务接口
 */
@Service
@Slf4j
public class RetryServiceImpl implements RetryService {

    /**
     *  @Retryable：标记当前方法会使用重试机制
     *  value：重试的触发机制，当遇到Exception异常的时候，会触发重试
     *  maxAttempts：重试次数（包括第一次调用），默认为：3
     *  backoff：重试等待策略，默认使用@Backoff，
     *  @Backoff的value默认为1000L delay表示重试的延迟时间
     *  multiplier（指定延迟倍数）默认为0，表示固定暂停1秒后进行重试，如果手动设置则表示：上一次延时时间是这一次的倍数
     * @param reqUrl
     * @param reqData
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2))
    @Override
    public void retryHttpReq(String reqUrl, String reqData) throws Exception{
        String response = "";
        try {
            response = HttpUtils.doPost(reqUrl, reqData, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        if(StringUtils.isEmpty(response)){
            throw new Exception("业务系统响应数据为空！");
        }
        log.info("调用业务系统返回响应数据：{}", response);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if (Integer.parseInt(jsonObject.get(Constants.CODE).toString()) != Constants.SUCCESS) {
            throw new Exception("业务系统响应处理结果为：" + jsonObject.get(Constants.MSG));
        }
        log.info("请求数据：{}，发送至业务系统成功！", reqData);
        return;
    }

    /**
     * 重试失败回调方法
     * @param e
     */
    @Recover
    public void recover(Exception e) {
        e.printStackTrace();
        log.warn("消息记录发送异常：{}", e.getMessage());
    }
}
