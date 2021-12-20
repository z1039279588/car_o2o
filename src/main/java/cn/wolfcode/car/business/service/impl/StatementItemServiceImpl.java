package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.mapper.StatementItemMapper;

import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatementItemServiceImpl implements IStatementItemService {
    @Autowired
    private StatementItemMapper statementItemMapper;
    @Autowired
    private IStatementService statementService;

    @Override
    public void pay(Long statementId) {
        Long userId = ShiroUtils.getUserId();
        statementService.pay(statementId,userId,Statement.STATUS_PAID);
    }

    @Override
    @Transactional
    public void saveItems(List<StatementItem> items) {
         // 进行合理性校验
        if(items == null || items.size() <= 0){
            throw new BusinessException("非法操作");
        }
        StatementItem tempItem = items.remove(items.size() - 1);
        Long statementId = tempItem.getStatementId();
        BigDecimal disCountPrice = tempItem.getItemPrice();
        Statement statement = statementService.get(statementId);
        if(Statement.STATUS_PAID.equals(statement.getStatus())){
            throw new BusinessException("已支付得结算不能进行保存操作");
        }
        // 删除之前的明细数据
        statementItemMapper.deleteByStatementId(statementId);
        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal totalQuantity = new BigDecimal(0);
        for(StatementItem item : items){
            // 保存数据库中
            statementItemMapper.insert(item);
           totalAmount = totalAmount.add(item.getItemPrice().multiply(item.getItemQuantity()));
           totalQuantity = totalQuantity.add(item.getItemQuantity());
        }
        // 更新结算单的总消费金额,总数量,总折扣金额
        statementService.updateAmount(statementId,totalAmount,totalQuantity,disCountPrice);
    }

    @Override
    public List query(StatementItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(),qo.getPageSize());
        return statementItemMapper.query(qo);
    }

}
