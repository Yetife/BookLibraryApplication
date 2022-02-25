package com.example.bookapplication.service;

import com.example.bookapplication.data.model.Book;
import com.example.bookapplication.data.model.BookUser;
import com.example.bookapplication.data.model.UserWishList;
import com.example.bookapplication.data.repository.BookRepository;
import com.example.bookapplication.data.repository.BookUserRepository;
import com.example.bookapplication.data.repository.WishListRepository;
import com.example.bookapplication.dto.BookUserRequestDto;
import com.example.bookapplication.dto.BookUserResponseDto;
import com.example.bookapplication.web.exception.BookDoesNotExistException;
import com.example.bookapplication.web.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookUserListServiceImpl implements BookUserListService {

    @Autowired
    WishListRepository wishListRepository;

    @Autowired
    BookUserRepository bookUserRepository;

    @Autowired
    BookRepository bookRepository;

    @Override
    public BookUserResponseDto addBookTowishList(BookUserRequestDto wishListRequestDto) throws UserNotFoundException, BookDoesNotExistException {

        Optional<BookUser> query = bookUserRepository.findById(wishListRequestDto.getUserId());
        if (query.isEmpty()){
            throw new UserNotFoundException("User with id" + wishListRequestDto.getUserId() + "does not exist");
        }
        BookUser existingUser = query.get();
        UserWishList myWishList = existingUser.getWishList();
        //check if book exist
        Book book = bookRepository.findBookByTitle("Think big").orElse(null);
        if (book == null) {
            throw new BookDoesNotExistException("Book with title"+ wishListRequestDto.getTitle() + "does not exist");
        }
        myWishList.addBookToList(book);
        wishListRepository.save(myWishList);
        return buildCartResponse(myWishList);
    }

    private BookUserResponseDto buildCartResponse(UserWishList wishList) {
        return BookUserResponseDto.builder().bookList(wishList.getFavoriteBookList()).build();
    }


    @Override
    public BookUserResponseDto viewWishList(Long userId) throws UserNotFoundException {
        BookUser bookUser = bookUserRepository.findById(userId).orElse(null);
        if (bookUser == null) {
            throw new UserNotFoundException("User with id" + userId + "does not exist");
        }
        UserWishList wishList = bookUser.getWishList();
        return buildCartResponse(wishList);
    }
}