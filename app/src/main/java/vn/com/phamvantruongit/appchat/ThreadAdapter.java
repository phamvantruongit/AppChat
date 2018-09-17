package vn.com.phamvantruongit.appchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import vn.com.phamvantruongit.appchat.model.Message;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {
    private int userId;
    private Context context;
    private int SELF=786;
    private ArrayList<Message> messages;

    public ThreadAdapter( Context context, ArrayList<Message> messages ,int userId) {
        this.userId = userId;
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(message.getUsersId()==userId){
            return SELF;
        }
        return position;
    }

    @NonNull
    @Override
    public ThreadAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if(viewType==SELF){
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_thread,parent,false);
        }else {
            itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_thread_other,parent,false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadAdapter.ViewHolder viewHolder, int position) {
        Message message=messages.get(position);
        viewHolder.txtMessage.setText(message.getMessage());
        viewHolder.txtTime.setText(message.getName() + ","+ message.getSentAt());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMessage,txtTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage=itemView.findViewById(R.id.textViewMessage);
            txtTime=itemView.findViewById(R.id.textViewTime);
        }
    }
}
