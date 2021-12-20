package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServiceItemServiceImpl implements IServiceItemService {

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Override
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<ServiceItem>(serviceItemMapper.selectForList(qo));
    }


    @Override
    public void save(ServiceItem serviceItem) {
        ServiceItem newObj = new ServiceItem();
        newObj.setName(serviceItem.getName());
        newObj.setOriginalPrice(serviceItem.getOriginalPrice());
        newObj.setDiscountPrice(serviceItem.getDiscountPrice());
        newObj.setCarPackage(serviceItem.getCarPackage());
        newObj.setInfo(serviceItem.getInfo());
        newObj.setSaleStatus(serviceItem.getSaleStatus());
        // 判断是否套餐
        if (ServiceItem.CARPACKAGE_YES.equals(newObj.getCarPackage())) {
            newObj.setAuditStatus(ServiceItem.AUDITSTATUS_INIT);// 设置状态为初始化
        } else {
            newObj.setAuditStatus(ServiceItem.AUDITSTATUS_NO_REQUIRED);// 设置状态为无需审核
        }
        serviceItemMapper.insert(newObj);
    }

    @Override
    public ServiceItem get(Long id) {
        return serviceItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(ServiceItem serviceItem) {
        ServiceItem oldObj = this.get(serviceItem.getId());
        // 处于上架状态,出于审核中的状态不能进行修改
        if (ServiceItem.SALESTATUS_ON.equals(oldObj.getSaleStatus())  // 处于上架状态
                || ServiceItem.AUDITSTATUS_AUDITING.equals(oldObj.getAuditStatus())) { // 处于审核中
            throw new BusinessException("非法操作");
        }
        // 如果是套餐,如果已经审核通过,然后进行修改,变成初始化
        if (ServiceItem.AUDITSTATUS_APPROVED.equals(oldObj.getAuditStatus())) {
            oldObj.setAuditStatus(ServiceItem.AUDITSTATUS_INIT);
        }
        // 把前台得属性设置当前对象中
        oldObj.setName(serviceItem.getName());
        oldObj.setOriginalPrice(serviceItem.getOriginalPrice());
        oldObj.setDiscountPrice(serviceItem.getDiscountPrice());
        oldObj.setServiceCatalog(serviceItem.getServiceCatalog());
        oldObj.setInfo(serviceItem.getInfo());
        serviceItemMapper.updateByPrimaryKey(oldObj);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            serviceItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<ServiceItem> list() {
        return serviceItemMapper.selectAll();
    }

    @Override
    public void saleOn(Long id) {
        // 合理化校验
        ServiceItem oldObj = this.get(id);
        if (oldObj != null) {
            // 如果处于上架状态,不需要做事情
            if (ServiceItem.SALESTATUS_ON.equals(oldObj.getSaleStatus())) {
                return;
            }
        }
        // 如果处于非审核通过,不允许进行上架操作
        if (ServiceItem.CARPACKAGE_YES.equals(oldObj.getCarPackage()) // 是套餐
                && !ServiceItem.AUDITSTATUS_APPROVED.equals(oldObj.getAuditStatus())) {
            throw new BusinessException("未审核通过套餐不想允许上架");
        }
        // 其他情况都可以上架
        serviceItemMapper.updateSaleStatus(id, ServiceItem.SALESTATUS_ON);
    }

    @Override
    public void saleOff(Long id) {
        serviceItemMapper.updateSaleStatus(id, ServiceItem.SALESTATUS_OFF);
    }
}
