package com.yupi.yuoj.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目提交请求
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 代码
     */
    private String code;

    /**
     * 创建用户 id（session里获取）
     */
//    private Long userId;

    /**
     * 判题状态（0-待判题、1-判题中、2-成功、3-失败），刚提交默认待判题即可
     */
//    private Integer status;

    private static final long serialVersionUID = 1L;
}