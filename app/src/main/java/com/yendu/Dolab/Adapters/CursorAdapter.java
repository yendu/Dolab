package com.yendu.Dolab.Adapters;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;

import androidx.recyclerview.widget.RecyclerView;

public abstract class CursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
     private Context context;
     private Cursor cursor;
     private boolean dataValid;
     private int rowIdColumn;
     private DataSetObserver dataSetObserver;

     public CursorAdapter(Context mContext,Cursor mCursor){
         context=mContext;
         cursor=mCursor;
         dataValid=cursor!=null;
         rowIdColumn=dataValid ? cursor.getColumnIndex("_id"):-1;
//         dataSetObserver=new ContentObserver();
     }
     public Cursor getCursor(){
         return cursor;
     }

    @Override
    public int getItemCount() {
        try{
            if(dataValid && cursor!=null){
//
                try{
                    return cursor.getCount();


                }catch (Exception e){
                    return 0;

                }

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return 0;
    }

    @Override
    public long getItemId(int position) {
        if(dataValid&&cursor !=null && cursor.moveToPosition(position)){
            return cursor.getLong(rowIdColumn);
        }
        return 0;
    }
    public abstract void onBindViewHolder(VH holder, Cursor cursor,int position);
    @Override
    public void onBindViewHolder(VH holder, int position) {

        if (!dataValid) {
            throw new IllegalStateException("Cannot bind view holder when cursor is in invalid state.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to bind view holder");
        }

        onBindViewHolder(holder, cursor,position);
    }


    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }
    public Cursor getItem(int position) {
        if (!dataValid) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to get an item id");
        }
        return cursor;
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
//        final Cursor oldCursor = cursor;
//        if (oldCursor != null && mDataSetObserver != null) {
//            oldCursor.unregisterDataSetObserver(mDataSetObserver);
//        }

        Cursor old=cursor;

        cursor = newCursor;
        if (newCursor != null) {
//            if (mDataSetObserver != null) {
//                mCursor.registerDataSetObserver(mDataSetObserver);
//            }

            rowIdColumn = newCursor.getColumnIndex("_id");
            dataValid = true;
            notifyDataSetChanged();
//            return cursor;
        } else {

            rowIdColumn = -1;
            dataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return old;


    }
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }


}
