package com.cross2u.manage.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cross2u.manage.model.*;
import com.cross2u.manage.service.ManageServiceL;
import com.cross2u.manage.util.BaseResponse;
import com.cross2u.manage.util.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/manage")
public class ManageControllerL {

    @Autowired
    BaseResponse jr;
    @Autowired
    ManageServiceL ms;

    //b->52、读取系统的通知
    //a->38、显示系统通知
    @RequestMapping("/showPublicInfo")
    public BaseResponse showPublicInfo()
    {
        JSONArray result = ms.selectPublicInfo();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }

    /*
    * A的api
    * */

    //27、显示已经设置的关键词
    @RequestMapping("/showKeyWord")
    public BaseResponse showKeyWord()
    {
        BaseResponse jr=new BaseResponse();
        JSONArray result = ms.selectKeyWord();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //28、编辑已经设置的关键词
    @RequestMapping("/updateKeyWord")
    public BaseResponse updateKeyWord(
            @RequestParam("akId") BigInteger akId,
            @RequestParam(value = "akText",required = false) String akText,
            @RequestParam(value = "akReply",required = false) String akReply
    )
    {
        Adminkeyword adminkeyword = new Adminkeyword();
        adminkeyword.setAkId(akId);
        if(akText!=null)
        {
            adminkeyword.setAkText(akText);
        }
        if(akReply!=null)
        {
            adminkeyword.setAkReply(akReply);
        }
        boolean result = adminkeyword.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //29、添加新的关键词
    @RequestMapping("/addKeyWord")
    public BaseResponse addKeyWord(
            @RequestParam("akText") String akText,
            @RequestParam("akReply") String akReply,
            @RequestParam("akAdministrator") BigInteger akAdministrator
    )
    {
        Adminkeyword adminkeyword = new Adminkeyword();
        adminkeyword.setAkText(akText);
        adminkeyword.setAkReply(akReply);
        adminkeyword.setAkAdministrator(akAdministrator);
        boolean result = adminkeyword.save();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //53、删除已经设置的关键词
    @RequestMapping("/deleteKeyWord")
    public BaseResponse deleteKeyWord(
            @RequestParam("akId") BigInteger akId
    )
    {
        Adminkeyword adminkeyword = new Adminkeyword();
        adminkeyword.setAkId(akId);
        boolean result = adminkeyword.delete();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //30、 显示其他智能客服设置（收到消息/注册通过）
    @RequestMapping("/showAutoReply")
    public BaseResponse showAutoReply()
    {
        BaseResponse jr=new BaseResponse();
        List<Adminreply> result = Adminreply.dao.find("select * from adminreply");
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //54、 编辑其他智能客服设置（收到消息/注册通过）
    @RequestMapping("/updateAutoReply")
    public BaseResponse updateAutoReply(
            @RequestParam("arId") BigInteger arId,
            @RequestParam("arReply") String arReply,
            @RequestParam("arAdministrator") BigInteger arAdministrator
    )
    {
        Adminreply adminreply = new Adminreply();
        adminreply.setArId(arId);
        adminreply.setArReply(arReply);
        adminreply.setArAdministrator(arAdministrator);
        boolean result = adminreply.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //31、显示已经设置的敏感词
    @RequestMapping("/showSensitive")
    public BaseResponse showSensitive()
    {
        JSONArray result = ms.selectSensitive();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //32、设置敏感词
    @RequestMapping("/addSensitive")
    public BaseResponse addSensitive(
            @RequestParam("senText") String senText,
            @RequestParam("senAdministrator") BigInteger senAdministrator
    )
    {
        Sensi sensitive = new Sensi();
        sensitive.setSenText(senText);
        sensitive.setSenAdministrator(senAdministrator);
        boolean result = sensitive.save();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //52、删除已经设置的敏感词
    @RequestMapping("/deleteSensitive")
    public BaseResponse deleteSensitive(
            @RequestParam("senId") BigInteger senId
    )
    {
        Sensi sensitive = new Sensi();
        sensitive.setSenId(senId);
        boolean result = sensitive.delete();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.ADD_ERROR);
        }
        jr.setData(null);
        return jr;
    }

    //39、修改系统通知
    @RequestMapping("/updatePublicInfo")
    public BaseResponse updatePublicInfo(
            @RequestParam("piId") BigInteger piId,
            @RequestParam(value = "piType",required = false) Integer piType,
            @RequestParam(value = "piTitle",required = false) String piTitle,
            @RequestParam(value = "piContent",required = false) String piContent
    )
    {
        Publicinfo publicinfo = new Publicinfo();
        publicinfo.setPiId(piId);
        if(piType!=null)
        {
            publicinfo.setPiType(piType);
        }
        if(piTitle!=null)
        {
            publicinfo.setPiTitle(piTitle);
        }
        if(piContent!=null)
        {
            publicinfo.setPiContent(piContent);
        }
        boolean result = publicinfo.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //40、添加系统通知
    @RequestMapping("/addPublicInfo")
    public BaseResponse addPublicInfo(
            @RequestParam("piType") Integer piType,
            @RequestParam("piTitle") String piTitle,
            @RequestParam("piContent") String piContent
    )
    {
        Publicinfo publicinfo = new Publicinfo();
        publicinfo.setPiType(piType);
        publicinfo.setPiTitle(piTitle);
        publicinfo.setPiContent(piContent);
        boolean result = publicinfo.save();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //49、显示系统通知每一类的数量
    @RequestMapping("/showPublicInfoNumByClass")
    public BaseResponse showPublicInfoNumByClass()
    {
        JSONArray result = ms.selectPublicInfoNumByClass();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //50、按类显示系统通知
    @RequestMapping("/showPublicInfoByClass")
    public BaseResponse showPublicInfoByClass(
            @RequestParam("piType") Integer piType
    )
    {
        JSONArray result = ms.selectPublicInfoByClass(piType);
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //51、删除系统通知
    @RequestMapping("/deletePublicInfo")
    public BaseResponse deletePublicInfo(
            @RequestParam("piId") BigInteger piId
    )
    {
        Publicinfo publicinfo = new Publicinfo();
        publicinfo.setPiId(piId);
        boolean result =publicinfo.delete();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.DELETE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //33、显示参数扣分信息
    @RequestMapping("/showScoreInfo")
    public BaseResponse showScoreInfo()
    {
        JSONObject result = ms.selectScoreInfo();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //34、修改退货原因减分参数
    @RequestMapping("/updateDrawBackScore")
    public BaseResponse updateDrawBackScore(
            @RequestParam("drId") BigInteger drId,
            @RequestParam("drDeduction") Integer drDeduction
    )
    {
        Drawbackreasons drawbackreasons = new Drawbackreasons();
        drawbackreasons.setDrId(drId);
        drawbackreasons.setDrDeduction(drDeduction);
        boolean result = drawbackreasons.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //35、修改异常店家减分参数
    @RequestMapping("/updateStoreScore")
    public BaseResponse updateStoreScore(
            @RequestParam("rmrId") BigInteger rmrId,
            @RequestParam("rmrPunish") Integer rmrPunish
    )
    {
        Reportmanufacturereasons reportmanufacturereasons = new Reportmanufacturereasons();
        reportmanufacturereasons.setRmrId(rmrId);
        reportmanufacturereasons.setRmrPunish(rmrPunish);
        boolean result = reportmanufacturereasons.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //36、修改异常评论参数
    @RequestMapping("/updateReplyScore")
    public BaseResponse updateReplyScore(
            @RequestParam("rerId") BigInteger rerId,
            @RequestParam("rerPunish") Integer rerPunish
    )
    {
        Reportevaluatereasons reportevaluatereasons = new Reportevaluatereasons();
        reportevaluatereasons.setRerId(rerId);
        reportevaluatereasons.setRerPunish(rerPunish);
        boolean result = reportevaluatereasons.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //37、修改异常商品参数
    @RequestMapping("/updateWareScore")
    public BaseResponse updateWareScore(
            @RequestParam("rgrId") BigInteger rgrId,
            @RequestParam("rgrPunish") Integer rgrPunish
    )
    {
        Reportgoodreasons reportgoodreasons = new Reportgoodreasons();
        reportgoodreasons.setRgrId(rgrId);
        reportgoodreasons.setRgrPunish(rgrPunish);
        boolean result = reportgoodreasons.update();
        if(result)
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.UPDATE_ERROR);
        }
        jr.setData(null);
        return jr;
    }
    //44、查看举报商品原因
    @RequestMapping("/showReportGoodReasons")
    public BaseResponse showReportGoodReasons()
    {
        List<Reportgoodreasons> result = ms.selectReportGoodReasons();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //45、查看举报店铺原因
    @RequestMapping("/showReportManufactureReasons")
    public BaseResponse showReportManufactureReasons()
    {
        List<Reportmanufacturereasons> result = ms.selectReportManufactureReasons();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }
    //46、查看举报评论原因
    @RequestMapping("/showReportEvaluateReasons")
    public BaseResponse showReportEvaluateReasons()
    {
        List<Reportevaluatereasons> result = ms.selectReportEvaluateReasons();
        if(!result.isEmpty())
        {
            jr.setResult(ResultCodeEnum.SUCCESS);
        }
        else
        {
            jr.setResult(ResultCodeEnum.FIND_ERROR);
        }
        jr.setData(result);
        return jr;
    }


}
