package com.aspengrades.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspengrades.data.Assignment;
import com.aspengrades.data.AssignmentList;
import com.aspengrades.data.Category;
import com.aspengrades.data.ClassInfo;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {

    private ClassInfo classInfo;
    private Context context;

    public CategoryAdapter(ClassInfo classInfo, Context context){
        this.classInfo = classInfo;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_category, viewGroup, false);
        return new CategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int i) {
        holder.layoutAssignments.removeAllViews();
        Category category = classInfo.getCategoryList().get(i);
        AssignmentList assignments = classInfo.fromCategory(category.getName());

        holder.textCategory.setText(category.getName());
        holder.textCategoryGrade.setText(category.getGrade() == -1 ? "" : Float.toString(category.getGrade()));
        holder.textWeight.setText(context.getString(R.string.text_weight, Float.toString(category.getWeight())));

        if(assignments.size() != 0) holder.textNoAssignments.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(context);
        for(Assignment assignment : assignments){
            View assignmentView = inflater.inflate(R.layout.assignment, holder.layoutAssignments, false);
            TextView textAssignment = assignmentView.findViewById(R.id.text_assignment);
            textAssignment.setText(assignment.getName());
            TextView textGrade = assignmentView.findViewById(R.id.text_assignment_grade);
            textGrade.setText(assignment.getScore().split(" ")[0]);
            holder.layoutAssignments.addView(assignmentView);
        }
    }

    @Override
    public int getItemCount() {
        return classInfo.getCategoryList().size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder {

        TextView textCategory, textWeight, textCategoryGrade, textNoAssignments;
        LinearLayout layoutAssignments;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.text_category);
            textWeight = itemView.findViewById(R.id.text_weight);
            textCategoryGrade = itemView.findViewById(R.id.text_category_grade);
            layoutAssignments = itemView.findViewById(R.id.layout_assignments);
            textNoAssignments = itemView.findViewById(R.id.text_no_assignments);
        }
    }
}
