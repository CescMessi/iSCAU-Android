package cn.scau.scautreasure.api;

import org.androidannotations.annotations.rest.Get;
import org.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import cn.scau.scautreasure.model.BookDetailModel;
import cn.scau.scautreasure.model.BookModel;

/**
 * 图书馆Api;
 * User:  Special Leung
 * Date:  13-7-16
 * Time:  下午9:35
 * Mail:  specialcyci@gmail.com
 */

@Rest(rootUrl = "http://mywpserver.sinaapp.com/index.php?s=lib/", converters = {GsonHttpMessageConverter.class})
public interface LibraryApi {

    @Get("search/{title}/{page}")
    BookModel.BookList searchBook(String title, int page);

    @Get("book/{address}")
    BookDetailModel.DetailList getBookDetail(String address);

    @Get("list/now/{userName}/{passWord}")
    BookModel.BookList getNowBorrowedBooks(String userName, String passWord);

    @Get("list/history/{userName}/{passWord}")
    BookModel.BookList getHistoryBorrowedBooks(String userName, String passWord);

    @Get("renew/{userName}/{passWord}/{barCode}/{checkCode}")
    void reNewBook(String userName, String passWord, String barCode, String checkCode);

}
