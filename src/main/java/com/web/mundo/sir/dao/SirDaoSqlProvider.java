package com.web.mundo.sir.dao;

import com.web.mundo.sir.po.SirTs;
import com.web.mundo.sir.po.SirVideo;
import org.apache.commons.lang3.StringUtils;

public class SirDaoSqlProvider {


    public String updateVideo(SirVideo sirVideo){
        String sql = " update sir_video set  " +
                "update_time = #{update_time} " +
                ",created_at = #{created_at} ";

        // 抓取类型
        if (StringUtils.isNotBlank(sirVideo.getMv_url())) {
            sql = sql + " ,mv_url = #{mv_url} ";
        }
        if (StringUtils.isNotBlank(sirVideo.getGood())) {
            sql = sql + " ,good = #{good}  ";
        }
        if (StringUtils.isNotBlank(sirVideo.getIs_down())) {
            sql = sql + " ,is_down = #{is_down}  ";
        }
        sql = sql + " where id = #{id} ";
        return sql;
    }

    public String updateTs(SirTs sirTs){
        String sql = " update sir_ts set  " +
                " update_time = #{update_time} ";
        // 抓取类型
        if (StringUtils.isNotBlank(sirTs.getUrl())) {
            sql = sql + " ,url = #{url} ";
        }
        if (StringUtils.isNotBlank(sirTs.getIs_down())) {
            sql = sql + " ,is_down = #{is_down}  ";
        }
        sql = sql + " where id = #{id} ";
        return sql;
    }


}
