package com.web.mundo.dao;

import com.web.mundo.po.Chapter;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IChapterDao {

    @Insert(" insert into t_chater (id,book_id,url,name,file_path,count,create_time,update_time) " +
            "values" +
            " (#{id},#{bookId},#{url},#{name},#{filePath},#{count},#{createTime},#{updateTime}) ")
    void saveChapter(Chapter chapter);

    @Select(" select id  from t_chater  where book_id=#{book_id} and name=#{name}  limit 1  ")
    Chapter findOneByBookIdAndTitle(@Param("book_id") String bookId, @Param("name") String title);

    @Select("  select  id,file_path filePath,count,book_id bookId,name   from  t_chater  where  book_id = #{bookId} order by count asc limit 1 ")
    Chapter queryDetailContentByBookId(String bookId);

    @Select("  select  id,file_path filePath,count,book_id bookId,name   from  t_chater  where  id = #{chapterId}  limit 1  ")
    Chapter queryDetailContentByChapterId(String chapterId);

    //    @Select("  select  id,file_path,count  from  t_chater  where  book_id = #{bookId} and id = #{chapterId} ")
//    Chapter queryDetailContentByBookId(String bookId, String chapterId);
    @Select("  select  id,file_path filePath,count,book_id bookId,name   from  t_chater  where  book_id = #{bookId} order by count asc  ")
    List<Chapter> queryAllDetails(String bookId);

}
