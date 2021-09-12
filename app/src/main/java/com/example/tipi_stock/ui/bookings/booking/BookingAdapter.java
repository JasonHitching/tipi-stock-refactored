package com.example.tipi_stock.ui.bookings.booking;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tipi_stock.R;
import com.example.tipi_stock.backend.bookings.data.Booking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter class used to create ViewHolder objects as required
 * and bind the required data to said view holder objects
 *
 * Select functionality from Android Jetpack library classes are utilised
 * to achieve some of the required functionality for this class:
 *      https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView
 *      https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.Adapter
 *      https://developer.android.com/reference/android/app/AlertDialog.Builder?hl=en
 */
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Booking> bookingData;
    private final LayoutInflater layoutInflater;
    private final OnBookingClickListener onBookingClickListener;

    public BookingAdapter(Context context, OnBookingClickListener onBookingClickListener) {
        // Create a layout inflater from the instantiating fragments context
        layoutInflater = LayoutInflater.from(context);
        bookingData = Collections.emptyList();
        this.onBookingClickListener = onBookingClickListener;
    }

    /**
     * Overridden method for creating and initialising a ViewHolder and related View
     *
     * @param parent new views added to the parent ViewGroup
     * @param viewType view type of new view
     * @return
     */
    @NonNull
    @Override
    public final ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(
                R.layout.booking_card_view, parent, false);
        return new ViewHolder(itemView, onBookingClickListener );
    }

    @Override
    public final void onBindViewHolder(@NonNull BookingAdapter.ViewHolder cardHolder, int position) {
        // If the booking data exists
        if (bookingData != null ) {
            cardHolder.setRowData(bookingData.get(position));
        }

        // Bind the current ViewHolder with an on click listener that removes the item
        cardHolder.binButton.setOnClickListener(view -> {

            // Obtain the view context to be used to display the dialog
            Context viewContext = view.getRootView().getContext();

            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(viewContext);
            deleteDialog.setMessage("Are you sure you want to delete this booking?");
            deleteDialog.setPositiveButton("Confirm", (dialogInterface, i) -> {
                List<Booking> newList = new ArrayList<>(bookingData);
                newList.remove(cardHolder.getAbsoluteAdapterPosition());
                setData(newList);
            });

            deleteDialog.setNegativeButton("Cancel", (dialogInterface, i) -> {

            });

            deleteDialog.create().show();
        });
    }


    /**
     * Accessor for obtaining the number of elements
     * @return size of dataset
     */
    @Override
    public final int getItemCount() {
        // initially booking data will be null until it updates from the database
        if (bookingData != null) {
            return bookingData.size();
        } else {
            return 0;
        }
    }

    /**
     * ViewHolder class provides a wrapper for around a View, contains layout for individual
     * items in the list
     *
     * https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView.ViewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final OnBookingClickListener bookingsClickListener;

        final TextView bookingTextView;
        final TextView customerTextView;
        final TextView customerAddText;
        final TextView dateStartTextView;
        final ImageButton binButton;

        /**
         * ViewHolder constructor
         * @param itemView individual row item view
         */
        public ViewHolder(@NonNull View itemView, OnBookingClickListener onBookingClickListener) {
            super(itemView);

            // Obtain the required view from recycler_row.xml
            bookingTextView = itemView.findViewById(R.id.structure_type_text);
            customerTextView = itemView.findViewById(R.id.customer_text);
            dateStartTextView = itemView.findViewById(R.id.start_date_text);
            customerAddText = itemView.findViewById(R.id.customer_add_text);
            bookingsClickListener = onBookingClickListener;
            binButton = itemView.findViewById(R.id.bin_button);
            itemView.setOnClickListener(this);
        }

        @Override
        public final void onClick(View view) {
            bookingsClickListener.onBookingClicked(getAbsoluteAdapterPosition());
        }

        /**
         * Set the booking item data
         * @param booking booking object passed when a ViewHolder binds
         */
        public final void setRowData(Booking booking) {
            // Set the text of all required views
            bookingTextView.setText(booking.getStructureType());
            customerTextView.setText("Customer name: " + booking.getCustomerFirstName() +
                    " " + booking.getCustomerLastName());
            customerAddText.setText("Address: " + booking.getCustomerAddress());
            dateStartTextView.setText("Date: " + booking.getBookingStartDate().getDayOfMonth()
                            + " - " + booking.getBookingStartDate().getMonth()
                            + " - " + booking.getBookingStartDate().getYear());
        }
    }

    /**
     * Interface to send item clicked psoition to fragment
     */
    public interface OnBookingClickListener {
        void onBookingClicked(int position);
    }

    public final void setData(List<Booking> newBookingList) {
        BookingDiffUtil bookingDiffUtil = new BookingDiffUtil(bookingData, newBookingList);
        DiffUtil.DiffResult diffUtilResults = DiffUtil.calculateDiff(bookingDiffUtil);
        diffUtilResults.dispatchUpdatesTo(this);
        bookingData = newBookingList;
    }
}
