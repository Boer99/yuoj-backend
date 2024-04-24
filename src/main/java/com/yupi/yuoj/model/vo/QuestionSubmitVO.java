package com.yupi.yuoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.yupi.yuoj.judge.codesandbox.model.JudgeInfo;
import com.yupi.yuoj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目
 */
@Data
public class QuestionSubmitVO implements Serializable {

    private final static Gson GSON = new Gson();

    /**
     * id
     */
    private Long id;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 代码（仅管理员可见）
     */
    private String code;

    /**
     * 判题状态（0-待判题、1-判题中、2-成功、3-失败）
     */
    private Integer status;

    /**
     * 判题信息（json对象）
     */
    private JudgeInfo judgeInfo;

    /**
     * 提交用户信息
     */
    private UserVO userVO;

    /**
     * 题目信息
     */
    private QuestionVO questionVO;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param questionSubmitVO
     * @return
     */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, questionSubmit);

        JudgeInfo judgeInfoVO = questionSubmitVO.getJudgeInfo();
        // 对象转json
        if (judgeInfoVO != null) {
            questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoVO));
        }
        return questionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param questionSubmit
     * @return
     */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);

        // json对象转对象
        questionSubmitVO.setJudgeInfo(JSONUtil.toBean(questionSubmit.getJudgeInfo(), JudgeInfo.class));
        return questionSubmitVO;
    }
}