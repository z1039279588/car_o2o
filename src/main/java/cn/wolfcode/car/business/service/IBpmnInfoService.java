package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.query.BpmnInfoQuery;

import java.util.List;

public interface IBpmnInfoService {
    List query(BpmnInfoQuery qo);
}
