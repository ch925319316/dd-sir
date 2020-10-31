package com.web.mundo.service;

import com.web.mundo.vo.BookVO;

import java.util.List;

public interface IBookService {


    /**
     * 查询推荐的书
     * @return
     */
    List<BookVO> queryRecommendBooks();


    /**
     * 根据书名和作者查询图书Id
     * @param bookName
     * @param author
     * @return
     */
    String queryBookIdByBookNameAndAuthor(String bookName, String author);



}
