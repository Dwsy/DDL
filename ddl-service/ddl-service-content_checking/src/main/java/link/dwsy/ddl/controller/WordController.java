package link.dwsy.ddl.controller;


import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import link.dwsy.ddl.XO.RB.WordEntityRB;
import link.dwsy.ddl.core.CustomExceptions.CodeException;
import link.dwsy.ddl.core.constant.CustomerErrorCode;
import link.dwsy.ddl.entity.ContentChecking.WordEntity;
import link.dwsy.ddl.repository.ContentChecking.WordRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Controller
@RequestMapping("/word")
public class WordController {

    @Resource
    private WordRepository wordRepository;

    @Resource
    private SensitiveWordBs sensitiveWordBs;


    /**
     * 添加元素
     *
     * @param entity 实体
     * @return 结果
     */
    @PostMapping("/add")
    @ResponseBody
    public boolean add(@RequestBody WordEntityRB entity) {
        WordEntity word = wordRepository.findByDeletedFalseAndWord(entity.getWord());
        if (word != null) {
            throw new CodeException(CustomerErrorCode.WordExist);
        }
        wordRepository.save(WordEntity.builder()
                .type(entity.getType())
                .word(entity.getWord()).build());
        refreshSensitiveWord();
        return true;
    }

    /**
     * 编辑
     *
     * @param entity 实体
     * @return 结果
     */
    @PutMapping("/edit")
    @ResponseBody
    public boolean edit(@RequestBody WordEntityRB entity) {
        if (!wordRepository.existsById(entity.getId())) {
            throw new CodeException(CustomerErrorCode.WordNotFount);
        }
        wordRepository.save(entity.toEntity());
        refreshSensitiveWord();
        return true;
    }

    /**
     * 删除
     *
     * @param id 实体
     * @return 结果
     */
    @DeleteMapping("/remove/{id}")
    @ResponseBody
    public boolean remove(@PathVariable Long id) {
        if (!wordRepository.existsById(id)) {
            throw new CodeException(CustomerErrorCode.WordNotFount);
        }
        wordRepository.deleteById(id);
        refreshSensitiveWord();
        return true;
    }

    /**
     * 刷新敏感詞
     * 可以优化为异步，甚至批量。
     *
     * @since 1.1.0
     */
    private void refreshSensitiveWord() {
        sensitiveWordBs.init();
    }

}