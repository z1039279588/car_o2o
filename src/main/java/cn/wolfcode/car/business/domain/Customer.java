package cn.wolfcode.car.business.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 客户列表
 */
@Setter@Getter
public class Customer {
    private static final long serialVersionUID = 1L;

    public static final Integer VIP_NO = 0;//不是会员

    public static final Integer VIP_YES = 1;//是会员

    public static final Integer softDelete_YES = 0;//软删除

    public static final Integer softDelete_NO = 1;//删除
    /** 客户ID*/
    private Long id;

    /** 客户名称*/
    private String name;

    /** 联系方式*/
    private String phone;

    /** 生日*/
    @JsonFormat(pattern = "yyyy-MM-dd ",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date birthday;

    /** 是否VIP(数据字典)*/
    private Integer vip;

    /** 备注信息*/
    private String remark;
    /*
    软删除
     */
    private Integer softDelete = softDelete_YES  ;
}