package com.yupi.yuoj.judge;

import cn.hutool.json.JSONUtil;
import com.yupi.yuoj.common.ErrorCode;
import com.yupi.yuoj.exception.BusinessException;
import com.yupi.yuoj.judge.codesandbox.CodeSandbox;
import com.yupi.yuoj.judge.codesandbox.CodeSandboxFactory;
import com.yupi.yuoj.judge.codesandbox.CodeSandboxProxy;
import com.yupi.yuoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.yuoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.yuoj.judge.strategy.JudgeContext;
import com.yupi.yuoj.model.dto.question.JudgeCase;
import com.yupi.yuoj.judge.codesandbox.model.JudgeInfo;
import com.yupi.yuoj.model.entity.Question;
import com.yupi.yuoj.model.entity.QuestionSubmit;
import com.yupi.yuoj.model.enums.JudgeInfoMessageEnum;
import com.yupi.yuoj.model.enums.QuestionSubmitStatusEnum;
import com.yupi.yuoj.service.QuestionService;
import com.yupi.yuoj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）获取到对应的题目、提交信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果“题目提交”状态不为【等待中】，就不用重复判题了
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中");
        }
        // 3）更新“题目提交”的状态为【判题中】，防止重复判题
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 4）调用沙箱（用户指定type），获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        // ==================== 封装 ExecuteCodeRequest
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream()
                .map(JudgeCase::getInput)
                .collect(Collectors.toList());
        // @Builder
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        // ====================
        // 调用沙箱，获取一组输出结果
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();

        // 5）选择不同的判题策略，设置“题目提交”的判题状态和判题信息（根据沙箱的执行结果）
        // 封装判题上下文
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList); // judgeCaseList有每个用例的正确执行结果
        judgeContext.setQuestion(question); // question包含JudgeConfig
        judgeContext.setQuestionSubmit(questionSubmit); // todo questionSubmit好像没用
        // 由 judgeManager 选择不同的判题策略
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        // 6）更新“题目提交”的判题状态和判题信息
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitUpdate.getId());

        // 6）更新题目的提交数、通过数
        Integer submitNum = question.getSubmitNum() + 1;
        Integer acceptNum = question.getAcceptedNum();
        Integer status = questionSubmitResult.getStatus();
        // 相同+1
        if (Objects.equals(QuestionSubmitStatusEnum.SUCCEED.getValue(), status)) {
            // todo 这里判断的是什么？
            if (JudgeInfoMessageEnum.ACCEPTED.getValue().equals(judgeInfo.getMessage())) {
                acceptNum = question.getAcceptedNum() + 1;
            }
        }
        // 更新题目
        Question resQuestion = new Question();
        resQuestion.setId(questionId);
        resQuestion.setSubmitNum(submitNum);
        resQuestion.setAcceptedNum(acceptNum);
        boolean id = questionService.updateById(resQuestion);
        if (!id) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交数目统计错误");
        }

        return questionSubmitResult;
    }
}
