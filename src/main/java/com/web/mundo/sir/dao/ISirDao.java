package com.web.mundo.sir.dao;

import com.web.mundo.sir.po.SirTs;
import com.web.mundo.sir.po.SirVideo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;

public interface ISirDao {

    @Insert(" insert into sir_video ( id,_id,title,cover_full,actors,tags,created_at,badges,price,good,rating,duration,mv_url,create_time,update_time,oss  ) " +
            "values " +
            "( #{id},#{_id},#{title},#{cover_full},#{actors},#{tags},#{created_at},#{badges},#{price},#{good},#{rating},#{duration},#{mv_url},#{create_time},#{update_time},#{oss}) ")
    void saveVideo(SirVideo sirVideo);

    @Select(" select *  from sir_video where id=#{id} ")
    SirVideo findVideo(@Param("id") String id);

    @UpdateProvider(type = SirDaoSqlProvider.class,method = "updateVideo")
    void updateVideo(SirVideo sirVideo);


    @Select(" select  *  from sir_video where is_down  NOT IN ('1') ORDER BY good DESC ")
    List<SirVideo> findVideoToDown();


    @Insert(" insert into sir_ts ( v_id,create_time,update_time,oss,url,count  ) " +
            "values " +
            "( #{v_id},#{create_time},#{update_time},#{oss},#{url},#{count}) ")
    void saveTs(SirTs sirTs);

    @Select(" select  *  from sir_ts where v_id=#{v_id} and  count=#{count} ")
    SirTs findTs(@Param("v_id") String v_id, @Param("count") String count);

    @Select(" select  *  from sir_ts where v_id=#{v_id}  and is_down != '1' ")
    List<SirTs> findTsList(@Param("v_id") String v_id);



    @UpdateProvider(type = SirDaoSqlProvider.class,method = "updateTs")
    void updateTs(SirTs sirTs);



}
