package com.example.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calculclau.R;
import com.example.models.Module;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private final List<Module> moduleList;

    public ModuleAdapter(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.module_item, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        holder.moduleName.setText(module.getName());
        holder.moduleCoef.setText("Coefficient: " + module.getCoefficient());

        // Remove previous text watchers to avoid callback loops
        if (holder.tdInput.getTag() instanceof TextWatcher) {
            holder.tdInput.removeTextChangedListener((TextWatcher) holder.tdInput.getTag());
        }
        if (holder.tpInput.getTag() instanceof TextWatcher) {
            holder.tpInput.removeTextChangedListener((TextWatcher) holder.tpInput.getTag());
        }
        if (holder.examInput.getTag() instanceof TextWatcher) {
            holder.examInput.removeTextChangedListener((TextWatcher) holder.examInput.getTag());
        }

        // Set existing values to EditText fields
        if (module.getTdScore() > 0) {
            holder.tdInput.setText(String.format("%.0f", module.getTdScore()));
        } else {
            holder.tdInput.setText("");  // Clear if zero
        }

        if (module.getTpScore() > 0) {
            holder.tpInput.setText(String.format("%.0f", module.getTpScore()));
        } else {
            holder.tpInput.setText("");
        }

        if (module.getExamScore() > 0) {
            holder.examInput.setText(String.format("%.0f", module.getExamScore()));
        } else {
            holder.examInput.setText("");
        }

        // Set up text watchers for input fields
        TextWatcher tdWatcher = setupTextWatcher(holder.tdInput, module, 0);
        TextWatcher tpWatcher = setupTextWatcher(holder.tpInput, module, 1);
        TextWatcher examWatcher = setupTextWatcher(holder.examInput, module, 2);

        // Store text watchers as tags to remove them later
        holder.tdInput.setTag(tdWatcher);
        holder.tpInput.setTag(tpWatcher);
        holder.examInput.setTag(examWatcher);
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    // Method to set up the TextWatcher and input validation
    private TextWatcher setupTextWatcher(EditText editText, final Module module, final int type) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();

                // If input is empty, reset background color and do nothing
                if (inputText.isEmpty()) {
                    editText.setBackgroundColor(editText.getContext().getResources().getColor(android.R.color.darker_gray));
                    return;
                }

                // Check if the input is a valid number
                try {
                    double value = Double.parseDouble(inputText);

                    // If the value is within the valid range (0 to 20)
                    if (value >= 0 && value <= 20) {
                        // Valid input: reset background color
                       if (value >= 0 && value <= 9){
                           editText.setBackgroundColor(editText.getContext().getResources().getColor(android.R.color.holo_red_light));

                       }else{
                           editText.setBackgroundColor(editText.getContext().getResources().getColor(android.R.color.holo_green_light));

                       }

                        // Update the module score based on input type
                        switch (type) {
                            case 0:
                                module.setTdScore(value);
                                break;
                            case 1:
                                module.setTpScore(value);
                                break;
                            case 2:
                                module.setExamScore(value);
                                break;
                        }
                    } else {
                        // Invalid input: highlight the field

                        editText.setBackgroundColor(editText.getContext().getResources().getColor(android.R.color.holo_red_light));
                    }
                } catch (NumberFormatException e) {
                    // If the input is not a valid numbe, highlight the field


                    editText.setBackgroundColor(editText.getContext().getResources().getColor(android.R.color.holo_red_light));
                }
            }
        };

        editText.addTextChangedListener(watcher);
        return watcher;
    }

    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        TextView moduleName;
        TextView moduleCoef;
        EditText tdInput;
        EditText tpInput;
        EditText examInput;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.module_name);
            moduleCoef = itemView.findViewById(R.id.module_coefficient);
            tdInput = itemView.findViewById(R.id.td_input);
            tpInput = itemView.findViewById(R.id.tp_input);
            examInput = itemView.findViewById(R.id.exam_input);
        }
    }
}