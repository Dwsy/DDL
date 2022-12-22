package link.dwsy.ddl.controller;


import link.dwsy.ddl.XO.VO.HeatmapData;
import link.dwsy.ddl.annotation.AuthAnnotation;
import link.dwsy.ddl.entity.User.UserPoints;
import link.dwsy.ddl.repository.User.UserPointsRepository;
import link.dwsy.ddl.support.UserSupport;
import link.dwsy.ddl.util.PRHelper;
import link.dwsy.ddl.util.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/points")
public class UserPointsController {

    @Resource
    private UserPointsRepository userPointsRepository;

    @Resource
    private UserSupport userSupport;

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        System.out.println(calendar.getTime());
    }

    @GetMapping
    @AuthAnnotation
    public int getPoints() {
        Long id = userSupport.getCurrentUser().getId();
        if (id != null) {
            return userPointsRepository.getSumPointsByUid(id);
        }
        return 0;
    }

    @GetMapping("details")
    @AuthAnnotation
    public PageData<UserPoints> getDetails(@RequestParam(required = false, defaultValue = "1", name = "page") int page,
                                           @RequestParam(required = false, defaultValue = "8", name = "size") int size) {
        Long id = userSupport.getCurrentUser().getId();
        if (id == null) {
            return null;
        }

        Page<UserPoints> pointsList = userPointsRepository.findByUserId(id, PRHelper.order(Sort.Direction.DESC, new String[]{"createTime"}, page, size));

        return new PageData<>(pointsList);
    }

    @GetMapping("heatmap/{userId}")
    public List<HeatmapData> getUserHeatmapData(@PathVariable long userId,
                                                @RequestParam(required = false, defaultValue = "0", name = "minusYears") int minusYears) {
        Calendar calendar = Calendar.getInstance();
        if (userId <= 0) {
            return null;
        }
//        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        Date lastYear = calendar.getTime();
        LocalDate startDate = LocalDate.of(calendar.get(Calendar.YEAR) - minusYears - 1, calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        List<Map<Object, Object>> heatmapDataMapList = userPointsRepository.getHeatmapDataByUserId(userId, startDate, LocalDate.now().minusYears(minusYears));

//        System.out.printf(String.valueOf(heatmapDataList.get(0).getCount()));
        List<HeatmapData> list = new ArrayList<>();
        for (Map<Object, Object> data : heatmapDataMapList) {
            int count = ((BigInteger) data.get("count")).intValue();
            HeatmapData heatmapData = new HeatmapData((Date) data.get("date"), count);
            list.add(heatmapData);
        }
        return list;
    }

}
