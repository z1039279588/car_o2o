package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.math.BigDecimal;
import java.util.List;

public interface IStatementService {
    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Statement> query(StatementQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    Statement get(Long id);


    /**
     * 保存
     * @param statement
     */
    void save(Statement statement);


    /**
     * 更新
     * @param statement
     */
    void update(Statement statement);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Statement> list();

    /**
     * 更新结算单得总金额,总数量,折扣金额
     * @param statementId
     * @param totalAmount
     * @param totalQuantity
     * @param disCountPrice
     */
    void updateAmount(Long statementId, BigDecimal totalAmount, BigDecimal totalQuantity, BigDecimal disCountPrice);

    /**
     * 支付更新状态
     * @param statementId
     * @param userId

     */
    void pay(Long statementId, Long userId, Integer status);

    /**
     * 从预约单中保存结算单
     * @param statement
     */
    void saveFromAppointment(Statement statement);

    /**
     * 根据预约单ID查算结算单对象
     * @param appointmentId
     * @return
     */
    Statement getByAppointmentId(Long appointmentId);
}
