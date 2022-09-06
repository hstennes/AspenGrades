package com.aspengrades.main;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aspengrades.data.Assignment;
import com.aspengrades.data.AssignmentList;
import com.aspengrades.data.Category;
import com.aspengrades.data.ClassInfo;
import com.aspengrades.data.SchoolClass;
import com.aspengrades.util.ColorUtil;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ClassInfo classInfo;
    private Context context;

    private boolean percentMode = false;

    public CategoryAdapter(ClassInfo classInfo, Context context){
        this.classInfo = classInfo;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.card_class_header, viewGroup, false);
            return new HeaderHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.card_category, viewGroup, false);
        return new CategoryHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int i) {
        if(h.getItemViewType() == 0) {
            CategoryHolder holder = (CategoryHolder) h;
            initializeLayout(holder);
            Category category = classInfo.getCategoryList().get(i - 1);
            holder.textCategory.setText(category.getName());
            holder.textCategoryGrade.setText(category.getGrade() == SchoolClass.BLANK_GRADE ? "" : Float.toString(category.getGrade()));
            holder.layoutHeader.getBackground().setColorFilter(ColorUtil.colorFromGrade(context, category.getGrade()), PorterDuff.Mode.SRC);

            holder.layoutDetails.setVisibility(View.VISIBLE);
            holder.textWeight.setText(context.getString(R.string.text_weight, category.getWeight()));
            AssignmentList assignments = classInfo.fromCategory(category.getName());
            if (assignments.size() == 0) return;

            holder.textNoAssignments.setVisibility(View.GONE);
            LayoutInflater inflater = LayoutInflater.from(context);
            for (Assignment assignment : assignments) setupAssignment(assignment, holder, inflater);
        }
        else {
            HeaderHolder holder = (HeaderHolder) h;

            String teacher = classInfo.getTeacher();
            if(teacher != null && !teacher.equals("")) {
                holder.textTeacher.setVisibility(View.VISIBLE);
                holder.textTeacher.setText(context.getString(R.string.text_teacher, classInfo.getTeacher(), classInfo.getCategoryList().getTeacherEmail()));
            }
            else holder.textTeacher.setVisibility(View.GONE);
            setTextIfPresent(holder.textSchedule, R.string.text_schedule, classInfo.getSchedule());
            setTextIfPresent(holder.textClssrm, R.string.text_clssrm, classInfo.getClssrm());

            float cumulativeGrade = classInfo.getCategoryList().getCumulativeGrade();
            holder.textCategoryGrade.setText(cumulativeGrade == SchoolClass.BLANK_GRADE ? "--" : Float.toString(cumulativeGrade));
            holder.layoutHeader.getBackground().setColorFilter(ColorUtil.colorFromGrade(context, cumulativeGrade), PorterDuff.Mode.SRC);
        }
    }

    private void setTextIfPresent(TextView view, int resource, String dataValue) {
        if(dataValue == null) view.setVisibility(View.GONE);
        else {
            view.setVisibility(View.VISIBLE);
            view.setText(context.getString(resource, dataValue));
        }
    }

    @Override
    public int getItemCount() {
        return classInfo.getCategoryList().size() + 1;
    }

    public void toggleViewType(){
        percentMode = !percentMode;
        notifyDataSetChanged();
    }

    private void initializeLayout(CategoryHolder holder){
        holder.layoutAssignments.removeAllViews();
        holder.textNoAssignments.setVisibility(View.VISIBLE);
        holder.layoutDetails.setVisibility(View.GONE);
    }

    private void setupAssignment(Assignment assignment, CategoryHolder holder, LayoutInflater inflater){
        View assignmentView = inflater.inflate(R.layout.assignment, holder.layoutAssignments, false);
        TextView textAssignment = assignmentView.findViewById(R.id.text_assignment);
        textAssignment.setText(assignment.getName());
        TextView textGrade = assignmentView.findViewById(R.id.text_assignment_grade);
        textGrade.setText(percentMode ? assignment.getPercentScore() : assignment.getFractionScore());
        holder.layoutAssignments.addView(assignmentView);
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {

        TextView textCategory, textWeight, textCategoryGrade, textNoAssignments;
        RelativeLayout layoutHeader;
        LinearLayout layoutAssignments, layoutDetails;

        public CategoryHolder(@NonNull View view) {
            super(view);
            textCategory = view.findViewById(R.id.text_category);
            textWeight = view.findViewById(R.id.text_weight);
            textCategoryGrade = view.findViewById(R.id.text_category_grade);
            textNoAssignments = view.findViewById(R.id.text_no_assignments);
            layoutHeader = view.findViewById(R.id.layout_header);
            layoutAssignments = view.findViewById(R.id.layout_assignments);
            layoutDetails = view.findViewById(R.id.layout_details);
        }
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        TextView textCategoryGrade, textTeacher, textSchedule, textClssrm;
        RelativeLayout layoutHeader;

        public HeaderHolder(@NonNull View view) {
            super(view);
            textCategoryGrade = view.findViewById(R.id.text_grade);
            textTeacher = view.findViewById(R.id.text_teacher);
            textSchedule = view.findViewById(R.id.text_schedule);
            textClssrm = view.findViewById(R.id.text_clssrm);
            layoutHeader = view.findViewById((R.id.layout_header));
        }
    }
}
