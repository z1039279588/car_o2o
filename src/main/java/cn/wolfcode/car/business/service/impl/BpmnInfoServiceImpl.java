package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BpmnInfoServiceImpl implements IBpmnInfoService {
    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;
    @Override
    public List query(BpmnInfoQuery qo) {
        PageHelper.startPage(qo.getPageNum(),qo.getPageSize());
        return null;
    }
}
