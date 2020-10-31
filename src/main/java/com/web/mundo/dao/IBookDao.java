package com.web.mundo.dao;


import com.web.mundo.po.Book;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;
import java.util.List;


public interface IBookDao {

    /**
     * 保存图书
     * @param book
     */
    @Insert(" insert into  t_book " +
            "(id,name,url,author,introduction,type,create_time,update_time) " +
            "values " +
            "(#{id},#{name},#{url},#{author},#{introduction},#{type},#{createTime},#{updateTime})  ")
    void saveBook(Book book);

    /**
     * 根据图书名和作者搜索图书
     * @param bookName
     * @param author
     * @return
     */
    @Select(" select id from  t_book where name = #{name} and author = #{author} limit 1 ")
    Book fondOneByNameAndAuthor(@Param("name") String bookName, @Param("author") String author);

    /**
     * 根据id更新图书下载时间
     * @param updateDate
     * @param id
     */
    @Update(" update t_book set update_time = #{update_time}  where id = #{id} ")
    void updateBoodById(@Param("update_time") Date updateDate, @Param("id") String id);


    /**
     * 查询推荐图书
     * @param count  指定数量，查询几本
     * @return
     */
    @Select(" select id,name,type,author,introduction from t_book limit #{count}")
    List<Book> queryAllRecommedBooks(@Param("count") int count);

    @Select(" select id from t_book where name = #{bookName} ")
    String queryBookIdByBookName(@Param("bookName") String bookName);

    @Select(" select id,name,author,introduction  from  t_book where id = #{id}  limit 1  ")
    Book queryBookById(String id);

}
