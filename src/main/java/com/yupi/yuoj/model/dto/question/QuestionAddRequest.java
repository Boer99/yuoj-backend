package com.yupi.yuoj.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 */
@Data
public class QuestionAddRequest implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 创建用户 id（当前用户获取）
     */
//    private Long userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 答案
     */
    private String answer;

    /**
     * 判题用例（json数组）
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置（json对象）
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}