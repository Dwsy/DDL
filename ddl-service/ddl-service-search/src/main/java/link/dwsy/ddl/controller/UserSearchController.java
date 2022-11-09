package link.dwsy.ddl.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import link.dwsy.ddl.XO.ES.User.UserEsDoc;
import link.dwsy.ddl.XO.Enum.Message.NotifyType;
import link.dwsy.ddl.XO.VO.InvitationUserSearchVO;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.annotation.UserActiveLog;
import link.dwsy.ddl.repository.User.UserNotifyRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PageData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Dwsy
 * @Date 2022/11/8
 */
@RestController
@RequestMapping("user")
public class UserSearchController {
    private static final String INDEX = "ddl_user";
    @Resource
    private ElasticsearchClient client;

    @Resource
    private UserNotifyRepository userNotifyRepository;

    @Resource
    private UserSupport userSupport;

    @GetMapping("{query}")
    @UserActiveLog
    public PageData<UserEsDoc> search(@PathVariable String query,
                                      @RequestParam(defaultValue = "1") int page) throws Exception {

        return getUserEsDocPageData(query, page);
    }

    @GetMapping("invitation-user/{query}")
    @UserActiveLog
    @AuthAnnotation
    public PageData<InvitationUserSearchVO> searchInvitationUser(
            @PathVariable String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam long questionId
    ) throws Exception {
        return getInvitationUserEsDocPageData(query, page, questionId);
    }


    private PageData<InvitationUserSearchVO> getInvitationUserEsDocPageData(String query, int page, long questionId) throws IOException {
        Long formUserId = userSupport.getCurrentUser().getId();

        HitsMetadata<UserEsDoc> hits = getSearchUserHits(query);
        List<Hit<UserEsDoc>> hitList = hits.hits();
        List<UserEsDoc> retSearch = hitList.stream().map(Hit::source).collect(Collectors.toList());
        PageData<InvitationUserSearchVO> docPageData = new PageData<>();

        ArrayList<InvitationUserSearchVO> invitationUserList = new ArrayList<>();
        for (UserEsDoc userEsDoc : retSearch) {
            boolean Invited = userNotifyRepository
                    .existsByDeletedFalseAndQuestionIdAndCancelFalseAndFromUserIdAndToUserIdAndNotifyType
                            (questionId, formUserId, userEsDoc.getUserId(), NotifyType.invitation_user_answer_question);
            InvitationUserSearchVO invitationUserSearchVO = new InvitationUserSearchVO
                    (userEsDoc.getUserId(), userEsDoc.getUserNickName(), userEsDoc.getAvatar(), false);
            if (Invited) {
                invitationUserSearchVO.setInvited(true);
            }
            invitationUserList.add(invitationUserSearchVO);
        }
        docPageData.setContent(invitationUserList);
        assert hits.total() != null;
        docPageData.setTotalElements(hits.total().value());
        docPageData.setPageNumber(page);
        var size = 10;
        docPageData.setTotalPages((int) Math.ceil((double) hits.total().value() / size));
        docPageData.setPageSize(size);
        if (page == 1) {
            docPageData.setFirst(true);
        }
        if (page == docPageData.getTotalPages()) {
            docPageData.setLast(true);
        }
        if (retSearch.isEmpty()) {
            docPageData.setEmpty(true);
        }
        return docPageData;
    }

    private PageData<UserEsDoc> getUserEsDocPageData(String query, int page) throws IOException {
        HitsMetadata<UserEsDoc> hits = getSearchUserHits(query);
        List<Hit<UserEsDoc>> hitList = hits.hits();
        List<UserEsDoc> retSearch = hitList.stream().map(Hit::source).collect(Collectors.toList());
        PageData<UserEsDoc> docPageData = new PageData<>();
        docPageData.setContent(retSearch);
        assert hits.total() != null;
        docPageData.setTotalElements(hits.total().value());
        docPageData.setPageNumber(page);
        var size = 10;
        docPageData.setTotalPages((int) Math.ceil((double) hits.total().value() / size));
        docPageData.setPageSize(size);
        if (page == 1) {
            docPageData.setFirst(true);
        }
        if (page == docPageData.getTotalPages()) {
            docPageData.setLast(true);
        }
        if (retSearch.isEmpty()) {
            docPageData.setEmpty(true);
        }
        return docPageData;
    }

    private HitsMetadata<UserEsDoc> getSearchUserHits(String query) throws IOException {
        SearchResponse<UserEsDoc> search = client.search(req -> {
                    req.index(INDEX).query(q ->
                            q.wildcard(w ->
                                    w.field("userNickName").value(query + "*")));
                    return req;
                },
                UserEsDoc.class);
        return search.hits();
    }
}
