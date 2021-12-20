package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class CustomerQuery extends QueryObject {
    private String keyword;
    private Integer vip;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    public Date getEndTime() {
        if (endTime != null) {
            Calendar instance = Calendar.getInstance();
            instance.add(Calendar.DAY_OF_MONTH, 1);
            return instance.getTime();
        } else {
            return null;
        }
    }
}
