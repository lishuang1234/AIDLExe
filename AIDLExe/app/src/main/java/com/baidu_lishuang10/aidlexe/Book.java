package com.baidu_lishuang10.aidlexe;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by baidu_lishuang10 on 15/11/20.
 */
public class Book implements Parcelable {
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", bookName='" + bookName + '\'' +
                '}';
    }

    public int bookId;
    public String bookName;

    public Book(int bookId, String bookName) {
        this.bookId = bookId;
        this.bookName = bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(bookId);
        out.writeString(bookName);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    private Book(Parcel in) {
        this.bookId = in.readInt();
        this.bookName = in.readString();

    }


}
