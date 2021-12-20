package cn.wolfcode.car.business.web.controller;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/business/statementItem")
public class StatementItemController {
    //模板前缀
    private static final String prefix = "business/statementItem/";
    @Autowired
    private IStatementItemService statementItemService;
    @Autowired
    private IStatementService statementService;
    @RequestMapping("/itemDetail")
    public String itemDetail(Long statementId, Model model){
        // 先根据statementId查询结算单对象
        Statement statement = statementService.get(statementId);
        model.addAttribute("statement",statement);
        // 根据结算单对象状态,决定跳转到什么页面
        if(Statement.STATUS_CONSUME.equals(statement.getStatus())){
            return prefix + "itemEdit";
        } else {
            return prefix + "itemDetail";
        }
    }
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo query(StatementItemQuery qo){
        List list = statementItemService.query(qo);
        return new TablePageInfo(list);
    }
    @RequestMapping("/saveItems")
    @ResponseBody
    public AjaxResult saveItems(@RequestBody List<StatementItem> items){
        statementItemService.saveItems(items);
        return AjaxResult.success();
    }

    @RequestMapping("/pay")
    @ResponseBody
    public AjaxResult pay(Long statementId){
        statementItemService.pay(statementId);
        return AjaxResult.success("支付成功");
    }
}
