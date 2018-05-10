package com.an.got.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.an.got.R;
import com.an.got.databinding.AnswerListItemBinding;
import com.an.got.model.Answer;
import com.an.got.utils.AnimationUtils;

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
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AnswerListItemBinding itemBinding = AnswerListItemBinding.inflate(layoutInflater, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(itemBinding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Answer answer = answers.get(position);
        holder.binding.answerTxt.setText(String.format("%s.  %s", String.valueOf(position+1), answer.getAnswerDesc()));
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public Answer getAnswer(int position) {
        return answers.get(position);
    }

    public void updateWrongAnswerResponse(RecyclerView recyclerView, int position) {
        AnswerListItemBinding itemBinding = ((AnswerListAdapter.CustomViewHolder)recyclerView.findViewHolderForLayoutPosition(position)).getBinding();
        itemBinding.answerTxt.setTextColor(ContextCompat.getColor(context, R.color.game_four_question_txt));
        AnimationUtils.getInstance().animateStrikeThrough(itemBinding.answerTxt);
    }

    public void updateCorrectAnswerResponse(RecyclerView recyclerView, int position) {
        AnswerListItemBinding itemBinding = ((AnswerListAdapter.CustomViewHolder)recyclerView.findViewHolderForLayoutPosition(position)).getBinding();
        AnimationUtils.getInstance().animateCorrectResponse(itemBinding.answerTxt, ContextCompat.getColor(context, R.color.correct_response_color));
    }

    public void clear() {
        if(answers != null && !answers.isEmpty()) {
            answers.removeAll(answers);
            notifyDataSetChanged();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        private final AnswerListItemBinding binding;

        public CustomViewHolder(AnswerListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public AnswerListItemBinding getBinding() {
            return binding;
        }
    }
}
