package jlee.app.cal.wampfirebasemessaging;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter
{
    private Context mContext;
    private List<UserMessage> mMessageList;

    public MessageListAdapter(Context context, List<UserMessage> messageList)
    {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount()
    {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position)
    {
        UserMessage message = mMessageList.get(position);

        if (message.getMessageType() == 1)
        {
            // If the current user is the sender of the message
            return UserMessage.MSG_TYPE_SENT;
        } else
        {
            // If some other user sent the message
            return UserMessage.MSG_TYPE_RECEIVED;
        }
    }

    // Creates a new ViewHolder object whenever the RecyclerView needs a new one.
    // A row layout is inflated, passed to the ViewHolder object and each child view can be found and stored
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;

        if (viewType == UserMessage.MSG_TYPE_SENT)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        }
        else if (viewType == UserMessage.MSG_TYPE_RECEIVED)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    // Passes the message object to a ViewHolder(SentMessageHolder/ReceivedMessageHolder) so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        UserMessage message = mMessageList.get(position);

        switch (holder.getItemViewType())
        {
            case UserMessage.MSG_TYPE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case UserMessage.MSG_TYPE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder
    {
        TextView messageText, timeText;

        SentMessageHolder(View itemView)
        {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(UserMessage message)
        {
            messageText.setText(message.getMessage());
            //Toast.makeText(mContext, message.getMessage(), Toast.LENGTH_LONG).show();
            //timeText.setText(DateUtils.formatDateTime(mContext,message.getCreatedAt(),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder
    {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView)
        {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(UserMessage message)
        {
            messageText.setText(message.getMessage());
            //timeText.setText(DateUtils.formatDateTime(mContext,message.getCreatedAt(),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE));
            //nameText.setText(message.getSender());

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }
}
