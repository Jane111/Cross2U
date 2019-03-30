package com.cross2u.ware.service;

import com.cross2u.ware.model.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.template.stat.ast.For;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WareServicesZ {

    public List<Warefdispatch> showFDispatch(String wfdSId) {
        String sql="SELECT wfdName,wfdId " +
                " FROM warefdispatch " +
                " WHERE wfdSId=? ORDER BY wfdSort ";
        List<Warefdispatch> warefdispatches=Warefdispatch.dao.find(sql,wfdSId);
        return warefdispatches;
    }

    public List<Waresdispatch> showSDispatch(String wfdId) {
        String sql="SELECT wsdName,wsdId,wsdImg " +
                " from waresdispatch " +
                " WHERE wsdWFDId=? ORDER BY wsdSort";
        List<Waresdispatch> waresdispatches= Waresdispatch.dao.find(sql,wfdId);
        return waresdispatches;
    }

    public List<Record> showForOptions(String fId) {
        String sql="SELECT foId,foName " +
                " from formatoption  " +
                " WHERE foFormat=?";
        List<Record> formatoptions= Db.find(sql,fId);
        for (Record formatoption : formatoptions)
        {
            String sonSql="SELECT foId,foName " +
                    " from formatoption " +
                    " WHERE foParentOption=?";
            List<Formatoption> son=Formatoption.dao.find(sql,fId);
            formatoption.get("son",son);
        }
        return formatoptions;
    }

    public void addCatalog(String name,String fatherid,String rank)
    {
        Category category=new Category();
        category.setCtParentId(Long.valueOf(fatherid));
        category.setCtName(name);
        category.setCtRank(Integer.valueOf(rank));
        category.save();
    }

}
