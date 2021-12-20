package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatementServiceImpl implements IStatementService {

    @Autowired
    private StatementMapper statementMapper;


    @Override
    public TablePageInfo<Statement> query(StatementQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Statement>(statementMapper.selectForList(qo));
    }


    @Override
    public void save(Statement statement) {
        Statement newObj = new Statement();
        newObj.setCustomerName(statement.getCustomerName());
        newObj.setCarSeries(statement.getCarSeries());
        newObj.setCreateTime(new Date());
        newObj.setInfo(statement.getInfo());
        newObj.setCustomerPhone(statement.getCustomerPhone());
        newObj.setServiceType(statement.getServiceType());
        newObj.setLicensePlate(statement.getLicensePlate());
        newObj.setActualArrivalTime(statement.getActualArrivalTime());
        statementMapper.insert(newObj);
    }

    @Override
    public Statement get(Long id) {
        return statementMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(Statement statement) {
        Statement currentObj = this.get(statement.getId());
        if(Statement.STATUS_PAID.equals(currentObj.getStatus())){
            throw new BusinessException("非法操作");
        }
        currentObj.setCustomerName(statement.getCustomerName());
        currentObj.setCarSeries(statement.getCarSeries());
        currentObj.setCreateTime(new Date());
        currentObj.setInfo(statement.getInfo());
        currentObj.setCustomerPhone(statement.getCustomerPhone());
        currentObj.setServiceType(statement.getServiceType());
        currentObj.setLicensePlate(statement.getLicensePlate());
        currentObj.setActualArrivalTime(statement.getActualArrivalTime());
        statementMapper.updateByPrimaryKey(currentObj);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            statementMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<Statement> list() {
        return statementMapper.selectAll();
    }

    @Override
    public void updateAmount(Long statementId, BigDecimal totalAmount, BigDecimal totalQuantity, BigDecimal disCountPrice) {
        statementMapper.updateAmount(statementId,totalAmount,totalQuantity,disCountPrice);
    }

    @Override
    public void pay(Long statementId, Long userId, Integer status) {
        statementMapper.pay(statementId,userId,status);
    }

    @Override
    public void saveFromAppointment(Statement statement) {
        statementMapper.insert(statement);
    }

    @Override
    public Statement getByAppointmentId(Long appointmentId) {
        return statementMapper.getByAppointmentId(appointmentId);
    }
}
