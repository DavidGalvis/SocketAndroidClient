package com.socket.david.socketandroidclient;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Class Adapter to handle recyclerView
 *
 * This class implements custom adapter to handle data received from server
 * and shows it to custom viewGroup
 *
 * @author David Galvis
 */
public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentViewHolder> {
    private List<Document> documents;

    // Provide a reference to the views for each data item
    public static class DocumentViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPrice;

        public DocumentViewHolder(View itemView) {
            super(itemView);
            this.tvName = (TextView)itemView.findViewById(R.id.text_item_name);
            this.tvPrice = (TextView)itemView.findViewById(R.id.text_item_price);
        }
    }

    public DocumentsAdapter(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public DocumentViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // creating a new view object
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);

        DocumentViewHolder dvh = new DocumentViewHolder(v);

        return dvh;
    }

    // Replacing the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DocumentViewHolder holder, int position) {
        /*
        * getting element from the data list and
        * replacing the contents of the view with that element
        */
        holder.tvName.setText(documents.get(position).name);
        holder.tvPrice.setText(documents.get(position).price);

    }

    /**
     * Method that returns the size of the data list (invoked by the layout manager)
     *
     * @return the size of data list that populates the recyclerView
     */
    @Override
    public int getItemCount() {
        return documents.size();
    }

}


