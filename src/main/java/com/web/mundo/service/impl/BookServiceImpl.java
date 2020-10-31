package com.web.mundo.service.impl;

import com.web.mundo.dao.IBookDao;
import com.web.mundo.po.Book;
import com.web.mundo.service.IBookService;
import com.web.mundo.vo.BookVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImpl implements IBookService {

    @Autowired
    private IBookDao bookDao;

    @Override
    public List<BookVO> queryRecommendBooks() {
        List<Book> books = bookDao.queryAllRecommedBooks(5);
        List<BookVO> bookVos = new ArrayList<>();
        if (books!= null && !books.isEmpty()) {
            int ranking = 1;
            for (Book book : books) {
                BookVO bookVO = new BookVO();
                bookVO.setAuthor(book.getAuthor());
                bookVO.setId(book.getId());
                bookVO.setIntroduction(book.getIntroduction());
                bookVO.setName(book.getName());
                bookVO.setType(book.getType());
                bookVO.setRanking(ranking+"");
                bookVos.add(bookVO);
                ranking++;
            }
            return bookVos;
        }
        return new ArrayList<>();
    }

    @Override
    public String queryBookIdByBookNameAndAuthor(String bookName, String author) {
        return bookDao.queryBookIdByBookName(bookName);
    }

}
