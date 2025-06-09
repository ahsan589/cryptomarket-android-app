package com.example.cryptomarket.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptomarket.R;
import com.example.cryptomarket.models.Section;

import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    private List<Section> sections;

    public SectionAdapter(List<Section> sections) {
        this.sections = sections;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        Section section = sections.get(position);
        holder.sectionTitle.setText(section.getTitle());
        holder.sectionIcon.setImageResource(getIconResource(section.getIcon()));

        // Set up nested RecyclerView for questions and answers
        QuestionAnswerAdapter questionAnswerAdapter = new QuestionAnswerAdapter(section.getData());
        holder.questionAnswerRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.questionAnswerRecyclerView.setAdapter(questionAnswerAdapter);

        // Toggle visibility of questions and answers
        holder.itemView.setOnClickListener(v -> {
            if (holder.questionAnswerRecyclerView.getVisibility() == View.GONE) {
                holder.questionAnswerRecyclerView.setVisibility(View.VISIBLE);
                holder.expandCollapseButton.setImageResource(R.drawable.ic_caret_up); // Change to up arrow
            } else {
                holder.questionAnswerRecyclerView.setVisibility(View.GONE);
                holder.expandCollapseButton.setImageResource(R.drawable.ic_caret_down); // Change to down arrow
            }
        });

        // Set initial state
        holder.questionAnswerRecyclerView.setVisibility(View.GONE);
        holder.expandCollapseButton.setImageResource(R.drawable.ic_caret_down);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        ImageView sectionIcon;
        TextView sectionTitle;
        ImageButton expandCollapseButton;
        RecyclerView questionAnswerRecyclerView;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionIcon = itemView.findViewById(R.id.sectionIcon);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            expandCollapseButton = itemView.findViewById(R.id.expandCollapseButton);
            questionAnswerRecyclerView = itemView.findViewById(R.id.questionAnswerRecyclerView);
        }
    }

    private int getIconResource(String iconName) {
        switch (iconName) {
            case "mobile-alt":
                return R.drawable.appl;
            case "book-open":
                return R.drawable.ic_crypt;
            case "lightbulb":
                return R.drawable.ic_feed;
            default:
                return R.drawable.ic_crypt; // Default icon
        }
    }
}