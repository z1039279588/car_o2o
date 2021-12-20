package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.service.IAppointmentService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements IAppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private IStatementService statementService;

    @Override
    public TablePageInfo<Appointment> query(AppointmentQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Appointment>(appointmentMapper.selectForList(qo));
    }

    @Override
    public void save(Appointment appointment) {
        Appointment newObj = new Appointment();
        newObj.setCustomerName(appointment.getCustomerName());
        newObj.setCustomerPhone(appointment.getCustomerPhone());
        newObj.setAppointmentTime(appointment.getAppointmentTime());
        newObj.setCarSeries(appointment.getCarSeries());
        newObj.setLicensePlate(appointment.getLicensePlate());
        newObj.setServiceType(appointment.getServiceType());
        newObj.setInfo(appointment.getInfo());
        appointmentMapper.insert(newObj);
    }

    @Override
    public Appointment get(Long id) {
        return appointmentMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Appointment appointment) {
        Appointment currentObj = this.get(appointment.getId());
        if (!Appointment.STATUS_APPOINTMENT.equals(currentObj.getStatus())) {
            // 不是预约中
            throw new BusinessException("非预约中的记录不允许编辑");
        }
        currentObj.setCustomerName(appointment.getCustomerName());
        currentObj.setCustomerPhone(appointment.getCustomerPhone());
        currentObj.setAppointmentTime(appointment.getAppointmentTime());
        currentObj.setCarSeries(appointment.getCarSeries());
        currentObj.setLicensePlate(appointment.getLicensePlate());
        currentObj.setServiceType(appointment.getServiceType());
        currentObj.setInfo(appointment.getInfo());
        appointmentMapper.updateByPrimaryKey(currentObj);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            appointmentMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<Appointment> list() {
        return appointmentMapper.selectAll();
    }

    @Override
    public void arrival(Long id) {
        //合理性校验
        Appointment appointment = this.get(id);
        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())) {
            // 处于预约中
            //  appointmentMapper.changeStatus(id,Appointment.STATUS_ARRIVAL);
            appointmentMapper.arrival(id, Appointment.STATUS_ARRIVAL, new Date());
        } else {
            throw new BusinessException("非法操作");
        }
    }

    @Override
    public void cancel(Long id) {
        //合理性校验
        Appointment appointment = this.get(id);
        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())) {
            // 处于预约中
            appointmentMapper.changeStatus(id, Appointment.STATUS_ARRIVAL);
        } else {
            throw new BusinessException("非法操作");
        }
    }

    @Override
    @Transactional
    public Statement generateStatement(Long appointmentId) {
        Appointment appointment = this.get(appointmentId);
        // 合理性校验
        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())
           || Appointment.STATUS_SETTLE.equals(appointment.getStatus())){
        // 根据预约单id查询出结算单对象
           Statement statement = statementService.getByAppointmentId(appointmentId);
            if(statement == null){
                // 从预约单中把信息保存到结算单
                statement = new Statement();
                statement.setCustomerPhone(appointment.getCustomerPhone());
                statement.setCustomerName(appointment.getCustomerName());
                statement.setCarSeries(appointment.getCarSeries());
                statement.setServiceType(appointment.getServiceType());
                statement.setAppointmentId(appointmentId);
                statement.setLicensePlate(appointment.getLicensePlate());
                statement.setInfo(appointment.getInfo());
                statement.setActualArrivalTime(appointment.getActualArrivalTime());
                statement.setCreateTime(new Date());
                statementService.saveFromAppointment(statement);
                // 修改预约单状态(结算单生成)
                appointmentMapper.changeStatus(appointmentId,Appointment.STATUS_SETTLE);
            }
           return statement;
        } else {
            throw new BusinessException("非法操作");
        }
    }
}