package com.example.cryptomarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptomarket.R;
import com.example.cryptomarket.models.QuestionAnswer;

import java.util.List;

public class QuestionAnswerAdapter extends RecyclerView.Adapter<QuestionAnswerAdapter.QuestionAnswerViewHolder> {

    private List<QuestionAnswer> data;

    public QuestionAnswerAdapter(List<QuestionAnswer> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public QuestionAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question_answer, parent, false);
        return new QuestionAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAnswerViewHolder holder, int position) {
        QuestionAnswer questionAnswer = data.get(position);
        holder.question.setText(questionAnswer.getQuestion());
        holder.answer.setText(questionAnswer.getAnswer());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class QuestionAnswerViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        TextView answer;

        public QuestionAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
        }
    }
}