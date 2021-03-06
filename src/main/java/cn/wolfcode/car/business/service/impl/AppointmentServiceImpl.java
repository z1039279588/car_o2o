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
            // ???????????????
            throw new BusinessException("????????????????????????????????????");
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
        //???????????????
        Appointment appointment = this.get(id);
        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())) {
            // ???????????????
            //  appointmentMapper.changeStatus(id,Appointment.STATUS_ARRIVAL);
            appointmentMapper.arrival(id, Appointment.STATUS_ARRIVAL, new Date());
        } else {
            throw new BusinessException("????????????");
        }
    }

    @Override
    public void cancel(Long id) {
        //???????????????
        Appointment appointment = this.get(id);
        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())) {
            // ???????????????
            appointmentMapper.changeStatus(id, Appointment.STATUS_ARRIVAL);
        } else {
            throw new BusinessException("????????????");
        }
    }

    @Override
    @Transactional
    public Statement generateStatement(Long appointmentId) {
        Appointment appointment = this.get(appointmentId);
        // ???????????????
        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())
           || Appointment.STATUS_SETTLE.equals(appointment.getStatus())){
        // ???????????????id????????????????????????
           Statement statement = statementService.getByAppointmentId(appointmentId);
            if(statement == null){
                // ??????????????????????????????????????????
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
                // ?????????????????????(???????????????)
                appointmentMapper.changeStatus(appointmentId,Appointment.STATUS_SETTLE);
            }
           return statement;
        } else {
            throw new BusinessException("????????????");
        }
    }
}