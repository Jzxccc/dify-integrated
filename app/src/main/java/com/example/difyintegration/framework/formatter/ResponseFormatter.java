package com.example.difyintegration.framework.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 响应格式化器，负责格式化服务执行结果
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ResponseFormatter {

    private final ObjectMapper objectMapper;

    /**
     * 格式化成功响应
     */
    public String formatSuccessResponse(Object result) {
        try {
            ResultWrapper wrapper = ResultWrapper.success(result);
            return objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            log.error("Error serializing success response", e);
            return "{\"error\": \"Error serializing response\"}";
        }
    }

    /**
     * 格式化错误响应
     */
    public String formatErrorResponse(Exception error) {
        try {
            ResultWrapper wrapper = ResultWrapper.error(error.getMessage());
            return objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            return "{\"error\": \"Error processing request\"}";
        }
    }

    /**
     * 结果包装类
     */
    public static class ResultWrapper {
        private boolean success;
        private Object data;
        private String error;

        public ResultWrapper() {}

        public ResultWrapper(boolean success, Object data, String error) {
            this.success = success;
            this.data = data;
            this.error = error;
        }

        public static ResultWrapper success(Object data) {
            return new ResultWrapper(true, data, null);
        }

        public static ResultWrapper error(String errorMessage) {
            return new ResultWrapper(false, null, errorMessage);
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}