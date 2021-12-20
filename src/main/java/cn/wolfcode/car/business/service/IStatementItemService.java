package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;

import java.util.List;

public interface IStatementItemService {
    List query(StatementItemQuery qo);

    void saveItems(List<StatementItem> items);

    void pay(Long statementId);
}
