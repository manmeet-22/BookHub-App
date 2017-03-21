package com.androidexample.bookhub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by MANI on 2/6/2017.
 */

public class BooksAdapter extends ArrayAdapter<Books> {
//    private static final String LOCATION_SEPARATOR = " of ";

    public BooksAdapter(Context context, List<Books> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.books_list_item, parent, false);
        }
        // Find the book at the given position in the list of books
        Books currentBook = getItem(position);

        // Find the TextView with view ID bookTitle
        TextView titleView = (TextView) listItemView.findViewById(R.id.bookTitle);
        String title = currentBook.getTitle();
        // Display the Title of the current book in that TextView
        titleView.setText(title);

        TextView authorView = (TextView) listItemView.findViewById(R.id.bookAuthors);
        String author = currentBook.getAuthor();
        authorView.setText(author);

        TextView dopView = (TextView) listItemView.findViewById(R.id.bookPublishDate);
        String dop = currentBook.getPublishDate();
        dopView.setText(dop);

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.bookImageThumb);
        Picasso.with(getContext()).load(currentBook.getImage()).into(imageView);

        return listItemView;
    }
}
