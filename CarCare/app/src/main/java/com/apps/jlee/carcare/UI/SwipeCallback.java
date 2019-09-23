package com.apps.jlee.carcare.UI;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.apps.jlee.carcare.Adapters.GasAdapter;
import com.apps.jlee.carcare.Fragments.GasFragment;
import com.apps.jlee.carcare.R;
/*The entire class is a callback class that will be used by ItemTouchHelpter when the user interacts with the RecycleView*/
public class SwipeCallback extends ItemTouchHelper.SimpleCallback
{
    private GasFragment g;
    private Drawable delete_icon, edit_icon;
    private ColorDrawable background;

    public SwipeCallback(GasFragment g)
    {
        //In super, we are disabling up and down swipes by passing 0 and enabling left and right swipes
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.g = g;
        delete_icon = ContextCompat.getDrawable(g.getContext(), R.drawable.trash);
        edit_icon = ContextCompat.getDrawable(g.getContext(), R.drawable.edit);
        background = new ColorDrawable(Color.rgb(220,20,60));
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
    {
        // used for up and down movements
        return false;
    }

    /*Called when user swipes a viewholder*/
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        int position = viewHolder.getAdapterPosition();

        if(direction == 4)
            g.deleteItem(position);
        else
            g.edit(position);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
    {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

        int iconMargin = (itemView.getHeight() - delete_icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - delete_icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + delete_icon.getIntrinsicHeight();

        if (dX > 0)
        { // Swiping to the right
            background.setColor(Color.rgb(50,205,50));
            int iconLeft = itemView.getLeft() + iconMargin + edit_icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            edit_icon.setBounds(iconRight, iconTop, iconLeft, iconBottom);
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            background.draw(c);
            edit_icon.draw(c);
        }
        else if (dX < 0)
        { // Swiping to the left
            background.setColor(Color.rgb(220,20,60));
            int iconLeft = itemView.getRight() - iconMargin - delete_icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            delete_icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);
            delete_icon.draw(c);
        }
        else
        {   // view is unSwiped
            background.setBounds(0, 0, 0, 0);
            background.draw(c);
        }
    }
}
