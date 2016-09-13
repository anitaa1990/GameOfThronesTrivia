package com.an.got.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.an.got.R;
import com.an.got.model.Answer;
import com.an.got.views.TypeWriter;

import java.util.List;

public class AnswerListAdapter extends RecyclerView.Adapter<AnswerListAdapter.CustomViewHolder> {

    private Context context;
    private List<Answer> answers;

    public AnswerListAdapter(Context context, List<Answer> answers) {
        this.context = context;
        this.answers = answers;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Answer answer = answers.get(position);
        holder.answerText.setText(String.format("%s.  %s", String.valueOf(position+1), answer.getAnswerDesc()));
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public Answer getAnswer(int position) {
        return answers.get(position);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private TypeWriter answerText;

        public CustomViewHolder(View convertView) {
            super(convertView);
            this.answerText = (TypeWriter) convertView.findViewById(R.id.answerTxt);
        }
    }
}
